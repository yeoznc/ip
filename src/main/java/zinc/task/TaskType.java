package zinc.task;

/**
 * The supported categories of tasks and their list display identifiers.
 */
public enum TaskType {
    TODO("[T]"),
    DEADLINE("[D]"),
    EVENT("[E]");

    private final String displayIdentifier;

    /**
     * Creates a task type with its display identifier.
     *
     * @param displayIdentifier The prefix shown before a task in the list.
     */
    TaskType(String displayIdentifier) {
        this.displayIdentifier = displayIdentifier;
    }

    /**
     * Returns the prefix displayed before tasks of this type.
     *
     * @return The display identifier.
     */
    public String getDisplayIdentifier() {
        return displayIdentifier;
    }
}
