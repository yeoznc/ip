package zinc.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Stores up to 100 user inputs and prints them as a numbered list.
 */
public class InputList {
    /** The maximum number of inputs that can be stored. */
    private static final int MAX_INPUTS = 100;

    /** The format used when displaying a date supplied to the list command. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    /** The stored user inputs. */
    private final Task[] items = new Task[MAX_INPUTS];

    /** The number of inputs currently stored. */
    private int itemCount;

    /** The component responsible for persisting the task list. */
    private final TaskStorage storage = new TaskStorage();

    /**
     * Creates a task list and restores tasks saved by a previous run.
     */
    public InputList() {
        for (Task task : storage.loadTasks()) {
            if (itemCount == MAX_INPUTS) {
                break;
            }
            items[itemCount++] = task;
        }
    }

    /**
     * Stores a task when there is remaining space in the list.
     *
     * @param task The task to store.
     */
    public void addTask(Task task) {
        if (itemCount < MAX_INPUTS) {
            items[itemCount] = task;
            itemCount++;
            saveTasks();
        }

        System.out.println("_________________________________________\n"
                + "Task added to list:\n"
                + task.getTaskType().getDisplayIdentifier() + task.toString() + "\n"
                + "You have " + itemCount + " tasks in the list\n"
                + "_________________________________________\n");
    }

    /**
     * Prints every stored task with its list number.
     */
    public void printItems() {
        System.out.println("_________________________________________\n"
                + "Here are your current tasks:\n");
        for (int i = 0; i < itemCount; i++) {
            System.out.println((i + 1) + ". " + items[i].getTaskType().getDisplayIdentifier()
                    + items[i].toString());
        }
        System.out.println("_________________________________________\n");
    }

    /**
     * Prints deadlines due, and events ending, on the specified calendar date.
     * Todo tasks are excluded because they do not have an end date.
     *
     * @param date The date on which matching tasks end.
     */
    public void printTasksEndingOn(LocalDate date) {
        System.out.println("_________________________________________\n"
                + "Here are your tasks ending on " + date.format(DISPLAY_DATE_FORMAT) + ":\n");
        for (int i = 0; i < itemCount; i++) {
            if (endsOn(items[i], date)) {
                System.out.println((i + 1) + ". "
                        + items[i].getTaskType().getDisplayIdentifier() + items[i]);
            }
        }
        System.out.println("_________________________________________\n");
    }

    /**
     * Prints tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword The exact, case-sensitive text to search for.
     */
    public void printTasksContaining(String keyword) {
        System.out.println("_________________________________________\n"
                + "Here are your tasks containing \"" + keyword + "\":\n");
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getTaskName().contains(keyword)) {
                System.out.println((i + 1) + ". "
                        + items[i].getTaskType().getDisplayIdentifier() + items[i]);
            }
        }
        System.out.println("_________________________________________\n");
    }

    /** Returns whether a deadline or event ends on the specified date. */
    private boolean endsOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return ((Deadline) task).getDeadline().toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            return ((Event) task).getEnd().toLocalDate().equals(date);
        }
        return false;
    }

    /**
     * Changes task to a completed state
     *
     * @param index The index of the task in the 1-indexed list.
     */
    public void complete(int index) {
        if (itemCount < index || index <= 0) {
            System.out.println("No such task found\n");
            return;
        }
        items[index - 1].complete();
        saveTasks();
        System.out.println("_________________________________________\n"
                + "Task marked as done:\n"
                + items[index - 1].toString()
                + "\n_________________________________________\n");

    }

    /**
     * Changes task to a uncompleted state
     *
     * @param index The index of the task in the 1-indexed list.
     */
    public void uncomplete(int index) {
        if (itemCount < index || index <= 0) {
            System.out.println("No such task found\n");
            return;
        }
        items[index - 1].uncomplete();
        saveTasks();
        System.out.println("_________________________________________\n"
                + "Task unmarked as done:\n"
                + items[index - 1].toString()
                + "\n_________________________________________\n");
    }

    /**
     * Deletes the task at the user-facing list number.
     *
     * @param index The task number shown to the user, starting from 1.
     */
    public void delete(int index) {
        int arrayIndex = index - 1;

        if (arrayIndex < 0 || arrayIndex >= itemCount) {
            System.out.println("No such task found\n");
            return;
        }

        Task deletedTask = items[arrayIndex];

        for (int i = arrayIndex; i < itemCount - 1; i++) {
            items[i] = items[i + 1];
        }

        itemCount--;
        items[itemCount] = null;
        saveTasks();

        System.out.println("_________________________________________\n"
                + "Task deleted:\n"
                + deletedTask.getTaskType().getDisplayIdentifier() + deletedTask + "\n"
                + "You have " + itemCount + " tasks in the list\n"
                + "_________________________________________\n");
    }

    /**
     * Returns the list of tasks stored.
     *
     * @return The stored task list.
     */
    public Task[] getTasks() {
        return items;
    }

    /**
     * Returns the number of tasks stored.
     *
     * @return The number of items currently in the stored list.
     */
    public int getItemCount() {
        return itemCount;
    }

    /** Saves the current list after a task has been changed. */
    private void saveTasks() {
        storage.saveTasks(items, itemCount);
    }
}
