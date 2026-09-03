package zinc.ui;

import java.util.List;

/**
 * Handles the user-facing presentation of Zinc.
 */
public class Ui {
    /** All commands currently supported by Zinc, in alphabetical order. */
    private final List<String> commands = List.of(
            "bye",
            "deadline",
            "delete",
            "event",
            "find",
            "help",
            "list",
            "mark",
            "todo",
            "unmark");

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
                + "_________________________________________");
    }

    /** Prints the currently supported commands and their usage. */
    public void printHelp() {
        System.out.println("Available commands:\n"
                + "bye\n"
                + "Exits\n"
                + "deadline\n"
                + "Adds a Deadline task\n"
                + "Usage: deadline <description> /by <DD/MM/YY Optional[HH:MM]>\n"
                + "delete\n"
                + "Deletes a task\n"
                + "Usage: delete <task number>\n"
                + "event\n"
                + "Adds an Event task\n"
                + "Usage: event <description> /from <DD/MM/YY Optional[HH:MM]>"
                + " /to <DD/MM/YY Optional[HH:MM]>\n"
                + "find\n"
                + "Finds tasks whose descriptions contain a keyword\n"
                + "Usage: find <keyword>\n"
                + "help\n"
                + "Shows this help list\n"
                + "Usage: help\n"
                + "list\n"
                + "Lists all added tasks\n"
                + "Usage: list\n"
                + "Usage: list <DD/MM/YY> to list deadlines and events ending that day\n"
                + "mark\n"
                + "Marks task as done\n"
                + "Usage: mark <task number>\n"
                + "todo\n"
                + "Adds a ToDo task\n"
                + "Usage: todo <description>\n"
                + "unmark\n"
                + "Marks task as undone\n"
                + "Usage: unmark <task number>\n");
    }

    /** Prints the closing message. */
    public void printGoodbye() {
        System.out.println("_________________________________________\n"
                + "Goodbye.\n"
                + "_________________________________________");
    }

    /**
     * Returns the supported commands in alphabetical order.
     *
     * @return An immutable list of command names.
     */
    public List<String> getCommands() {
        return commands;
    }
}
