package zinc.ui;

import java.util.List;

import zinc.contact.ContactCommandHandler;
import zinc.contact.ContactList;
import zinc.task.TaskCommandHandler;
import zinc.task.TaskList;

/**
 * Interprets top-level user commands and delegates domain operations.
 */
public class CommandParser {
    /** The command that exits Zinc. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that displays usage information. */
    private static final String HELP_COMMAND = "help";

    /** The full contact-command name. */
    private static final String CONTACT_COMMAND = "contact";

    /** The short contact-command alias. */
    private static final String CONTACT_COMMAND_ALIAS = "ct";

    /** The handler for commands that operate on tasks. */
    private final TaskCommandHandler taskCommandHandler;

    /** The handler for commands that operate on contacts. */
    private final ContactCommandHandler contactCommandHandler;

    /** The UI used to display general command messages. */
    private final Ui ui;

    /**
     * Creates a parser that updates the given task list.
     *
     * @param taskList The task list to update.
     */
    public CommandParser(TaskList taskList) {
        this(taskList, new ContactList(), new Ui());
    }

    /**
     * Creates a parser that updates the given task list using the given UI.
     *
     * @param taskList The task list to update.
     * @param ui The UI used to display user-facing messages.
     */
    public CommandParser(TaskList taskList, Ui ui) {
        this(taskList, new ContactList(), ui);
    }

    /**
     * Creates a parser that updates the supplied task and contact lists.
     *
     * @param taskList The task list to update.
     * @param contactList The contact list to update.
     */
    public CommandParser(TaskList taskList, ContactList contactList) {
        this(taskList, contactList, new Ui());
    }

    /**
     * Creates a parser using the supplied task list, contact list, and UI.
     *
     * @param taskList The task list to update.
     * @param contactList The contact list to update.
     * @param ui The UI used to display user-facing messages.
     */
    public CommandParser(TaskList taskList, ContactList contactList, Ui ui) {
        assert taskList != null && contactList != null && ui != null
                : "Command-parser dependencies must not be null";
        this.taskCommandHandler = new TaskCommandHandler(taskList, ui);
        this.contactCommandHandler = new ContactCommandHandler(contactList, ui);
        this.ui = ui;
    }

    /**
     * Processes one user command.
     *
     * @param input The complete line entered by the user.
     * @return {@code true} when the user entered {@code bye}; otherwise, {@code false}.
     */
    public boolean parseCommand(String input) {
        assert input != null : "Command input must not be null";
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return false;
        }

        String[] commandParts = trimmedInput.split("\\s+", 2);
        String command = commandParts[0];
        String parameters = commandParts.length > 1 ? commandParts[1].trim() : "";

        if (command.equals(EXIT_COMMAND) && parameters.isEmpty()) {
            return true;
        }
        if (command.equals(HELP_COMMAND)) {
            ui.printHelp();
            return false;
        }
        if (command.equals(CONTACT_COMMAND) || command.equals(CONTACT_COMMAND_ALIAS)) {
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
        List<String> suggestedCommands = ui.getCommands().stream()
                .filter(commandName -> commandName.startsWith(command))
                .toList();
        ui.printUnknownCommand(suggestedCommands);
    }
}
