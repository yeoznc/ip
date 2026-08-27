package zinc.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    /** The format used when displaying deadline date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    /** The date and time by which this task must be completed. */
    public final LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param name the description of the task
     * @param deadline the date and time by which the task must be completed
     */
    public Deadline(String name, LocalDateTime deadline) {
        super(name);
        this.deadline = deadline;
    }

    /**
     * Returns the category of this task.
     *
     * @return the deadline task type
     */
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns a display-friendly representation including the deadline.
     *
     * @return the task name, completion status, and deadline
     */
    public String toString() {
        return super.toString() + " (by: "
                + deadline.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH) + ")";
    }
}
