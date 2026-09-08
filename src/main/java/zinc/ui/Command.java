package zinc.ui;

/** Represents a user command that can be executed with its parameters. */
@FunctionalInterface
public interface Command {
    /** Executes the command with the supplied parameters. */
    void execute(String parameters);
}
