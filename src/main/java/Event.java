import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task with a start date-time and an end date-time. */
public class Event extends Task{
    /** The format used when displaying event date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    /** The date and time at which the event starts. */
    public final LocalDateTime start;
    /** The date and time at which the event ends. */
    public final LocalDateTime end;

    Event(String name, LocalDateTime start, LocalDateTime end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    public String toString() {
        return super.toString() +  " (from: "
                + start.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH)
                + " to: " + end.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH) + ")";
    }
}
