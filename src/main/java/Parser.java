import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Interprets user commands and applies them to a list of tasks.
 */
public class Parser {
    /** Accepts a date with an optional 24-hour time, such as 31/08/26 1800. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yy HHmm");

    /** Accepts the calendar date used to filter deadlines and events. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** The date/time notation shown to users in command help and errors. */
    private static final String DATE_TIME_USAGE = "DD/MM/YY Optional[HH:MM]";

    /** The task list affected by recognised commands. */
    private final InputList inputs;

    /**
     * Creates a parser that updates the given task list.
     *
     * @param inputs the task list to update
     */
    public Parser(InputList inputs) {
        this.inputs = inputs;
    }

    /**
     * Processes one user command.
     *
     * @param input the complete line entered by the user
     * @return {@code true} when the user entered {@code bye}; otherwise {@code false}
     */
    public boolean parse(String input) {
        String[] commandParts = input.trim().split(" ", 2);
        String command = commandParts[0];
        String parameters = commandParts.length > 1 ? commandParts[1].trim() : "";

        if (command.equals("bye") && parameters.isEmpty()) {
            return true;
        } else if (command.equals("list") && parameters.isEmpty()) {
            inputs.printItems();
        } else if (command.equals("list")) {
            listTasksEndingOn(parameters);
        } else if (command.equals("mark")) {
            markTask(parameters);
        } else if (command.equals("unmark")) {
            unmarkTask(parameters);
        } else if (command.equals("todo")) {
            addTodo(parameters);
        } else if (command.equals("deadline")) {
            addDeadline(parameters);
        } else if (command.equals("event")) {
            addEvent(parameters);
        } else if(command.equals("delete")) {
            deleteTask(parameters);
        } else if (command.equals("help")) {
            System.out.println("\tAvailable commands:\n" +
                    "\tmark\n" +
                    "\t\tMarks task as done\n" +
                    "\t\tUsage: mark <task number>\n" +
                    "\tunmark\n" +
                    "\t\tMarks task as undone\n" +
                    "\t\tUsage: unmark <task number>\n" +
                    "\tlist\n" +
                    "\t\tLists all added tasks\n" +
                    "\t\tUsage: list\n" +
                    "\t\tUsage: list <DD/MM/YY> to list deadlines and events ending that day\n" +
                    "\ttodo\n" +
                    "\t\tAdds a ToDo task\n" +
                    "\t\tUsage: todo <description>\n" +
                    "\tdeadline\n" +
                    "\t\tAdds a Deadline task\n" +
                    "\t\tUsage: deadline <description> /by <" + DATE_TIME_USAGE + ">\n" +
                    "\tevent\n" +
                    "\t\tAdds an Event task\n" +
                    "\t\tUsage: event <description> /from <" + DATE_TIME_USAGE + "> /to <"
                            + DATE_TIME_USAGE + ">\n" +
                    "\tbye\n" +
                    "\t\tExits\n");
        } else {
            System.out.println("\tSorry, I don't know what you mean. Type help for a list of available commands\n");
        }
        return false;
    }

    /** Adds a todo when it has a description. */
    private void addTodo(String description) {
        if (description.isEmpty()) {
            System.out.println("\tThe description of a todo cannot be empty.\n");
            return;
        }
        inputs.addTask(new Todo(description));
    }

    /** Adds a deadline when it has a description and a due date. */
    private void addDeadline(String parameters) {
        String[] deadlineParts = parameters.split(" /by ", 2);
        if (deadlineParts.length != 2 || deadlineParts[0].isBlank() || deadlineParts[1].isBlank()) {
            System.out.println("\tUsage: deadline <description> /by <" + DATE_TIME_USAGE + ">\n");
            return;
        }
        try {
            inputs.addTask(new Deadline(deadlineParts[0].trim(), parseDateTime(deadlineParts[1])));
        } catch (DateTimeParseException exception) {
            System.out.println("\tDate and time must use " + DATE_TIME_USAGE + ".\n");
        }
    }

    /** Adds an event when it has a description, start time, and end time. */
    private void addEvent(String parameters) {
        String[] eventParts = parameters.split(" /from | /to ", 3);
        if (eventParts.length != 3 || eventParts[0].isBlank()
                || eventParts[1].isBlank() || eventParts[2].isBlank()) {
            System.out.println("\tUsage: event <description> /from <" + DATE_TIME_USAGE + "> /to <"
                    + DATE_TIME_USAGE + ">\n");
            return;
        }
        try {
            inputs.addTask(new Event(eventParts[0].trim(), parseDateTime(eventParts[1]),
                    parseDateTime(eventParts[2])));
        } catch (DateTimeParseException exception) {
            System.out.println("\tDate and time must use " + DATE_TIME_USAGE + ".\n");
        }
    }

    /** Converts a command date to a date-time, using midnight when no time is given. */
    private LocalDateTime parseDateTime(String dateTime) {
        String input = dateTime.trim();
        if (!input.contains(" ")) {
            input += " 0000";
        }
        return LocalDateTime.parse(input, DATE_TIME_FORMAT);
    }

    /** Lists deadlines and events whose end date matches the supplied date. */
    private void listTasksEndingOn(String parameters) {
        try {
            inputs.printTasksEndingOn(LocalDate.parse(parameters, DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            System.out.println("\tDate must use DD/MM/YY. Usage: list <DD/MM/YY>\n");
        }
    }

    /** Marks the task at the supplied user-facing task number as complete. */
    private void markTask(String parameters) {
        try {
            inputs.complete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            System.out.println("\tTask number must be an integer. Usage: mark <task number>\n");
        }
    }

    /** Marks the task at the supplied user-facing task number as incomplete. */
    private void unmarkTask(String parameters) {
        try {
            inputs.uncomplete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            System.out.println("\tTask number must be an integer. Usage: unmark <task number>\n");
        }
    }

    /** Deletes the task at the supplied user-facing task number. */
    private void deleteTask(String parameters) {
        try {
            inputs.delete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            System.out.println("\tTask number must be an integer. Usage: delete <task number>\n");
        }
    }
}
