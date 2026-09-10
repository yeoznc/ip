package zinc.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import zinc.Zinc;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    /** The delay before Zinc exits after displaying its goodbye message. */
    private static final Duration EXIT_DELAY = Duration.seconds(2);

    /** The command that exits Zinc. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that displays Zinc's help message. */
    private static final String HELP_COMMAND = "help";

    /** The command that displays every saved contact. */
    private static final String LIST_CONTACTS_COMMAND = "ct ls";

    /** The scroll position representing the bottom of the conversation. */
    private static final double BOTTOM_SCROLL_POSITION = 1.0;

    /** The image displayed beside user messages. */
    private final Image userImage = loadImage("/images/user-avatar.png");

    /** The image displayed beside Zinc messages. */
    private final Image zincImage = loadImage("/images/zinc-avatar.png");

    /** The scrollable region containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The container holding dialog boxes in display order. */
    @FXML
    private VBox dialogContainer;

    /** The field in which the user enters commands. */
    @FXML
    private TextField userInput;

    /** The application instance used to process commands. */
    private Zinc zinc;

    /**
     * Initializes the center pane and prints a greeting.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((ignoredObservable, ignoredOldHeight, ignoredNewHeight) -> {
            Platform.runLater(() -> scrollPane.setVvalue(BOTTOM_SCROLL_POSITION));
        });
        appendZincDialog("Hi, I’m Zinc. What should we do today?");
    }

    /**
     * Injects the Zinc instance.
     *
     * @param zincInstance The application instance used to process commands.
     */
    public void setZinc(Zinc zincInstance) {
        zinc = zincInstance;
    }

    /**
     * Displays the user's command and Zinc's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        if (input.equals(EXIT_COMMAND)) {
            exitProgram();
            userInput.clear();
            return;
        }
        String response = zinc.processCommand(input);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, userImage),
                DialogBox.createZincDialog(response, zincImage));
        userInput.clear();
    }

    /** Displays Zinc's command reference. */
    @FXML
    private void showHelp() {
        appendZincDialog(zinc.processCommand(HELP_COMMAND));
    }

    /** Displays every saved contact. */
    @FXML
    private void showContactList() {
        appendZincDialog(zinc.processCommand(LIST_CONTACTS_COMMAND));
    }

    /** Clears the conversation and displays a fresh prompt. */
    @FXML
    private void startNewConversation() {
        dialogContainer.getChildren().clear();
        appendZincDialog("What's on your mind?");
    }

    /** Displays a goodbye message and exits after a short delay. */
    @FXML
    private void exitProgram() {
        appendZincDialog("Goodbye");

        PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
        exitPause.setOnFinished(ignoredEvent -> Platform.exit());
        exitPause.play();
    }

    /** Adds a Zinc-authored message to the conversation. */
    private void appendZincDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.createZincDialog(message, zincImage));
    }

    /** Loads an image resource required by the main window. */
    private static Image loadImage(String resourcePath) {
        try (InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath), "Missing image resource: " + resourcePath)) {
            return new Image(imageStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load image resource: " + resourcePath, exception);
        }
    }
}
