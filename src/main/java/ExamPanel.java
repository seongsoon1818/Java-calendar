import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ExamPanel extends JPanel {
    private ExamManager exammanager;
    private JPanel bodyPanel;
    private List<JLabel> exams;

    public ExamPanel(ExamManager exammanager) {
        this.exammanager = exammanager;
        this.exams = new ArrayList();

        initUI();
        refresh();
    }

    public void initUI(){
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("시험 D-day"));

        JPanel header = new JPanel(new GridLayout(1, 3));
        header.add(makeHeaderLabel("과목명"));
        header.add(makeHeaderLabel("시험일"));
        header.add(makeHeaderLabel("D-day"));
        add(header, BorderLayout.NORTH);

        bodyPanel = new JPanel(new GridLayout(0,3,2,2));
        add(bodyPanel, BorderLayout.CENTER);
    }

    private JLabel makeHeaderLabel(String text){
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return label;
    }

    private JLabel makeCellLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return label;
    }

    public void refresh(){
        bodyPanel.removeAll();
        exams.clear();

        if (exammanager == null){
            revalidate();
            repaint();
            return;
        }

        Collection<ExamSchedule> schedules = exammanager.getAllExamSchedules();
        if (schedules == null || schedules.isEmpty()){
            JLabel empty = new JLabel("등록된 시험이 없습니다.", SwingConstants.CENTER);
            bodyPanel.setLayout(new BorderLayout());
            bodyPanel.add(empty, BorderLayout.CENTER);
            revalidate();
            repaint();
            return;
        }

        bodyPanel.setLayout(new GridLayout(0,3,2,2));
        LocalDate today = LocalDate.now();
        List<ExamSchedule> sorted = new ArrayList(schedules);
        sorted.sort(Comparator.comparing(ExamSchedule::getExamDate));

        for (ExamSchedule es : sorted) {
            Lecture lec = es.getLecture();
            LocalDate examDate = es.getExamDate();
            if (lec == null || examDate == null) {
                continue;
            }
            int dday = es.getDday(today);
            String ddayText = formatDday(dday);

            JLabel nameLabel = makeCellLabel(lec.getname());
            JLabel dateLabel = makeCellLabel(examDate.toString()); // yyyy-MM-dd
            JLabel ddayLabel = makeCellLabel(ddayText);

            if (dday == 0) {
                ddayLabel.setForeground(Color.RED);
                ddayLabel.setFont(ddayLabel.getFont().deriveFont(Font.BOLD));
            }

            bodyPanel.add(nameLabel);
            bodyPanel.add(dateLabel);
            bodyPanel.add(ddayLabel);

            exams.add(nameLabel);
            exams.add(dateLabel);
            exams.add(ddayLabel);
        }

        revalidate();
        repaint();
    }

    private String formatDday(int diff) {
        if (diff == 0) {
            return "D-day";
        } else if (diff > 0) {
            return "D-" + diff;
        } else {
            return "D+" + Math.abs(diff);
        }
    }
}
