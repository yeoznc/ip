package zinc.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests adding, completing, and deleting tasks in an input list. */
public class InputListTest {
    private static final Path STORAGE_FILE = Path.of("data", "zinc.txt");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void addTask_todo_success() {
        InputList inputList = new InputList();
        Todo todo = new Todo("Buy bread");

        inputList.addTask(todo);

        assertEquals(todo, inputList.getTasks()[0]);
    }

    @Test
    public void addTask_whileFull_success() {
        InputList inputList = new InputList();
        for (int i = 0; i < 101; i++) {
            inputList.addTask(new Todo("Buy bread"));
        }

        assertEquals(100, inputList.getItemCount());
    }

    @Test
    public void addDeadline_onlyDateSuccess() {
        InputList inputList = new InputList();

        LocalDate date = LocalDate.parse("2030-12-26", DATE_FORMAT);
        Deadline deadline = new Deadline("Buy bread", date.atStartOfDay());

        inputList.addTask(deadline);

        assertEquals(deadline, inputList.getTasks()[0]);
    }

    @Test
    public void addDeadline_dateAndTimeSuccess() {
        InputList inputList = new InputList();
        Deadline deadline = new Deadline("Buy bread", LocalDateTime.parse("2030-12-26T10:30"));

        inputList.addTask(deadline);

        assertEquals(deadline, inputList.getTasks()[0]);
    }

    @Test
    public void addEvent_onlyDateSuccess() {
        LocalDate startDate = LocalDate.parse("2030-12-26", DATE_FORMAT);
        LocalDate endDate = LocalDate.parse("2030-12-27", DATE_FORMAT);

        Event event = new Event("Buy bread", startDate.atStartOfDay(), endDate.atStartOfDay());

        InputList inputList = new InputList();
        inputList.addTask(event);

        assertEquals(event, inputList.getTasks()[0]);
    }

    @Test
    public void addEvent_dateAndTimeSuccess() {
        InputList inputList = new InputList();
        Event event = new Event("Buy bread", LocalDateTime.parse("2030-12-26T10:30"),
                LocalDateTime.parse("2030-12-27T20:00"));

        inputList.addTask(event);

        assertEquals(event, inputList.getTasks()[0]);
    }

    @Test
    public void complete_success() {
        InputList inputList = new InputList();
        inputList.addTask(new Todo("Buy bread"));

        inputList.complete(1);

        assertTrue(inputList.getTasks()[0].isCompleted());
    }

    @Test
    public void uncomplete_success() {
        InputList inputList = new InputList();
        inputList.addTask(new Todo("Buy bread"));
        inputList.complete(1);

        inputList.uncomplete(1);

        assertFalse(inputList.getTasks()[0].isCompleted());
    }

    @Test
    public void delete_notLastAddedTask_success() {
        InputList inputList = new InputList();
        inputList.addTask(new Todo("First"));
        inputList.addTask(new Todo("Second"));

        inputList.delete(1);

        assertEquals("Second", inputList.getTasks()[0].getTaskName());
        assertEquals(null, inputList.getTasks()[1]);
    }

    @Test
    public void delete_lastAddedTask_success() {
        InputList inputList = new InputList();
        inputList.addTask(new Todo("First"));
        inputList.addTask(new Todo("Second"));

        inputList.delete(2);

        assertEquals("First", inputList.getTasks()[0].getTaskName());
        assertEquals(null, inputList.getTasks()[1]);
    }

    @Test
    public void printTasksEndingOn_success() {
        InputList inputList = new InputList();
        LocalDateTime deadline = LocalDateTime.of(2026, 8, 27, 18, 0);
        LocalDateTime deadline2 = LocalDateTime.of(2026, 8, 28, 18, 0);
        inputList.addTask(new Deadline("Submit report", deadline));
        inputList.addTask(new Deadline("Don't do report", deadline2));

        inputList.printTasksEndingOn(LocalDate.of(2026, 8, 27));

        assertEquals("Submit report", inputList.getTasks()[0].getTaskName());
    }

    @Test
    public void printTasksContaining_success() {
        InputList inputList = new InputList();
        inputList.addTask(new Todo("Buy bread"));
        inputList.addTask(new Todo("Read book"));

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            inputList.printTasksContaining("bread");
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Buy bread"));
        assertFalse(output.toString().contains("Read book"));
    }
}
