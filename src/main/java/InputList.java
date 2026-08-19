/**
 * Stores up to 100 user inputs and prints them as a numbered list.
 */
public class InputList {
    /** The maximum number of inputs that can be stored. */
    private static final int MAX_INPUTS = 100;

    /** The stored user inputs. */
    private final String[] items = new String[MAX_INPUTS];

    /** The number of inputs currently stored. */
    private int itemCount;

    /**
     * Stores an input when there is remaining space in the list.
     *
     * @param input the input to store
     */
    public void add(String input) {
        if (itemCount < MAX_INPUTS) {
            items[itemCount] = input;
            itemCount++;
        }
    }

    /**
     * Prints every stored input with its list number.
     */
    public void printItems() {
        for (int i = 0; i < itemCount; i++) {
            System.out.println("\t" + (i + 1) + ". " + items[i]);
        }
    }
}
