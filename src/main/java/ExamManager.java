import java.util.*;
import java.time.LocalDate;

public class ExamManager {
    private Timetable timetable;
    private Map<Lecture, ExamSchedule> exams = new HashMap<>();

    public ExamManager(Timetable timetable) {
        this.timetable = timetable;
    }

    public ExamSchedule getExamSchedule(Lecture lecture) {
        if (lecture == null) {
            return null;
        }
        return exams.get(lecture);
    }

    public void setExamDate(Lecture lecture, LocalDate examdate) {
        if (lecture == null) {
            return;
        }

        if (examdate == null) {
            exams.remove(lecture);
        }
        else {
            ExamSchedule schedule = exams.get(lecture);
            if (schedule == null) {
                schedule = new ExamSchedule(lecture, examdate);
                exams.put(lecture, schedule);
            } else {
                schedule.setExamDate(examdate);
            }
        }
    }

    public Collection<ExamSchedule> getAllExamSchedules() {
        return exams.values();
    }

    public void clear() {
        exams.clear();
    }

    public Lecture findLecture(int id) {
        int day = timetable.getDay();
        int time = timetable.getTimes();

        for (int  i = 0; i < day; i++) {
            for (int  j = 0; j < time; j++) {
                Lecture lec = timetable.getLecture(i, j);
                if (lec != null && lec.getid() == id) {
                    return lec;
                }
            }
        }
        return null;
    }
}
