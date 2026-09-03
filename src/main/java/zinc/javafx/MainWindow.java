package zinc.javafx;


import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Zinc zinc;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user-avatar.png"));
    private Image zincImage = new Image(this.getClass().getResourceAsStream("/images/zinc-avatar.png"));

    /**
     * Initializes the center pane and prints a greeting.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox
                .getZincDialog("Hi, I’m Zinc. What should we do today?", zincImage));
    }

    /** Injects the Zinc instance */
    public void setZinc(Zinc z) {
        zinc = z;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        if (input.equals("bye")) {
            this.exitProgram();
        }
        String response = zinc.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getZincDialog(response, zincImage)
        );
        userInput.clear();
    }

    @FXML
    private void printHelp(javafx.event.ActionEvent event) {
        String response = zinc.getResponse("help");
        dialogContainer.getChildren().addAll(
                DialogBox.getZincDialog(response, zincImage)
        );
    }

    @FXML
    private void newConversation() {
        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().add(DialogBox.getZincDialog(
                "What's on your mind?", zincImage));
    }

    @FXML
    private void exitProgram() {
        dialogContainer.getChildren().add(
                DialogBox.getZincDialog("Goodbye", zincImage)
        );

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
