public class Deadline extends Task{
    private final String deadline;

    Deadline(String name, String deadline) {
        super(name);
        this.deadline = deadline;
    }

    public String taskIdentifier() {
        return "[D]";
    }

    public String toString() {
        return super.toString() +  " (by: " + deadline + ")";
    }
}
