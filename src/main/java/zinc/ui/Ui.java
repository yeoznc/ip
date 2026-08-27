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
        System.out.println("\tAvailable commands:\n"
                + "\tbye\n"
                + "\t\tExits\n"
                + "\tdeadline\n"
                + "\t\tAdds a Deadline task\n"
                + "\t\tUsage: deadline <description> /by <DD/MM/YY Optional[HH:MM]>\n"
                + "\tdelete\n"
                + "\t\tDeletes a task\n"
                + "\t\tUsage: delete <task number>\n"
                + "\tevent\n"
                + "\t\tAdds an Event task\n"
                + "\t\tUsage: event <description> /from <DD/MM/YY Optional[HH:MM]>"
                + " /to <DD/MM/YY Optional[HH:MM]>\n"
                + "\tfind\n"
                + "\t\tFinds tasks whose descriptions contain a keyword\n"
                + "\t\tUsage: find <keyword>\n"
                + "\thelp\n"
                + "\t\tShows this help list\n"
                + "\t\tUsage: help\n"
                + "\tlist\n"
                + "\t\tLists all added tasks\n"
                + "\t\tUsage: list\n"
                + "\t\tUsage: list <DD/MM/YY> to list deadlines and events ending that day\n"
                + "\tmark\n"
                + "\t\tMarks task as done\n"
                + "\t\tUsage: mark <task number>\n"
                + "\ttodo\n"
                + "\t\tAdds a ToDo task\n"
                + "\t\tUsage: todo <description>\n"
                + "\tunmark\n"
                + "\t\tMarks task as undone\n"
                + "\t\tUsage: unmark <task number>\n");
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
     * @return an immutable list of command names
     */
    public List<String> getCommands() {
        return commands;
    }
}
