package zinc.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zinc.task.Deadline;
import zinc.task.Event;
import zinc.task.InputList;
import zinc.task.Todo;

/** Tests command parsing and the tasks created by recognised commands. */
public class ParserTest {
    private static final Path STORAGE_FILE = Path.of("data", "zinc.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void parse_bye_returnsTrue() {
        Parser parser = new Parser(new InputList());

        assertTrue(parser.parse("bye"));
    }

    @Test
    public void parse_byeWithParameters_returnsFalse() {
        Parser parser = new Parser(new InputList());

        assertFalse(parser.parse("bye now"));
    }

    @Test
    public void parse_todo_addToListSuccess() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("todo Buy bread");

        assertEquals("Buy bread", inputList.getTasks()[0].taskName);
        assertTrue(inputList.getTasks()[0] instanceof Todo);
    }

    @Test
    public void parse_todoWithoutDescription_addToListFailure() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("todo");

        assertEquals(0, inputList.getItemCount());
    }

    @Test
    public void parseDeadline_onlyDateSuccess() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("deadline Submit report /by 27/08/26");

        Deadline deadline = (Deadline) inputList.getTasks()[0];
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), deadline.deadline);
    }

    @Test
    public void parseDeadline_dateAndTimeSuccess() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("deadline Submit report /by 27/08/26 1830");

        Deadline deadline = (Deadline) inputList.getTasks()[0];
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), deadline.deadline);
    }

    @Test
    public void parseDeadline_invalidDateFailure() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("deadline Submit report /by 31/02/26");

        assertEquals(0, inputList.getItemCount());
    }

    @Test
    public void parse_event_addToListSuccess() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);

        parser.parse("event Meeting /from 27/08/26 1000 /to 27/08/26 1100");

        Event event = (Event) inputList.getTasks()[0];
        assertEquals(LocalDateTime.of(2026, 8, 27, 10, 0), event.start);
        assertEquals(LocalDateTime.of(2026, 8, 27, 11, 0), event.end);
    }

    @Test
    public void parse_markAndUnmark_success() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);
        parser.parse("todo Buy bread");

        parser.parse("mark 1");
        assertTrue(inputList.getTasks()[0].toString().startsWith("[X]"));

        parser.parse("unmark 1");
        assertFalse(inputList.getTasks()[0].toString().startsWith("[X]"));
    }

    @Test
    public void parse_delete_success() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);
        parser.parse("todo Buy bread");

        parser.parse("delete 1");

        assertEquals(0, inputList.getItemCount());
    }

    @Test
    public void parse_find_searchesTaskDescriptions() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);
        parser.parse("todo Buy bread");
        parser.parse("todo Read book");

        parser.parse("find bread");

        assertEquals("Buy bread", inputList.getTasks()[0].taskName);
        assertEquals("Read book", inputList.getTasks()[1].taskName);
    }

    @Test
    public void parse_findWithoutKeyword_doesNotChangeList() {
        InputList inputList = new InputList();
        Parser parser = new Parser(inputList);
        parser.parse("todo Buy bread");

        parser.parse("find");

        assertEquals(1, inputList.getItemCount());
    }
}
