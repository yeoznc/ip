public class Deadline extends Task{
    public final String deadline;

    Deadline(String name, String deadline) {
        super(name);
        this.deadline = deadline;
    }

    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    public String toString() {
        return super.toString() +  " (by: " + deadline + ")";
    }
}
