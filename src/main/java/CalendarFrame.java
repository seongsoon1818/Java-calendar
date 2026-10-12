import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarFrame extends JFrame {
    private DayofTodoManager todomanager;
    private LocalDate month;
    private List<CalendarDayPanel> daypanels = new ArrayList<>();

    public CalendarFrame(DayofTodoManager todomanager) {
        this.todomanager = todomanager;
        this.month = LocalDate.of(LocalDate.now().getYear(), 12, 1);

        setTitle("12월 캘린더");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        init();
        calendarrefresh();
    }

    public void init() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(month.getYear() + "년 " + month.getMonthValue() + "월", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        add(center, BorderLayout.CENTER);

        String[] weekDays = {"월", "화", "수", "목", "금", "토", "일"};
        JPanel header = new JPanel(new GridLayout(1, 7));
        for (String wd : weekDays) {
            JLabel lbl = new JLabel(wd, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            header.add(lbl);
        }
        center.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(5, 7, 3, 3));
        grid.setBorder(new TitledBorder("12월 할 일 캘린더"));
        center.add(grid, BorderLayout.CENTER);

        int daysInMonth = month.lengthOfMonth();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                int dayNumber = row * 7 + col +1;
                if (dayNumber <= daysInMonth) {
                    LocalDate date = month.withDayOfMonth(dayNumber);
                    CalendarDayPanel dayPanel = new CalendarDayPanel(date, todomanager);
                    daypanels.add(dayPanel);

                    CalendarAddListener addListener = new CalendarAddListener(this, dayPanel, todomanager);
                    dayPanel.addaddlistener(addListener);

                    grid.add(dayPanel);
                }
                else {
                    JPanel empty = new JPanel();
                    empty.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    grid.add(empty);
                }
            }
        }
    }

    public void calendarrefresh() {
        for (CalendarDayPanel p : daypanels) {
            p.itemrefresh();
        }
    }
}
