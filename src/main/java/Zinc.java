import java.util.Scanner;

public class Zinc {
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

        do {
            input = scanner.nextLine();
            System.out.println("\t" + input);
        } while (!input.equals("bye"));

        System.out.println("_________________________________________\n" +
                "Goodbye.\n" +
                "_________________________________________");
    }
}
