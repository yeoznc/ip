package zinc.task;

public class Todo extends Task {
    public Todo(String name) {
        super(name);
    }

    public TaskType getTaskType() {
        return TaskType.TODO;
    }
}
