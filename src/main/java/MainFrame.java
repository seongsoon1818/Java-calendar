import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private DataManager datamanager;
    private MainbuttonPanel mainbuttonpanel;
    private MainTimeTable mainTimetable;
    private ExamPanel exampanel;
    private MainTodoList maintodolist;
    private JLabel clockLabel;

    private Thread clockThread;
    private volatile boolean clockRunning = false;

    public MainFrame(DataManager datamanager) {
        this.datamanager = datamanager;
        setTitle("시간표 & 스터디 플래너");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        init();
        startClockThread();
    }

    private void startClockThread() {
        clockRunning = true;
        clockThread = new Thread(() -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (clockRunning) {
                String text = LocalDateTime.now().format(fmt);
                SwingUtilities.invokeLater(() -> clockLabel.setText(text));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockThread.setDaemon(true); // 프로그램 종료 시 자동으로 같이 종료 (데몬스레드)
        clockThread.start();
    }

    public void init() {
        setLayout(new BorderLayout(10, 10));

        // 상단 버튼 패널 (리스너를 외부에서 주입)
        mainbuttonpanel = new MainbuttonPanel();

        MainbuttonListener btnListener = new MainbuttonListener(this, mainbuttonpanel);
        mainbuttonpanel.gettimetablebutton().addActionListener(btnListener);
        mainbuttonpanel.getcalendarbutton().addActionListener(btnListener);
        mainbuttonpanel.getexambutton().addActionListener(btnListener);

        // 중앙: 왼쪽 시간표, 오른쪽 오늘 할 일 + 시험 D-day + 시계
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        add(centerPanel, BorderLayout.CENTER);

        // 메인 시간표 패널 (MainTimeTable 재사용)
        mainTimetable = new MainTimeTable(datamanager.gettimetable());
        centerPanel.add(mainTimetable, BorderLayout.CENTER);

        // 오른쪽 영역
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setPreferredSize(new Dimension(380, 500));
        centerPanel.add(rightPanel, BorderLayout.EAST);

        // 상단: 시계
        JPanel clockPanel = new JPanel();
        clockPanel.setBorder(new LineBorder(Color.GRAY));
        clockPanel.setBackground(new Color(204, 255, 204));
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));

        JLabel clockTitle = new JLabel("현재 시각");
        clockTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        clockLabel = new JLabel("0000-00-00 00:00:00");
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        clockPanel.add(Box.createVerticalStrut(5));
        clockPanel.add(clockTitle);
        clockPanel.add(Box.createVerticalStrut(5));
        clockPanel.add(clockLabel);
        clockPanel.add(Box.createVerticalStrut(5));
        Dimension dim = clockPanel.getPreferredSize();
        if (dim == null) {
            dim = new Dimension(0,0);
        }
        dim.width = 380;
        clockPanel.setPreferredSize(dim);

        // 상단패널 : 이미지, 버튼, 시계
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(mainbuttonpanel, BorderLayout.CENTER);
        topPanel.add(clockPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙패널 : 오늘의 할 일
        maintodolist = new MainTodoList(datamanager.gettodomanager());
        rightPanel.add(maintodolist, BorderLayout.CENTER);

        // 하단패널 : 시험 D-day
        exampanel = new ExamPanel(datamanager.getexammanager());
        rightPanel.add(exampanel, BorderLayout.SOUTH);
    }

    // 시간표 조회/수정 프레임 열기 (버튼 1)
    public void gototimetable() {
        TimetableFrame tf = new TimetableFrame(datamanager.gettimetable());
        tf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                refresh();
            }

            public void  windowClosed(WindowEvent e) {
                refresh();
            }
        });
        tf.setVisible(true);
    }

    // 캘린더 조회/수정 프레임 열기 (버튼 2)
    public void gotocalendar() {
        CalendarFrame cf = new CalendarFrame(datamanager.gettodomanager());
        cf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                refresh();
            }

            public void  windowClosed(WindowEvent e) {
                refresh();
            }
        });
        cf.setVisible(true);
    }

    // 시험 일정 수정 다이얼로그 열기 (버튼 3)
    public void exameditdialog() {
        ExamEditDialog dialog = new ExamEditDialog(
                this,
                datamanager.getexammanager(),
                datamanager.gettimetable()
        );
        dialog.setVisible(true);  // 모달: 닫힐 때까지 대기

        // 시험 일정이 바뀌었을 수 있으니 D-day 갱신
        exampanel.refresh();
    }

    // 메인 화면 전체를 다시 그리는 용도 (외부에서 호출 가능)
    public void refresh() {
        mainTimetable.refresh();
        maintodolist.refreshToday();
        exampanel.refresh();
    }

    public void dispose() {
        clockRunning = false;
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataManager dm = new DataManager();
            MainFrame frame = new MainFrame(dm);

            // 창 닫힐 때 파일 저장
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dm.saveTimetable();
                    dm.saveTodo();
                    dm.saveExam();
                }
            });

            frame.setVisible(true);
        });
    }
}

class MainbuttonPanel extends JPanel {
    private JButton timetablebutton;
    private JButton calendarbutton;
    private JButton exambutton;
    private JLabel imglabel2;
    private JLabel imglabel1;

    public MainbuttonPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        timetablebutton = new JButton("시간표 수정");
        calendarbutton = new JButton("캘린더 보기 및 수정");
        exambutton = new JButton("시험 일정 수정");

        timetablebutton.setBackground(Color.cyan);
        calendarbutton.setBackground(Color.magenta);
        exambutton.setBackground(Color.yellow);

        timetablebutton.setOpaque(true);
        calendarbutton.setOpaque(true);
        exambutton.setOpaque(true);

        imglabel1 = new JLabel();
        imglabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        imglabel1.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));

        try {
            ImageIcon icon = new ImageIcon("timetable1_1.png");
            Image img = icon.getImage();

            int width = 75;
            int height = 75;

            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imglabel1.setIcon(new ImageIcon(scaled));
        }
        catch (Exception e) {
            System.out.println("이미지 불러오기 오류");
        }

        imglabel2 = new JLabel();
        imglabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        imglabel2.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));

        try {
            ImageIcon icon = new ImageIcon("hansung2_1.png");
            Image img = icon.getImage();

            int width = 150;
            int height = width / 2;

            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imglabel2.setIcon(new ImageIcon(scaled));
        }
        catch (Exception e) {
            System.out.println("이미지 불러오기 오류");
        }

        add(imglabel1, BorderLayout.WEST);
        add(imglabel2, BorderLayout.WEST);
        add(timetablebutton);
        add(calendarbutton);
        add(exambutton);
    }

    public JButton gettimetablebutton() { return timetablebutton; }
    public JButton getcalendarbutton() { return calendarbutton; }
    public JButton getexambutton() { return exambutton; }
}

class MainbuttonListener implements ActionListener {
    private MainFrame mainframe;
    private MainbuttonPanel panel;

    public MainbuttonListener(MainFrame mainframe, MainbuttonPanel panel) {
        this.mainframe = mainframe;
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent e) {
        SoundPlayer.playClick();

        if (e.getSource() == panel.gettimetablebutton()) {
            mainframe.gototimetable();
        }
        else if (e.getSource() == panel.getcalendarbutton()) {
            mainframe.gotocalendar();
        }
        else if (e.getSource() == panel.getexambutton()) {
            mainframe.exameditdialog();
        }
    }
}