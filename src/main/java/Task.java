/**
 * Represents a task with a description and completion status.
 *
 * <p>Tasks are immutable: methods that change the completion status return a
 * new {@code Task} instance instead of modifying the existing one.</p>
 */
public class Task {
    private final String taskName;
    private final boolean completed;

    /**
     * Creates a new task.
     *
     * @param name the description of the task
     */
    Task(String name) {
        this.taskName = name;
        this.completed = false;
    }

    /**
     * Creates a task with a specified completion status.
     *
     * @param name the description of the task
     * @param state whether the task is complete
     */
    private Task(String name, boolean state) {
        this.taskName = name;
        this.completed = state;
    }

    /**
     * Returns a completed version of this task.
     *
     * @return a new task with the same description, marked as complete
     */
    Task complete() {
        return new Task(this.taskName, true);
    }

    /**
     * Returns an incomplete version of this task.
     *
     * @return a new task with the same description, marked as incomplete
     */
    Task uncomplete() {
        return new Task(this.taskName, false);
    }

    /**
     * Checks whether this task is complete.
     *
     * @return {@code true} if the task is complete; otherwise, {@code false}
     */
    boolean isCompleted() {
        return this.completed;
    }

    /**
     * Returns a display-friendly representation of this task and its status.
     *
     * @return {@code [X]} followed by the task name when complete, or
     *         {@code [ ]} followed by the task name when incomplete
     */
    @Override
    public String toString() {
        if(completed) {
            return "[X] " + taskName;
        } else {
            return "[ ] " + taskName;
        }
    }
}
