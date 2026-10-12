import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class ExamEditDialog extends JDialog {
    private ExamManager examManager;
    private Timetable timetable;

    private java.util.List<Lecture> lectures = new ArrayList<>();
    private Map<Lecture, JTextField> dataFileds = new HashMap<>();

    public ExamEditDialog(JFrame parent, ExamManager examManager, Timetable timetable) {
        super(parent, "시험 일정 수정", true);
        this.examManager = examManager;
        this.timetable = timetable;

        collectLecturesFromTimetable();
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void collectLecturesFromTimetable() {
        int daycount = timetable.getDay();
        int timecount = timetable.getTimes();

        Map<Integer, Lecture> map = new HashMap<>();

        for (int d = 0; d < daycount; d++) {
            for (int t = 0; t < timecount; t++) {
                Lecture lec = timetable.getLecture(d, t);
                if(lec != null && !map.containsKey(lec.getid())) {
                    map.put(lec.getid(), lec);
                }
            }
        }

        lectures = new ArrayList<>(map.values());
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel("형식: yyyy-MM-dd   (빈칸으로 두면 해당 과목 시험 일정 삭제)", SwingConstants.LEFT);
        add(info, BorderLayout.NORTH);

        if (lectures.isEmpty()) {
            JLabel emptyLabel = new JLabel("시간표에 등록된 과목이 없습니다.", SwingConstants.CENTER);
            add(emptyLabel, BorderLayout.CENTER);

            JButton closeButton = new JButton("닫기");
            closeButton.addActionListener(e -> dispose());
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(closeButton);
            add(bottom, BorderLayout.SOUTH);
            return;
        }

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JPanel gridPanel = new JPanel(new GridLayout(lectures.size() + 1, 2, 5, 5));

        gridPanel.add(makeHeaderLabel("과목명"));
        gridPanel.add(makeHeaderLabel("시험일 (yyyy-MM-dd)"));

        for (Lecture lec : lectures) {
            JLabel nameLabel = new JLabel(lec.getname());
            JTextField dataField = new JTextField(10);

            ExamSchedule es = examManager.getExamSchedule(lec);
            if (es != null && es.getExamDate() != null) {
                dataField.setText(es.getExamDate().toString());
            }

            gridPanel.add(nameLabel);
            gridPanel.add(dataField);

            dataFileds.put(lec, dataField);
        }

        centerPanel.add(gridPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("저장");
        JButton cancelButton = new JButton("취소");

        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> {
            SoundPlayer.playCancel();
            dispose();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel makeHeaderLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        return lbl;
    }

    private void onSave(ActionEvent e) {
        SoundPlayer.playConfirm();
        for (Lecture lec : lectures) {
            JTextField field = dataFileds.get(lec);
            if (field == null) {
                continue;
            }

            String text =  field.getText().trim();
            if (text.isEmpty()) {
                examManager.setExamDate(lec, null);
                continue;
            }
            else {
                try {
                    LocalDate date = LocalDate.parse(text);
                    examManager.setExamDate(lec, date);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(
                            this, lec.getname() + "의 날짜 형식이 잘못되었습니다.",
                            "날짜 형식 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        dispose();
    }
}
