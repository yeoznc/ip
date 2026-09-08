package zinc.ui;

import zinc.contact.ContactCommandHandler;
import zinc.task.InputList;
import zinc.task.TaskCommandHandler;

/**
 * Interprets top-level user commands and delegates domain operations.
 */
public class Parser {
    /** The handler for commands that operate on tasks. */
    private final TaskCommandHandler taskCommandHandler;

    /** The handler for commands that operate on contacts. */
    private final ContactCommandHandler contactCommandHandler;

    /** The UI used to display general command messages. */
    private final Ui ui;

    /**
     * Creates a parser that updates the given task list.
     *
     * @param inputs The task list to update.
     */
    public Parser(InputList inputs) {
        this(inputs, new zinc.contact.InputList(), new Ui());
    }

    /**
     * Creates a parser that updates the given task list using the given UI.
     *
     * @param inputs The task list to update.
     * @param ui The UI used to display user-facing messages.
     */
    public Parser(InputList inputs, Ui ui) {
        this(inputs, new zinc.contact.InputList(), ui);
    }

    /**
     * Creates a parser that updates the supplied task and contact lists.
     *
     * @param taskInputs The task list to update.
     * @param contactInputs The contact list to update.
     */
    public Parser(InputList taskInputs, zinc.contact.InputList contactInputs) {
        this(taskInputs, contactInputs, new Ui());
    }

    /**
     * Creates a parser using the supplied task list, contact list, and UI.
     *
     * @param taskInputs The task list to update.
     * @param contactInputs The contact list to update.
     * @param ui The UI used to display user-facing messages.
     */
    public Parser(InputList taskInputs, zinc.contact.InputList contactInputs, Ui ui) {
        assert taskInputs != null && contactInputs != null && ui != null
                : "Parser dependencies must not be null";
        this.taskCommandHandler = new TaskCommandHandler(taskInputs, ui);
        this.contactCommandHandler = new ContactCommandHandler(contactInputs, ui);
        this.ui = ui;
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
        if (command.equals("help")) {
            ui.printHelp();
            return false;
        }
        if (command.equals("contact") || command.equals("ct")) {
            contactCommandHandler.execute(parameters);
            return false;
        }
        if (taskCommandHandler.execute(command, parameters)) {
            return false;
        }

        printUnknownCommand(command);
        return false;
    }

    /** Prints either similar command names or the unknown-command message. */
    private void printUnknownCommand(String command) {
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
    }
}
