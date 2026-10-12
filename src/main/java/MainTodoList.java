import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MainTodoList extends JPanel {
    private DayofTodoManager todomanager;
    private JPanel listpanel;
    private List<JLabel> todos;

    private JLabel imglabel;

    public MainTodoList(DayofTodoManager todomanager) {
        this.todomanager = todomanager;
        this.todos = new ArrayList<>();

        initUI();
        daliyrefresh(LocalDate.now());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("오늘의 할 일"));

        JLabel title = new JLabel("오늘의 할 일", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        add(title, BorderLayout.NORTH);

        listpanel = new JPanel();
        listpanel.setLayout(new BoxLayout(listpanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listpanel);
        scrollPane.setBorder(null); // 바깥 테두리는 TitledBorder로 충분
        add(scrollPane, BorderLayout.CENTER);

        imglabel = new JLabel();
        imglabel.setHorizontalAlignment(SwingConstants.CENTER);
        imglabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        loadtodoimg();
        add(imglabel, BorderLayout.SOUTH);
    }

    private void loadtodoimg() {
        try{
            ImageIcon icon = new ImageIcon("fight15_1.png");
            Image img = icon.getImage();

            int width = 180;
            int height = (int)(width / 1.5);

            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imglabel.setIcon(new ImageIcon(scaled));
        }
        catch(Exception e){
            System.out.println("이미지 불러오기 오류");
        }
    }

    private void daliyrefresh(LocalDate date) {
        listpanel.removeAll();
        todos.clear();

        List<TodoItem> items = todomanager.getTodos();

        if (items == null || items.isEmpty()) {
            JLabel empty = new JLabel("오늘 할 일이 없습니다.", SwingConstants.CENTER);
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listpanel.add(Box.createVerticalStrut(5));
            listpanel.add(empty);
        }
        else {
            for (TodoItem item : items) {
                JLabel lbl = new JLabel("• " + item.getcontent());
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                listpanel.add(lbl);
                listpanel.add(Box.createVerticalStrut(3));
                todos.add(lbl);
            }
        }

        revalidate();
        repaint();
    }

    public void refreshToday() {
        daliyrefresh(LocalDate.now());
    }
}
