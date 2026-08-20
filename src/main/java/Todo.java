public class Todo extends Task {
    Todo(String name) {
        super(name);
    }

    public TaskType getTaskType() {
        return TaskType.TODO;
    }
}
