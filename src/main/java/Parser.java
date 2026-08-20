/**
 * Interprets user commands and applies them to a list of tasks.
 */
public class Parser {
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
        } else if (command.equals("mark") && !parameters.isEmpty()) {
            inputs.complete(Integer.parseInt(parameters));
        } else if (command.equals("unmark") && !parameters.isEmpty()) {
            inputs.uncomplete(Integer.parseInt(parameters));
        } else if (command.equals("todo")) {
            addTodo(parameters);
        } else if (command.equals("deadline")) {
            addDeadline(parameters);
        } else if (command.equals("event")) {
            addEvent(parameters);
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
                    "\ttodo\n" +
                    "\t\tAdds a ToDo task\n" +
                    "\t\tUsage: todo <description>\n" +
                    "\tdeadline\n" +
                    "\t\tAdds a Deadline task\n" +
                    "\t\tUsage: deadline <description> /by <date>\n" +
                    "\tevent\n" +
                    "\t\tAdds an Event task\n" +
                    "\t\tUsage: event <description> /from <date> /to <date>\n" +
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
            System.out.println("\tUsage: deadline <description> /by <date>\n");
            return;
        }
        inputs.addTask(new Deadline(deadlineParts[0].trim(), deadlineParts[1].trim()));
    }

    /** Adds an event when it has a description, start time, and end time. */
    private void addEvent(String parameters) {
        String[] eventParts = parameters.split(" /from | /to ", 3);
        if (eventParts.length != 3 || eventParts[0].isBlank()
                || eventParts[1].isBlank() || eventParts[2].isBlank()) {
            System.out.println("\tUsage: event <description> /from <date> /to <date>\n");
            return;
        }
        inputs.addTask(new Event(eventParts[0].trim(), eventParts[1].trim(), eventParts[2].trim()));
    }
}
