import java.time.LocalDate;
import java.util.*;

public class DayofTodoManager {
    private Map<LocalDate, List<TodoItem>> todos = new HashMap<>();
    private int nextid = 1;

    public TodoItem addtodo(LocalDate date, String content, String location) {
        TodoItem item = new TodoItem(nextid++, date, content);
        addtodo(item);
        return item;
    }

    public void addtodo(TodoItem item) {
        todos.computeIfAbsent(item.getdate(), d -> new ArrayList<>()).add(item);
        nextid = Math.max(nextid++, item.getid() + 1);
    }

    public void removeTodo(int id) {
        for (List<TodoItem> list : todos.values()) {
            list.removeIf(t -> t.getid() == id);
        }
    }

    public List<TodoItem> getTodos(LocalDate date) {
        return new ArrayList<>(todos.getOrDefault(date, Collections.emptyList()));
    }

    public List<TodoItem> getTodos() {
        List<TodoItem> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<TodoItem>> e : todos.entrySet()) {
            result.addAll(e.getValue());
        }
        return result;
    }

    public void clear() {
        todos.clear();
    }
}
