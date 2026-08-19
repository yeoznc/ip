import java.util.Scanner;

public class Zinc {
    /**
     * Starts Zinc and handles terminal input until the user enters {@code bye}.
     * Enter {@code list} to display all previously stored inputs.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ______ _            \n"
                + "|___  /(_)           \n"
                + "   / /  _ _ __   ___ \n"
                + "  / /  | | '_ \\ / __|\n"
                + " / /___| | | | | (__ \n"
                + "/______|_|_| |_|\\___|\n";
        System.out.println(banner);
        System.out.println("Hello, my name's Zinc.\n" +
                "What can I do for you?\n" +
                "_________________________________________");

        Scanner scanner = new Scanner(System.in);
        String input;
        InputList inputs = new InputList();

        do {
            input = scanner.nextLine();
            if (input.equals("list")) {
                inputs.printItems();
            } else if (input.startsWith("mark")) {
                inputs.complete(Integer.parseInt(input.replaceAll("[^0-9]", "")));
            } else if (input.startsWith("unmark")) {
                inputs.uncomplete(Integer.parseInt(input.replaceAll("[^0-9]", "")));
            } else if (input.startsWith("todo")) {
                inputs.addTask(new Todo(input.substring(4).trim()));
            } else if (input.startsWith("deadline")) {
                String[] parameters = input.split("/by");
                inputs.addTask(new Deadline(parameters[0].substring(8).trim(), parameters[1].trim()));
            } else if (input.startsWith("event")) {
                String[] parameters = input.split("/from|/to");
                inputs.addTask(new Event(parameters[0].substring(5).trim(), parameters[1].trim(), parameters[2].trim()));
            } else if (!input.equals("bye")) {
                System.out.println("\tSorry, I don't know what you mean.\n");
            }
        } while (!input.equals("bye"));

        System.out.println("_________________________________________\n" +
                "Goodbye.\n" +
                "_________________________________________");
    }
}
