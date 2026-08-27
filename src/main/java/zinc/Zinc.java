package zinc;

import java.util.Scanner;

import zinc.task.InputList;
import zinc.ui.Parser;
import zinc.ui.Ui;

public class Zinc {
    /**
     * Starts Zinc and handles terminal input until the user enters {@code bye}.
     * Enter {@code list} to display all previously stored inputs.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.printBanner();
        ui.printGreeting();

        Scanner scanner = new Scanner(System.in);
        InputList inputs = new InputList();
        Parser parser = new Parser(inputs, ui);

        while (!parser.parse(scanner.nextLine())) {}

        ui.printGoodbye();
    }
}
