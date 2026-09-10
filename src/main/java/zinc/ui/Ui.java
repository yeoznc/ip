package zinc.ui;

import java.util.List;

/**
 * Handles the user-facing presentation of Zinc.
 */
public class Ui {
    /** The horizontal separator used between sections of user-facing output. */
    public static final String SEPARATOR = "_________________________________________";

    /** All commands currently supported by Zinc, in alphabetical order. */
    private static final List<String> SUPPORTED_COMMANDS = List.of(
            "bye",
            "contact",
            "deadline",
            "delete",
            "event",
            "find",
            "help",
            "list",
            "mark",
            "todo",
            "unmark");

    /** Instructions displayed by the help command. */
    private static final String HELP_MESSAGE = """
            Available commands:
            bye
            \tExits
            contact add (Alternative: ct add)
            \tAdds a contact; the contact number and description are optional
            \tUsage: contact add /n <name> [/p <8-digit contact number>] [/d <description>]
            contact delete (or contact del)
            \tDeletes a contact using its name
            \tUsage: contact del /n <name>
            contact update
            \tUpdates one or more fields of a contact
            \tUsage: contact update <current name> [/n <new name>] [/p <8-digit contact number>] [/d <description>]
            deadline
            \tAdds a Deadline task
            \tUsage: deadline <description> /by <DD/MM/YY Optional[HH:MM]>
            delete
            \tDeletes a task
            \tUsage: delete <task number>
            event
            \tAdds an Event task
            \tUsage: event <description> /from <DD/MM/YY Optional[HH:MM]> /to <DD/MM/YY Optional[HH:MM]>
            find
            \tFinds tasks whose descriptions contain a keyword
            \tUsage: find <keyword>
            help
            \tShows this help list
            \tUsage: help
            list
            \tLists all added tasks
            \tUsage: list
            \tUsage: list <DD/MM/YY> to list deadlines and events ending that day
            mark
            \tMarks task as done
            \tUsage: mark <task number>
            todo
            \tAdds a ToDo task
            \tUsage: todo <description>
            unmark
            \tMarks task as undone
            \tUsage: unmark <task number>
            """;

    /** Prints the Zinc banner. */
    public void printBanner() {
        String banner = " ______ _            \n"
                + "|___  /(_)           \n"
                + "   / /  _ _ __   ___ \n"
                + "  / /  | | '_ \\ / __|\n"
                + " / /___| | | | | (__ \n"
                + "/______|_|_| |_|\\___|\n";
        System.out.println(banner);
    }

    /** Prints the opening greeting. */
    public void printGreeting() {
        System.out.println("Hello, my name's Zinc.\n"
                + "What can I do for you?\n"
                + SEPARATOR);
    }

    /** Prints the currently supported commands and their usage. */
    public void printHelp() {
        System.out.println(HELP_MESSAGE);
    }

    /** Prints the usage message for an empty todo description. */
    public void printTodoUsage() {
        System.out.println("The description of a todo cannot be empty.\n");
    }

    /** Prints the usage message for the deadline command. */
    public void printDeadlineUsage() {
        System.out.println("Usage: deadline <description> /by <DD/MM/YY Optional[HH:MM]>\n");
    }

    /** Prints the usage message for the event command. */
    public void printEventUsage() {
        System.out.println("Usage: event <description> /from <DD/MM/YY Optional[HH:MM]>"
                + " /to <DD/MM/YY Optional[HH:MM]>\n");
    }

    /** Prints the message used when an event ends before it starts. */
    public void printEventChronologyError() {
        System.out.println("An event cannot end before it starts.\n");
    }

    /** Prints the message used when the task list has reached its capacity. */
    public void printTaskListFull() {
        System.out.println("The task list is full. Delete a task before adding another.\n");
    }

    /** Prints the invalid date/time message. */
    public void printDateTimeError() {
        System.out.println("Date and time must use DD/MM/YY Optional[HH:MM].\n");
    }

    /** Prints the usage message for the list date filter. */
    public void printListDateError() {
        System.out.println("Date must use DD/MM/YY. Usage: list <DD/MM/YY>\n");
    }

    /** Prints the usage message for the find command. */
    public void printFindUsage() {
        System.out.println("Usage: find <keyword>\n");
    }

    /** Prints the usage message for contact commands. */
    public void printContactUsage() {
        System.out.println("Usage:\n"
                + "contact add /n <name> [/p <8-digit contact number>] [/d <description>]\n"
                + "contact del /n <name>\n"
                + "contact update <current name> [/n <new name>]"
                + " [/p <8-digit contact number>] [/d <description>]\n"
                + "contact list <keyword>\n");
    }

    /** Prints the message used when a contact number is invalid. */
    public void printContactNumberError() {
        System.out.println("Contact number must contain exactly 8 digits.\n");
    }

    /** Prints the invalid task-number message for the given command. */
    public void printTaskNumberError(String command) {
        System.out.println("Task number must be an integer. Usage: " + command
                + " <task number>\n");
    }

    /** Prints the closing message. */
    public void printGoodbye() {
        System.out.println(SEPARATOR + "\n"
                + "Goodbye.\n"
                + SEPARATOR);
    }

    /**
     * Returns the supported commands in alphabetical order.
     *
     * @return An immutable list of command names.
     */
    public List<String> getCommands() {
        return SUPPORTED_COMMANDS;
    }
}
