import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDayPanel extends JPanel {
    private LocalDate date;
    private DayofTodoManager todoManager;
    private JLabel labeldate;
    private JPanel todoPanel;
    private List<JLabel> todolabels = new ArrayList<>();

    public CalendarDayPanel(LocalDate date, DayofTodoManager todoManager) {
        this.date = date;
        this.todoManager = todoManager;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(2,2));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        labeldate = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.RIGHT);
        labeldate.setFont(labeldate.getFont().deriveFont(Font.BOLD));
        add(labeldate, BorderLayout.NORTH);

        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));
        add(todoPanel, BorderLayout.CENTER);
    }

    public LocalDate getDate() {
        return date;
    }

    public void itemrefresh() {
        todoPanel.removeAll();
        todolabels.clear();

        java.util.List<TodoItem> items = todoManager.getTodos(date);
        if (items != null) {
            for (TodoItem item : items) {
                JLabel label = new JLabel("• " + item.getcontent());
                label.setAlignmentX(Component.CENTER_ALIGNMENT);

                CalendarDeleteListener deleteListener = new CalendarDeleteListener(item, todoManager, this);
                adddeletelistener(deleteListener, label);

                todolabels.add(label);
                todoPanel.add(label);
            }
        }

        revalidate();
        repaint();
    }

    public void  addaddlistener(MouseListener listener) {
        this.addMouseListener(listener);
        labeldate.addMouseListener(listener);
    }

    public void adddeletelistener(MouseAdapter listener, JLabel label) {
        label.addMouseListener(listener);
    }
}

class CalendarAddListener extends MouseAdapter {
    private DayofTodoManager todoManager;
    private CalendarDayPanel daypanel;
    private CalendarFrame parent;

    public CalendarAddListener(CalendarFrame parent, CalendarDayPanel daypanel, DayofTodoManager todomanager) {
        this.parent = parent;
        this.todoManager = todomanager;
        this.daypanel = daypanel;
    }

    public void mouseClicked(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (e.getClickCount() != 1) {
            return;
        }

        TodoEdit dialog = new TodoEdit(parent, daypanel.getDate(), todoManager);
        dialog.setVisible(true);

        parent.calendarrefresh();
    }
}

class CalendarDeleteListener extends MouseAdapter {
    private TodoItem todoitem;
    private DayofTodoManager todomanager;
    private CalendarDayPanel daypanel;

    public CalendarDeleteListener(TodoItem todoitem, DayofTodoManager todomanager, CalendarDayPanel daypanel) {
        this.todoitem = todoitem;
        this.todomanager = todomanager;
        this.daypanel = daypanel;
    }

    public void mouseClicked(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (e.getClickCount() != 2) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                null,
                "이 할 일을 삭제할까요?\n\n" + todoitem.getcontent(),
                "삭제 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            SoundPlayer.playConfirm();
            todomanager.removeTodo(todoitem.getid());
            daypanel.itemrefresh();
        }
        else {
            SoundPlayer.playCancel();
        }
    }
}