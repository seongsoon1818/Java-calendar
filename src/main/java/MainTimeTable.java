import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class MainTimeTable extends JPanel {
    private Timetable timetable;
    private JLabel[][] cells;

    private final String[] days = { " ", "월", "화"," 수", "목", "금"};
    private final String[] times = { "9시", "10시", "11시", "12시", "13시", "14시", "15시", "16시", "17시"};

    public MainTimeTable(Timetable timetable) {
        this.timetable = timetable;
        init();
        refresh();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("시간표"));

        JPanel dayHeaderPanel = new JPanel(new GridLayout(1, days.length));
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            dayHeaderPanel.add(label);
        }
        add(dayHeaderPanel, BorderLayout.NORTH);

        JPanel grid  = new JPanel(new GridLayout(times.length, days.length));
        cells = new JLabel[times.length][days.length];

        for (int r = 0; r < times.length; r++) {
            JLabel timeLabel = new JLabel(times[r], SwingConstants.CENTER);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            grid.add(timeLabel);
            cells[r][0] = timeLabel;

            for (int c = 1; c < days.length; c++) {
                JLabel cell = new JLabel(" ", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(Color.white);
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                grid.add(cell);
                cells[r][c] = cell;
            }
        }

        add(grid, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 500));
    }

    public void refresh() {
        int daycount = Math.min(timetable.getDay(), days.length - 1);
        int timecount = Math.min(timetable.getTimes(),  times.length);

        for (int r = 0; r < times.length; r++) {
            for (int c = 1; c <= daycount; c++) {
                JLabel cell = cells[r][c];
                if (r< timecount) {
                    Lecture lec = timetable.getLecture(c - 1, r);

                    if (lec != null) {
                        cell.setText(lec.getname());
                        cell.setBackground(new Color(204, 229, 255));
                    }
                    else {
                        cell.setText("");
                        cell.setBackground(Color.white);
                    }
                }
                else {
                    cell.setText("");
                    cell.setBackground(Color.white);
                }
            }
        }
    }

    public JLabel[][] getCells() { return cells; }
}
