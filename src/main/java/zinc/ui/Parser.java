package zinc.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;

import zinc.task.Deadline;
import zinc.task.Event;
import zinc.task.InputList;
import zinc.task.Todo;

/**
 * Interprets user commands and applies them to a list of tasks.
 */
public class Parser {
    /** Accepts a date with an optional 24-hour time, such as 31/08/26 1800. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts the calendar date used to filter deadlines and events. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** The task list affected by recognised commands. */
    private final InputList inputs;

    /** The UI used to display user-facing messages. */
    private final Ui ui;

    /** Commands indexed by their user-facing names. */
    private final Map<String, Command> commands;

    /**
     * Creates a parser that updates the given task list.
     *
     * @param inputs The task list to update.
     */
    public Parser(InputList inputs) {
        this(inputs, new Ui());
    }

    /**
     * Creates a parser that updates the given task list using the given UI.
     *
     * @param inputs The task list to update.
     * @param ui The UI used to display user-facing messages.
     */
    public Parser(InputList inputs, Ui ui) {
        assert inputs != null && ui != null : "Parser dependencies must not be null";
        this.inputs = inputs;
        this.ui = ui;
        this.commands = createCommands();
    }

    /**
     * Processes one user command.
     *
     * @param input The complete line entered by the user.
     * @return {@code true} when the user entered {@code bye}; otherwise, {@code false}.
     */
    public boolean parse(String input) {
        assert input != null : "Command input must not be null";
        String[] commandParts = input.trim().split(" ", 2);
        String command = commandParts[0];
        String parameters = commandParts.length > 1 ? commandParts[1].trim() : "";

        if (command.equals("bye") && parameters.isEmpty()) {
            return true;
        }

        Command selectedCommand = commands.get(command);
        if (selectedCommand != null) {
            selectedCommand.execute(parameters);
            return false;
        }

        StringBuilder otherCommands = new StringBuilder();
        for (String commandName : ui.getCommands()) {
            if (commandName.startsWith(command)) {
                otherCommands.append(commandName).append(" ");
            }
        }
        if (otherCommands.isEmpty()) {
            System.out.println("Sorry, I don't know what you mean. Type help for a list of available commands\n");
        } else {
            System.out.println("Did you mean: " + otherCommands);
            System.out.println("Type help for a list of available commands");
        }

        return false;
    }

    /** Creates the command registry used to dispatch parsed input. */
    private Map<String, Command> createCommands() {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("list", parameters -> {
            if (parameters.isEmpty()) {
                inputs.printTasks();
            } else {
                listTasksEndingOn(parameters);
            }
        });
        commandMap.put("ls", parameters -> inputs.printTasks());
        commandMap.put("find", this::findTasks);
        commandMap.put("mark", this::markTask);
        commandMap.put("unmark", this::unmarkTask);
        commandMap.put("todo", this::addTodo);
        commandMap.put("deadline", this::addDeadline);
        commandMap.put("event", this::addEvent);
        commandMap.put("delete", this::deleteTask);
        commandMap.put("help", parameters -> ui.printHelp());
        return commandMap;
    }

    /** Adds a todo when it has a description. */
    private void addTodo(String description) {
        if (description.isEmpty()) {
            ui.printTodoUsage();
            return;
        }

        inputs.addTask(new Todo(description));
    }

    /** Adds a deadline when it has a description and a due date. */
    private void addDeadline(String parameters) {
        String[] deadlineParts = parameters.split(" /by ", 2);
        boolean isIncorrectLength = deadlineParts.length != 2;

        if (isIncorrectLength || deadlineParts[0].isBlank() || deadlineParts[1].isBlank()) {
            ui.printDeadlineUsage();
            return;
        }

        try {
            inputs.addTask(new Deadline(deadlineParts[0].trim(), parseDateTime(deadlineParts[1])));
        } catch (DateTimeParseException exception) {
            ui.printDateTimeError();
        }
    }

    /** Adds an event when it has a description, start time, and end time. */
    private void addEvent(String parameters) {
        String[] eventParts = parameters.split(" /from | /to ", 3);
        boolean isIncorrectLength = eventParts.length != 3;

        if (isIncorrectLength || eventParts[0].isBlank()
                || eventParts[1].isBlank() || eventParts[2].isBlank()) {
            ui.printEventUsage();
            return;
        }

        try {
            inputs.addTask(new Event(eventParts[0].trim(), parseDateTime(eventParts[1]),
                    parseDateTime(eventParts[2])));
        } catch (DateTimeParseException exception) {
            ui.printDateTimeError();
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
            ui.printListDateError();
        }
    }

    /** Finds tasks whose descriptions contain the supplied keyword. */
    private void findTasks(String keyword) {
        if (keyword.isBlank()) {
            ui.printFindUsage();
            return;
        }
        inputs.printTasksContaining(keyword);
    }

    /** Marks the task at the supplied user-facing task number as complete. */
    private void markTask(String parameters) {
        try {
            inputs.complete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            ui.printTaskNumberError("mark");
        }
    }

    /** Marks the task at the supplied user-facing task number as incomplete. */
    private void unmarkTask(String parameters) {
        try {
            inputs.uncomplete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            ui.printTaskNumberError("unmark");
        }
    }

    /** Deletes the task at the supplied user-facing task number. */
    private void deleteTask(String parameters) {
        try {
            inputs.delete(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            ui.printTaskNumberError("delete");
        }
    }

}
