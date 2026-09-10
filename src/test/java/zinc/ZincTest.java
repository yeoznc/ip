package zinc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Zinc's terminal command loop. */
public class ZincTest {
    private static final Path TASK_STORAGE_FILE = Path.of("data", "zincTasks.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(TASK_STORAGE_FILE);
    }

    @Test
    public void main_multipleCommandsBeforeBye_processesEveryCommand() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        String commands = "todo First\ntodo Second\nlist\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            Zinc.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String terminalOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(terminalOutput.contains("2. [T][ ] Second"));
        assertTrue(terminalOutput.contains("Goodbye."));
    }
}
