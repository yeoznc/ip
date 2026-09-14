package zinc.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import zinc.Zinc;
import zinc.ui.BackgroundType;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    /** The start of the daytime background period. */
    private static final LocalTime DAY_START_TIME = LocalTime.of(6, 0);

    /** The start of the evening background period. */
    private static final LocalTime EVENING_START_TIME = LocalTime.of(18, 0);

    /** The start of the nighttime background period. */
    private static final LocalTime NIGHT_START_TIME = LocalTime.of(22, 0);

    /** The background used during the daytime period. */
    private static final String DAY_BACKGROUND_STYLE_CLASS = "background-morning";

    /** The background used during the evening period. */
    private static final String EVENING_BACKGROUND_STYLE_CLASS = "background-sunset";

    /** The background used during the nighttime period. */
    private static final String NIGHT_BACKGROUND_STYLE_CLASS = "background-night";

    /** The sidebar style class used during the daytime period. */
    private static final String DAY_SIDEBAR_STYLE_CLASS = "sidebar-morning";

    /** The sidebar style class used during the evening period. */
    private static final String EVENING_SIDEBAR_STYLE_CLASS = "sidebar-sunset";

    /** The sidebar style class used during the nighttime period. */
    private static final String NIGHT_SIDEBAR_STYLE_CLASS = "sidebar-night";

    /** The CSS classes managed when the background changes. */
    private static final String[] BACKGROUND_STYLE_CLASSES = {
        "background-morning",
        "background-sunset",
        "background-night"
    };

    /** The sidebar style classes managed when the background changes. */
    private static final String[] SIDEBAR_STYLE_CLASSES = {
        DAY_SIDEBAR_STYLE_CLASS,
        EVENING_SIDEBAR_STYLE_CLASS,
        NIGHT_SIDEBAR_STYLE_CLASS
    };

    /** The delay before Zinc exits after displaying its goodbye message. */
    private static final Duration EXIT_DELAY = Duration.seconds(2);

    /** The command that exits Zinc. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that displays Zinc's help message. */
    private static final String HELP_COMMAND = "help";

    /** The command that displays every saved contact. */
    private static final String LIST_CONTACTS_COMMAND = "ct ls";

    /** The command that displays every saved task. */
    private static final String LIST_TASK_COMMAND = "ls";

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

    /** The navigation panel whose colour follows the active background. */
    @FXML
    private VBox sidebar;

    /** The pane behind the conversation and its controls. */
    @FXML
    private BorderPane chatPane;

    /** The field in which the user enters commands. */
    @FXML
    private TextField userInput;

    /** The application instance used to process commands. */
    private Zinc zinc;

    /**
     * Initializes the center pane.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((ignoredObservable, ignoredOldHeight, ignoredNewHeight) -> {
            Platform.runLater(() -> scrollPane.setVvalue(BOTTOM_SCROLL_POSITION));
        });
        applyCurrentBackground();
        chatPane.sceneProperty().addListener((ignoredObservable, ignoredOldScene, newScene) ->
                observeWindowFocus(newScene));
    }

    /** Refreshes the background selection using the current local time. */
    private void applyCurrentBackground() {
        LocalTime currentTime = LocalTime.now();
        BackgroundType selectedBackgroundType = zinc == null ? BackgroundType.AUTO : zinc.getBackgroundType();
        String backgroundStyleClass = getBackgroundStyleClass(currentTime, selectedBackgroundType);
        String sidebarStyleClass = getSidebarStyleClass(backgroundStyleClass);
        chatPane.getStyleClass().removeAll(BACKGROUND_STYLE_CLASSES);
        chatPane.getStyleClass().add(backgroundStyleClass);
        sidebar.getStyleClass().removeAll(SIDEBAR_STYLE_CLASSES);
        sidebar.getStyleClass().add(sidebarStyleClass);
    }

    /** Re-evaluates the background whenever the main window regains focus. */
    private void observeWindowFocus(Scene scene) {
        if (scene == null) {
            return;
        }

        scene.windowProperty().addListener((ignoredObservable, ignoredOldWindow, newWindow) -> {
            if (newWindow != null) {
                newWindow.focusedProperty().addListener((ignoredFocusObservable, ignoredWasFocused, isFocused) -> {
                    if (isFocused) {
                        applyCurrentBackground();
                    }
                });
            }
        });

        if (scene.getWindow() != null) {
            scene.getWindow().focusedProperty().addListener((ignoredObservable, ignoredWasFocused, isFocused) -> {
                if (isFocused) {
                    applyCurrentBackground();
                }
            });
        }
    }

    /**
     * Injects the Zinc instance and displays its startup message.
     *
     * @param zincInstance The application instance used to process commands.
     */
    public void setZinc(Zinc zincInstance) {
        zinc = zincInstance;
        appendZincDialog(zinc.getGreetingResponse());
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

        appendUserDialog(input);
        userInput.clear();

        if (isHelpCommand(input)) {
            showHelp();
            return;
        }
        if (input.equals(EXIT_COMMAND)) {
            exitProgram();
            return;
        }

        String response = zinc.processCommand(input);
        appendZincDialog(response);
        applyCurrentBackground();
    }

    /** Displays Zinc's command reference. */
    @FXML
    private void showHelp() {
        new HelpWindow().show(userInput.getScene().getWindow());
    }

    /** Displays every saved contact. */
    @FXML
    private void showContactList() {
        appendZincDialog(zinc.processCommand(LIST_CONTACTS_COMMAND));
    }

    /** Displays every saved task. */
    @FXML
    private void showTaskList() {
        appendZincDialog(zinc.processCommand(LIST_TASK_COMMAND));
    }

    /** Clears the conversation and displays a fresh prompt. */
    @FXML
    private void startNewConversation() {
        dialogContainer.getChildren().clear();
        appendZincDialog(zinc.getConversationPromptResponse());
    }

    /** Displays a goodbye message and exits after a short delay. */
    @FXML
    private void exitProgram() {
        appendZincDialog(zinc.getGoodbyeResponse());

        PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
        exitPause.setOnFinished(ignoredEvent -> Platform.exit());
        exitPause.play();
    }

    /** Adds a Zinc-authored message to the conversation. */
    private void appendZincDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.createZincDialog(message, zincImage));
    }

    /** Adds a user-authored message to the conversation. */
    private void appendUserDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.createUserDialog(message, userImage));
    }

    /** Returns the CSS background class for a supplied local time. */
    static String getBackgroundStyleClass(LocalTime time) {
        assert time != null : "Time must not be null";

        if (time.isAfter(DAY_START_TIME) && time.isBefore(EVENING_START_TIME)) {
            return DAY_BACKGROUND_STYLE_CLASS;
        }
        if (time.isAfter(EVENING_START_TIME) && time.isBefore(NIGHT_START_TIME)) {
            return EVENING_BACKGROUND_STYLE_CLASS;
        }
        return NIGHT_BACKGROUND_STYLE_CLASS;
    }

    /** Returns the background CSS class for the selected mode and current time. */
    static String getBackgroundStyleClass(LocalTime time, BackgroundType selectedBackgroundType) {
        assert time != null && selectedBackgroundType != null : "Background inputs must not be null";
        if (selectedBackgroundType == BackgroundType.AUTO) {
            return getBackgroundStyleClass(time);
        }
        return switch (selectedBackgroundType) {
            case MORNING -> DAY_BACKGROUND_STYLE_CLASS;
            case EVENING -> EVENING_BACKGROUND_STYLE_CLASS;
            case NIGHT -> NIGHT_BACKGROUND_STYLE_CLASS;
            case AUTO -> throw new IllegalStateException("Automatic background should be handled earlier");
        };
    }

    /** Returns the sidebar CSS class for a supplied local time. */
    static String getSidebarStyleClass(LocalTime time) {
        assert time != null : "Time must not be null";

        return switch (getBackgroundStyleClass(time)) {
            case DAY_BACKGROUND_STYLE_CLASS -> DAY_SIDEBAR_STYLE_CLASS;
            case EVENING_BACKGROUND_STYLE_CLASS -> EVENING_SIDEBAR_STYLE_CLASS;
            case NIGHT_BACKGROUND_STYLE_CLASS -> NIGHT_SIDEBAR_STYLE_CLASS;
            default -> throw new IllegalStateException("Unknown background style class");
        };
    }

    /** Returns the sidebar CSS class matching a background CSS class. */
    private static String getSidebarStyleClass(String backgroundStyleClass) {
        return switch (backgroundStyleClass) {
            case DAY_BACKGROUND_STYLE_CLASS -> DAY_SIDEBAR_STYLE_CLASS;
            case EVENING_BACKGROUND_STYLE_CLASS -> EVENING_SIDEBAR_STYLE_CLASS;
            case NIGHT_BACKGROUND_STYLE_CLASS -> NIGHT_SIDEBAR_STYLE_CLASS;
            default -> throw new IllegalStateException("Unknown background style class");
        };
    }

    /** Returns whether the supplied input uses the help command word. */
    static boolean isHelpCommand(String input) {
        assert input != null : "Command input must not be null";
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return false;
        }
        return trimmedInput.split("\\s+", 2)[0].equals(HELP_COMMAND);
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
