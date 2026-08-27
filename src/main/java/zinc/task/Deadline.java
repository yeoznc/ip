package zinc.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task{
    /** The format used when displaying deadline date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    /** The date and time by which this task must be completed. */
    public final LocalDateTime deadline;

    public Deadline(String name, LocalDateTime deadline) {
        super(name);
        this.deadline = deadline;
    }

    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    public String toString() {
        return super.toString() +  " (by: "
                + deadline.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH) + ")";
    }
}
