import java.time.LocalDate;

public class TodoItem {
    private int id;
    private LocalDate date;
    private String content;

    public TodoItem(int id, LocalDate date, String content) {
        this.id = id;
        this.date = date;
        this.content = content;
    }

    public int getid() { return id; }
    public LocalDate getdate() { return date; }
    public String getcontent() { return content; }

    public void setdate(LocalDate date) { this.date = date; }
    public void setcontent(String content) { this.content = content; }
}
