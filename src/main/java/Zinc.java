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
        InputList inputs = new InputList();
        Parser parser = new Parser(inputs);

        while (!parser.parse(scanner.nextLine())) {}

        System.out.println("_________________________________________\n" +
                "Goodbye.\n" +
                "_________________________________________");
    }
}
