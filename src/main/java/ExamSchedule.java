import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class ExamSchedule {
    private Lecture lecture;
    private LocalDate examDate;

    public ExamSchedule(Lecture lecture, LocalDate examDate) {
        this.lecture = lecture;
        this.examDate = examDate;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public int getDday(LocalDate today) {
        if (examDate == null || today == null) {
            return 0;
        }
        long diff = ChronoUnit.DAYS.between(today, examDate);
        return (int) diff;
    }
}
