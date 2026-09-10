package zinc.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Map;
import java.util.function.Consumer;

import zinc.ui.Ui;

/**
 * Parses and executes commands that operate on tasks.
 */
public class TaskCommandHandler {
    /** Accepts a date with an optional 24-hour time, such as 31/08/26 1800. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts the calendar date used to filter deadlines and events. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** The task list affected by commands. */
    private final TaskList taskList;

    /** The UI used to display validation messages. */
    private final Ui ui;

    /** Task commands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /**
     * Creates a handler for commands that affect the supplied task list.
     *
     * @param taskList The task list to update.
     * @param ui The UI used to display validation messages.
     */
    public TaskCommandHandler(TaskList taskList, Ui ui) {
        assert taskList != null && ui != null : "Task command dependencies must not be null";
        this.taskList = taskList;
        this.ui = ui;
        this.commands = createCommands();
    }

    /**
     * Executes a task command when its name is recognized.
     *
     * @param command The task command name.
     * @param parameters The parameters following the command name.
     * @return {@code true} if the command was recognized; otherwise, {@code false}.
     */
    public boolean execute(String command, String parameters) {
        assert command != null && parameters != null : "Task command input must not be null";
        Consumer<String> selectedCommand = commands.get(command);
        if (selectedCommand == null) {
            return false;
        }

        selectedCommand.accept(parameters);
        return true;
    }

    /** Creates the command registry used to dispatch task operations. */
    private Map<String, Consumer<String>> createCommands() {
        return Map.of(
                "list", this::listTasks,
                "ls", ignoredParameters -> taskList.printTasks(),
                "find", this::findTasks,
                "mark", parameters -> changeTaskCompletion(parameters, true),
                "unmark", parameters -> changeTaskCompletion(parameters, false),
                "todo", this::addTodo,
                "deadline", this::addDeadline,
                "event", this::addEvent,
                "delete", this::deleteTask);
    }

    /** Lists every task or only tasks ending on a supplied date. */
    private void listTasks(String parameters) {
        if (parameters.isEmpty()) {
            taskList.printTasks();
            return;
        }

        try {
            taskList.printTasksEndingOn(LocalDate.parse(parameters, DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            ui.printListDateError();
        }
    }

    /** Adds a todo when it has a description. */
    private void addTodo(String description) {
        if (description.isEmpty()) {
            ui.printTodoUsage();
            return;
        }

        taskList.addTask(new Todo(description));
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
            taskList.addTask(new Deadline(deadlineParts[0].trim(), parseDateTime(deadlineParts[1])));
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
            LocalDateTime start = parseDateTime(eventParts[1]);
            LocalDateTime end = parseDateTime(eventParts[2]);
            if (end.isBefore(start)) {
                ui.printEventChronologyError();
                return;
            }

            taskList.addTask(new Event(eventParts[0].trim(), start, end));
        } catch (DateTimeParseException exception) {
            ui.printDateTimeError();
        }
    }

    /** Converts a command date to a date-time, using midnight when no time is given. */
    private LocalDateTime parseDateTime(String dateTimeText) {
        String normalizedDateTime = dateTimeText.trim();
        if (!normalizedDateTime.contains(" ")) {
            normalizedDateTime += " 0000";
        }
        return LocalDateTime.parse(normalizedDateTime, DATE_TIME_FORMAT);
    }

    /** Finds tasks whose descriptions contain the supplied keyword. */
    private void findTasks(String keyword) {
        if (keyword.isBlank()) {
            ui.printFindUsage();
            return;
        }
        taskList.printTasksContaining(keyword);
    }

    /** Changes the completion state of the task at the supplied user-facing task number. */
    private void changeTaskCompletion(String parameters, boolean shouldMarkAsCompleted) {
        String command = shouldMarkAsCompleted ? "mark" : "unmark";
        try {
            int taskNumber = Integer.parseInt(parameters);
            if (shouldMarkAsCompleted) {
                taskList.markTaskAsCompleted(taskNumber);
            } else {
                taskList.markTaskAsIncomplete(taskNumber);
            }
        } catch (NumberFormatException exception) {
            ui.printTaskNumberError(command);
        }
    }

    /** Deletes the task at the supplied user-facing task number. */
    private void deleteTask(String parameters) {
        try {
            taskList.deleteTask(Integer.parseInt(parameters));
        } catch (NumberFormatException exception) {
            ui.printTaskNumberError("delete");
        }
    }
}
