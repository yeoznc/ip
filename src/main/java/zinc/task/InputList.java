package zinc.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import zinc.ui.Ui;

/**
 * Stores up to 100 user inputs and prints them as a numbered list.
 */
public class InputList {
    /** The maximum number of inputs that can be stored. */
    private static final int MAX_INPUTS = 100;

    /** The format used when displaying a date supplied to the list command. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");

    /** The stored user inputs. */
    private final List<Task> tasks = new ArrayList<>();

    /** The component responsible for persisting the task list. */
    private final TaskStorage storage = new TaskStorage();

    /**
     * Creates a task list and restores tasks saved by a previous run.
     */
    public InputList() {
        for (Task task : storage.loadTasks()) {
            if (tasks.size() == MAX_INPUTS) {
                break;
            }
            tasks.add(task);
        }
    }

    /**
     * Stores a task when there is remaining space in the list.
     *
     * @param task The task to store.
     */
    public void addTask(Task task) {
        assert task != null : "Task to add must not be null";
        if (tasks.size() < MAX_INPUTS) {
            tasks.add(task);
            saveTasks();
        }

        System.out.println(Ui.SEPARATOR + "\n"
                + "Task added to list:\n"
                + task.getTaskType().getDisplayIdentifier() + task.toString() + "\n"
                + "You have " + tasks.size() + " tasks in the list\n"
                + Ui.SEPARATOR + "\n");
    }

    /**
     * Prints every stored task with its list number.
     */
    public void printTasks() {
        System.out.println(Ui.SEPARATOR + "\n"
                + "Here are your current tasks:\n");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getTaskType().getDisplayIdentifier()
                    + tasks.get(i).toString());
        }
        System.out.println(Ui.SEPARATOR + "\n");
    }

    /**
     * Prints deadlines due, and events ending, on the specified calendar date.
     * Todo tasks are excluded because they do not have an end date.
     *
     * @param date The date on which matching tasks end.
     */
    public void printTasksEndingOn(LocalDate date) {
        System.out.println(Ui.SEPARATOR + "\n"
                + "Here are your tasks ending on " + date.format(DISPLAY_DATE_FORMAT) + ":\n");
        for (int i = 0; i < tasks.size(); i++) {
            if (endsOn(tasks.get(i), date)) {
                System.out.println((i + 1) + ". "
                        + tasks.get(i).getTaskType().getDisplayIdentifier() + tasks.get(i));
            }
        }
        System.out.println(Ui.SEPARATOR + "\n");
    }

    /**
     * Prints tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword The exact, case-sensitive text to search for.
     */
    public void printTasksContaining(String keyword) {
        System.out.println(Ui.SEPARATOR + "\n"
                + "Here are your tasks containing \"" + keyword + "\":\n");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskName().contains(keyword)) {
                System.out.println((i + 1) + ". "
                        + tasks.get(i).getTaskType().getDisplayIdentifier() + tasks.get(i));
            }
        }
        System.out.println(Ui.SEPARATOR + "\n");
    }

    /** Returns whether a deadline or event ends on the specified date. */
    private boolean endsOn(Task task, LocalDate date) {
        assert task != null && date != null : "Task and date must not be null";
        if (task instanceof Deadline) {
            return ((Deadline) task).getDeadline().toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            return ((Event) task).getEnd().toLocalDate().equals(date);
        }
        return false;
    }

    /**
     * Changes task to a completed state.
     *
     * @param index The index of the task in the 1-indexed list.
     */
    public void complete(int index) {
        if (tasks.size() < index || index <= 0) {
            System.out.println("No such task found\n");
            return;
        }
        tasks.get(index - 1).complete();
        saveTasks();
        System.out.println(Ui.SEPARATOR + "\n"
                + "Task marked as done:\n"
                + tasks.get(index - 1).toString()
                + "\n" + Ui.SEPARATOR + "\n");

    }

    /**
     * Changes task to an uncompleted state.
     *
     * @param index The index of the task in the 1-indexed list.
     */
    public void uncomplete(int index) {
        if (tasks.size() < index || index <= 0) {
            System.out.println("No such task found\n");
            return;
        }
        tasks.get(index - 1).uncomplete();
        saveTasks();
        System.out.println(Ui.SEPARATOR + "\n"
                + "Task unmarked as done:\n"
                + tasks.get(index - 1).toString()
                + "\n" + Ui.SEPARATOR + "\n");
    }

    /**
     * Deletes the task at the user-facing list number.
     *
     * @param index The task number shown to the user, starting from 1.
     */
    public void delete(int index) {
        int listIndex = index - 1;

        if (listIndex < 0 || listIndex >= tasks.size()) {
            System.out.println("No such task found\n");
            return;
        }

        Task deletedTask = tasks.remove(listIndex);
        saveTasks();

        System.out.println(Ui.SEPARATOR + "\n"
                + "Task deleted:\n"
                + deletedTask.getTaskType().getDisplayIdentifier() + deletedTask + "\n"
                + "You have " + tasks.size() + " tasks in the list\n"
                + Ui.SEPARATOR + "\n");
    }

    /**
     * Returns the list of tasks stored.
     *
     * @return The stored task list.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks stored.
     *
     * @return The number of items currently in the stored list.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /** Saves the current list after a task has been changed. */
    private void saveTasks() {
        assert tasks.size() <= MAX_INPUTS : "Task count must be within list bounds";
        storage.saveTasks(tasks);
    }
}
