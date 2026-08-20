public class Event extends Task{
    private final String start;
    private final String end;

    Event(String name, String start, String end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    public String toString() {
        return super.toString() +  " (from: " + start + " to: " + end + ")";
    }
}
