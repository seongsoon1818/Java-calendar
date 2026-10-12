import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class TodoEdit extends JDialog {
    private LocalDate date;
    private DayofTodoManager todomanager;

    private JLabel labeldate;
    private JTextField contentfield;
    private JButton okbutton;
    private JButton cancelbutton;

    public TodoEdit(JFrame parent, LocalDate date, DayofTodoManager todomanager) {
        super(parent, "항 일 추가", true);
        this.date = date;
        this.todomanager = todomanager;

        init();
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        labeldate = new JLabel("날짜: " + date.toString());
        add(labeldate, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(new JLabel("할 일 내용:"), BorderLayout.WEST);

        contentfield = new JTextField(20);
        center.add(contentfield, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okbutton = new JButton("확인");
        cancelbutton = new JButton("취소");

        okbutton.addActionListener(this::onOk);
        cancelbutton.addActionListener(e -> {
            SoundPlayer.playCancel();
            dispose();
        });

        bottom.add(okbutton);
        bottom.add(cancelbutton);
        add(bottom, BorderLayout.SOUTH);
    }

    private void onOk(ActionEvent e) {
        SoundPlayer.playConfirm();
        changeapply();
    }

    private void changeapply() {
        String content = contentfield.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "할 일 내용을 입력하세요.",
                    "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        todomanager.addtodo(date, content, "");

        dispose();
    }
}
