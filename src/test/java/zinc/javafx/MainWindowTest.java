package zinc.javafx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command handling decisions made by the main JavaFX window. */
public class MainWindowTest {
    @Test
    public void isHelpCommand_helpWithWhitespaceAndParameters_returnsTrue() {
        assertTrue(MainWindow.isHelpCommand("help"));
        assertTrue(MainWindow.isHelpCommand("  help  "));
        assertTrue(MainWindow.isHelpCommand("help todo"));
    }

    @Test
    public void isHelpCommand_nonHelpCommands_returnsFalse() {
        assertFalse(MainWindow.isHelpCommand(""));
        assertFalse(MainWindow.isHelpCommand("helpdesk"));
        assertFalse(MainWindow.isHelpCommand("Help"));
        assertFalse(MainWindow.isHelpCommand("todo help"));
    }
}
