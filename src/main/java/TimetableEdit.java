import javax.swing.*;
import java.awt.*;

public class TimetableEdit extends JDialog {

    private TimetableFrame parent;
    private Timetable timetable;
    private int dayIndex;
    private int timeIndex;

    private JTextField nameText;
    private JTextField professorText;
    private JTextField locationText;
    private JTextField durationText;

    private Lecture originalLecture;

    public TimetableEdit(TimetableFrame parent,Timetable timetable, int dayIndex, int timeIndex) {
        super(parent, "시간표 수정", true);
        this.parent = parent;
        this.timetable = timetable;
        this.timeIndex = timeIndex;
        this.dayIndex = dayIndex;

        this.originalLecture = timetable.getLecture(dayIndex, timeIndex);

        init();
        pack();
        setLocationRelativeTo(parent);
    }

    private void init() {
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4,2,5,5));

        form.add(new JLabel("과목명"));
        nameText = new JTextField();
        form.add(nameText);

        form.add(new JLabel("교수명:"));
        professorText = new JTextField();
        form.add(professorText);

        form.add(new JLabel("강의실:"));
        locationText = new JTextField();
        form.add(locationText);

        form.add(new JLabel("지속 시간(칸 수):"));
        durationText = new JTextField("1");
        form.add(durationText);

        add(form, BorderLayout.CENTER);

        if (originalLecture != null) {
            nameText.setText(originalLecture.getname());
            professorText.setText(originalLecture.getprofessor());
            locationText.setText(originalLecture.getlocation());
            durationText.setText(String.valueOf(originalLecture.getduration()));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");

        okButton.addActionListener(e -> {
            SoundPlayer.playConfirm();
            okchange();
        });
        cancelButton.addActionListener(e -> {
            SoundPlayer.playCancel();
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void okchange() {
        String name = nameText.getText().trim();
        String professor = professorText.getText().trim();
        String location = locationText.getText().trim();
        int duration = 1;

        try{
            duration = Integer.parseInt(durationText.getText().trim());
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "지속 시간은 숫자로 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "과목명을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (duration <= 0) {
            JOptionPane.showMessageDialog(this, "지속 시간은 1 이상이어야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id;
        if (originalLecture != null) {
            id = originalLecture.getid();
        }
        else {
            id = dayIndex * 1000 + timeIndex + 1;
        }

        Lecture newLecture = new Lecture(id, name, professor, location, duration);
        int timeCount = timetable.getTimes();
        for (int t = 0; t < timeCount; t++) {
            Lecture lec = timetable.getLecture(dayIndex, t);
            if (lec != null && lec == originalLecture) {
                timetable.clearLecture(dayIndex, t);
            }
        }

        timetable.setLecture(dayIndex, timeIndex, duration, newLecture);
        parent.refreshTable();
        dispose();
    }
}
