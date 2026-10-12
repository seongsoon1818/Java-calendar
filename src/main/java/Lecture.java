

public class Lecture {
    private int id;
    private String name;
    private String professor;
    private String location;
    private int duration;

    public Lecture(int id, String name, String professor, String location, int duration) {
        this.id = id;
        this.name = name;
        this.professor = professor;
        this.location = location;
        this.duration = duration;
    }

    public int getid() { return id; }
    public String getname() { return name; }
    public String getprofessor() { return professor; }
    public String getlocation() { return location; }
    public int getduration() { return duration; }

    public void editid(int id) { this.id = id; }
    public void editname(String name) { this.name = name; }
    public void editprofessor(String professor) { this.professor = professor; }
    public void editlocation(String location) { this.location = location; }
    public void editduration(int duration) { this.duration = duration; }
}
