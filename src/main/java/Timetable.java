

public class Timetable {
    private int day;
    private int times;
    private Lecture[][] lectures;

    public Timetable(int day, int times) {
        this.day = day;
        this.times = times;
        this.lectures = new Lecture[day][times];
    }

    public int getDay() { return day; }
    public int getTimes() { return times; }

    public void setLecture(int dayindex, int start, int duration, Lecture lecture) {
        if (dayindex < 0 || dayindex >= day) return;
        if (start < 0 || start >= times) return;
        if (duration <= 0 || duration >= times) return;
        for (int i = start; i < start + duration && i < times; i++) {
            lectures[dayindex][i] = lecture;
        }
    }

    public Lecture getLecture(int dayindex, int timeindex) {
        if (dayindex < 0 || dayindex >= day) {return null;}
        if (timeindex < 0 || timeindex >= times) {return null;}
        return lectures[dayindex][timeindex];
    }

    public void clearLecture(int dayindex, int timeindex) {
        if (dayindex < 0 || dayindex >= day) {return;}
        if (timeindex < 0 || timeindex >= times) {return;}
        lectures[dayindex][timeindex] = null;
    }
}
