package zinc.task;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** The description of this task. */
    private final String taskName;
    private boolean isCompleted;

    /**
     * Creates a new task.
     *
     * @param taskName The description of the task.
     */
    Task(String taskName) {
        this.taskName = taskName;
        this.isCompleted = false;
    }

    /**
     * Completes the task.
     */
    void complete() {
        this.isCompleted = true;
    }

    /**
     * Marks the task as incomplete.
     */
    void uncomplete() {
        this.isCompleted = false;
    }

    /**
     * Checks whether this task is complete.
     *
     * @return {@code true} if the task is complete; otherwise, {@code false}.
     */
    boolean isCompleted() {
        return this.isCompleted;
    }

    /** Returns the task description.*/
    public String getTaskName() {
        return taskName;
    }

    /**
     * Returns a display-friendly representation of this task and its status.
     *
     * @return {@code [X]} followed by the task name when complete, or
     *         {@code [ ]} followed by the task name when incomplete.
     */
    @Override
    public String toString() {
        if (isCompleted) {
            return "[X] " + taskName;
        } else {
            return "[ ] " + taskName;
        }
    }

    /**
     * Returns this task's category.
     *
     * @return The type of this task.
     */
    public abstract TaskType getTaskType();
}
