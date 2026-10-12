import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TimetableFrame extends JFrame {
    private Timetable timetable;
    private MainTimeTable maintimetable;

    public TimetableFrame(Timetable timetable) {
        this.timetable = timetable;

        setTitle("시간표 조회/수정");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        init();
    }

    private void init() {
        setLayout(new BorderLayout(10, 10));
        JLabel info = new JLabel("한번 클릭시 수정 / 두번 클릭시 삭제", SwingConstants.CENTER);
        add(info, BorderLayout.NORTH);

        maintimetable = new MainTimeTable(timetable);
        add(maintimetable, BorderLayout.CENTER);
        JLabel[][] cells = maintimetable.getCells();

        int daycount = timetable.getDay();
        int timecount = timetable.getTimes();

        for (int r = 0; r < timecount && r < cells.length; r++) {
            for (int dayIndex = 0; dayIndex < daycount; dayIndex++) {
                int c = dayIndex + 1;
                if (c >= cells[r].length) {
                    continue;
                }
                JLabel cell = cells[r][c];

                cell.addMouseListener(new TimetableListener(this, timetable, dayIndex, r));
            }
        }
    }

    public void refreshTable() {
        maintimetable.refresh();
    }

    public void openEditDialog(int dayIndex, int timeIndex) {
        TimetableEdit dialog = new TimetableEdit(this, timetable, dayIndex, timeIndex);
        dialog.setVisible(true);
        refreshTable();
    }

    public void deleteLectureAt(int dayIndex, int timeIndex) {
        Lecture lec = timetable.getLecture(dayIndex, timeIndex);

        if (lec == null) {
            return;
        }

        int timecount = timetable.getTimes();
        for (int t = 0; t < timecount; t++) {
            if (timetable.getLecture(dayIndex, t) == lec) {
                timetable.clearLecture(dayIndex, t);
            }
        }
        refreshTable();
    }
}

class TimetableListener extends MouseAdapter {
    private  TimetableFrame parent;
    private Timetable timetable;
    private int dayIndex;
    private int timeIndex;

    private Timer clickTimer;
    private static final int CLICK_DELAY = 200;

    public TimetableListener(TimetableFrame parent,Timetable timetable, int dayIndex, int timeIndex) {
        this.parent = parent;
        this.timetable = timetable;
        this.dayIndex = dayIndex;
        this.timeIndex = timeIndex;
    }

    public void mouseClicked(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        int count = e.getClickCount();
        if (count == 2) {
            if (clickTimer != null && clickTimer.isRunning()) {
                clickTimer.stop();
            }
            parent.deleteLectureAt(dayIndex, timeIndex);
        }
        else if(count == 1) {
            if (clickTimer != null && clickTimer.isRunning()) {
                clickTimer.stop();
            }
            clickTimer = new Timer(CLICK_DELAY, ev ->{
                parent.openEditDialog(dayIndex, timeIndex);
            });
            clickTimer.setRepeats(false);
            clickTimer.start();
        }
    }
}
