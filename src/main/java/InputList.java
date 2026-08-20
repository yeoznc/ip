/**
 * Stores up to 100 user inputs and prints them as a numbered list.
 */
public class InputList {
    /** The maximum number of inputs that can be stored. */
    private static final int MAX_INPUTS = 100;

    /** The stored user inputs. */
    private final Task[] items = new Task[MAX_INPUTS];

    /** The number of inputs currently stored. */
    private int itemCount;

    /**
     * Stores a task when there is remaining space in the list.
     *
     * @param task the task to store
     */
    public void addTask(Task task) {
        if (itemCount < MAX_INPUTS) {
            items[itemCount] = task;
            itemCount++;
        }
        System.out.println("\t_________________________________________\n" +
                "\tTask added to list:\n\t" +
                task.taskIdentifier() + task.toString() + "\n" +
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
            System.out.println("\t" + (i + 1) + ". " + items[i].taskIdentifier()
                    + items[i].toString());
        }
        System.out.println("\t_________________________________________\n");
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

        System.out.println("\t_________________________________________\n"
                + "\tTask deleted:\n\t"
                + deletedTask.taskIdentifier() + deletedTask + "\n"
                + "\tYou have " + itemCount + " tasks in the list\n"
                + "\t_________________________________________\n");
    }
}
