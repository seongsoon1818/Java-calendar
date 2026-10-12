import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.file.*;
import java.time.*;

public class DataManager {
    private Timetable timetable;
    private ExamManager exammanager;
    private DayofTodoManager todomanager;

    private static final String TIMETABLE_FILE = "timetable.txt";
    private static final String TODO_FILE = "todo.txt";
    private static final String EXAM_FILE = "exam.txt";

    public Timetable gettimetable() {
        return timetable;
    }

    public ExamManager getexammanager() {
        return exammanager;
    }

    public DayofTodoManager gettodomanager() {
        return todomanager;
    }

    public DataManager() {
        this.timetable = new Timetable(5, 9);
        this.exammanager = new ExamManager(timetable);
        this.todomanager = new DayofTodoManager();

        loadTimetable();
        loadExam();
        loadTodo();
    }

    public void loadTimetable() {
        loadTimetable(TIMETABLE_FILE);
    }

    public void saveTimetable() {
        saveTimetable(TIMETABLE_FILE);
    }

    private void loadTimetable(String filename) {
        Path path = Paths.get(filename);

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) {
                    continue;
                }
                int day = Integer.parseInt(parts[0]);
                int start = Integer.parseInt(parts[1]);
                int duration = Integer.parseInt(parts[2]);
                int id = Integer.parseInt(parts[3]);
                String name = parts[4];
                String professor = parts[5];
                String location = parts[6];

                Lecture lec = new Lecture(id, name, professor, location, duration);
                timetable.setLecture(day, start, duration, lec);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveTimetable(String filename) {
        Path path = Paths.get(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            int dayCount = timetable.getDay();
            int timeCount = timetable.getTimes();

            for (int day = 0; day < dayCount; day++) {
                for (int t = 0; t < timeCount; t++) {
                    Lecture lec = timetable.getLecture(day, t);
                    if (lec == null) {
                        continue;
                    }
                    if (t > 0 && timetable.getLecture(day, t - 1) == lec) {
                        continue;
                    }
                    int duration = lec.getduration();
                    bw.write(day + "," + t + "," + duration + "," + lec.getid() + "," +
                            lec.getname() + "," + lec.getprofessor() + "," + lec.getlocation());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTodo() {
        loadTodo(TODO_FILE);
    }

    public void saveTodo() {
        saveTodo(TODO_FILE);
    }

    private void loadTodo(String filename) {
        Path path = Paths.get(filename);

        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) {
                    continue;
                }
                int id = Integer.parseInt(parts[0]);
                LocalDate date = LocalDate.parse(parts[1]);
                String content = parts[2];

                TodoItem item = new TodoItem(id, date, content);
                todomanager.addtodo(item);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTodo(String filename) {
        Path path = Paths.get(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            List<TodoItem> items = todomanager.getTodos();
            for (TodoItem item : items) {
                bw.write(item.getid() + "," + item.getdate().toString() + "," + item.getcontent());
                bw.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadExam() {
        loadExam(EXAM_FILE);
    }

    public void saveExam() {
        saveExam(EXAM_FILE);
    }

    private void loadExam(String filename) {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", 2);
                if (parts.length < 2) {
                    continue;
                }

                int lectureId = Integer.parseInt(parts[0]);
                LocalDate examDate = LocalDate.parse(parts[1]);

                Lecture lec = exammanager.findLecture(lectureId);
                if (lec != null) {
                    exammanager.setExamDate(lec, examDate);
                }
                else {
                    System.out.println("강의 id와 시간 확인 요망");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveExam(String filename) {
        Path path = Paths.get(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            for (ExamSchedule es : exammanager.getAllExamSchedules()) {
                Lecture lec = es.getLecture();
                LocalDate date = es.getExamDate();

                if (lec == null || date == null) {
                    continue;
                }

                bw.write(lec.getid() + "," + date.toString());
                bw.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
