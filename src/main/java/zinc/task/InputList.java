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
     * @param task the task to store
     */
    public void addTask(Task task) {
        if (itemCount < MAX_INPUTS) {
            items[itemCount] = task;
            itemCount++;
            saveTasks();
        }
        System.out.println("\t_________________________________________\n" +
                "\tTask added to list:\n\t" +
                task.getTaskType().getDisplayIdentifier() + task.toString() + "\n" +
                "\tYou have " + itemCount + " tasks in the list\n" +
                "\t_________________________________________\n");
    }

    /**
     * Prints every stored input with its list number.
     */
    public void printItems() {
        System.out.println("\t_________________________________________\n" +
                "\tHere are your current tasks:\n");
        for (int i = 0; i < itemCount; i++) {
            System.out.println("\t" + (i + 1) + ". " + items[i].getTaskType().getDisplayIdentifier()
                    + items[i].toString());
        }
        System.out.println("\t_________________________________________\n");
    }

    /**
     * Prints deadlines due, and events ending, on the specified calendar date.
     * Todo tasks are excluded because they do not have an end date.
     *
     * @param date the date on which matching tasks end
     */
    public void printTasksEndingOn(LocalDate date) {
        System.out.println("\t_________________________________________\n" +
                "\tHere are your tasks ending on " + date.format(DISPLAY_DATE_FORMAT) + ":\n");
        for (int i = 0; i < itemCount; i++) {
            if (endsOn(items[i], date)) {
                System.out.println("\t" + (i + 1) + ". "
                        + items[i].getTaskType().getDisplayIdentifier() + items[i]);
            }
        }
        System.out.println("\t_________________________________________\n");
    }

    /** Returns whether a deadline or event ends on the specified date. */
    private boolean endsOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return ((Deadline) task).deadline.toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            return ((Event) task).end.toLocalDate().equals(date);
        }
        return false;
    }

    /**
     * Marks task located at index completed
     */
    public void complete(int index) {
        if(itemCount < index || index <= 0) {
            System.out.println("\tNo such task found\n");
            return;
        }
        items[index - 1].complete();
        saveTasks();
        System.out.println("\t_________________________________________\n" +
                "\tTask marked as done:\n\t" +
                items[index - 1].toString() +
                "\n\t_________________________________________\n");

    }

    /**
     * Unmarks task located at index completed
     */
    public void uncomplete(int index) {
        if(itemCount < index || index <= 0) {
            System.out.println("\tNo such task found\n");
            return;
        }
        items[index - 1].uncomplete();
        saveTasks();
        System.out.println("\t_________________________________________\n" +
                "\tTask unmarked as done:\n\t" +
                items[index - 1].toString() +
                "\n\t_________________________________________\n");
    }

    /**
     * Deletes the task at the user-facing list number.
     *
     * @param index the task number shown to the user, starting from 1
     */
    public void delete(int index) {
        int arrayIndex = index - 1;

        if (arrayIndex < 0 || arrayIndex >= itemCount) {
            System.out.println("\tNo such task found\n");
            return;
        }

        Task deletedTask = items[arrayIndex];

        for (int i = arrayIndex; i < itemCount - 1; i++) {
            items[i] = items[i + 1];
        }

        itemCount--;
        items[itemCount] = null;
        saveTasks();

        System.out.println("\t_________________________________________\n"
                + "\tTask deleted:\n\t"
                + deletedTask.getTaskType().getDisplayIdentifier() + deletedTask + "\n"
                + "\tYou have " + itemCount + " tasks in the list\n"
                + "\t_________________________________________\n");
    }

    /** Saves the current list after a task has been changed. */
    private void saveTasks() {
        storage.saveTasks(items, itemCount);
    }
}
