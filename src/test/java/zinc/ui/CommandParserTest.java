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
import zinc.task.TaskList;
import zinc.task.Todo;

/** Tests command parsing and the tasks created by recognised commands. */
public class CommandParserTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincTasks.txt");
    private static final Path CONTACT_STORAGE_FILE = Path.of("data", "zincContacts.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
        Files.deleteIfExists(CONTACT_STORAGE_FILE);
    }

    @Test
    public void parseCommand_bye_returnsTrue() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertTrue(commandParser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_byeWithParameters_returnsFalse() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertFalse(commandParser.parseCommand("bye now"));
    }

    @Test
    public void parseCommand_emptyInput_returnsFalse() {
        CommandParser commandParser = new CommandParser(new TaskList());

        assertFalse(commandParser.parseCommand("   "));
    }

    @Test
    public void parseCommand_validTodo_addsTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("todo Buy bread");

        assertEquals("Buy bread", taskList.getTasks().get(0).getDescription());
        assertTrue(taskList.getTasks().get(0) instanceof Todo);
    }

    @Test
    public void parseCommand_todoWithoutDescription_doesNotAddTodo() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("todo");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_deadlineWithOnlyDate_addsDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 27/08/26");

        Deadline deadline = (Deadline) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), deadline.getDeadline());
    }

    @Test
    public void parseCommand_deadlineWithDateAndTime_addsDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 27/08/26 1830");

        Deadline deadline = (Deadline) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), deadline.getDeadline());
    }

    @Test
    public void parseCommand_deadlineWithInvalidDate_doesNotAddDeadline() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("deadline Submit report /by 31/02/26");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_validEvent_addsEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /from 27/08/26 1000 /to 27/08/26 1100");

        Event event = (Event) taskList.getTasks().get(0);
        assertEquals(LocalDateTime.of(2026, 8, 27, 10, 0), event.getStart());
        assertEquals(LocalDateTime.of(2026, 8, 27, 11, 0), event.getEnd());
    }

    @Test
    public void parseCommand_eventEndsBeforeItStarts_doesNotAddEvent() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);

        commandParser.parseCommand("event Meeting /from 27/08/26 1100 /to 27/08/26 1000");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_markAndUnmark_updatesCompletionStatus() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("mark 1");
        assertTrue(taskList.getTasks().get(0).toString().startsWith("[X]"));

        commandParser.parseCommand("unmark 1");
        assertFalse(taskList.getTasks().get(0).toString().startsWith("[X]"));
    }

    @Test
    public void parseCommand_delete_removesTask() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("delete 1");

        assertEquals(0, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_find_searchesTaskDescriptions() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");
        commandParser.parseCommand("todo Read book");

        commandParser.parseCommand("find bread");

        assertEquals("Buy bread", taskList.getTasks().get(0).getDescription());
        assertEquals("Read book", taskList.getTasks().get(1).getDescription());
    }

    @Test
    public void parseCommand_findWithoutKeyword_doesNotChangeList() {
        TaskList taskList = new TaskList();
        CommandParser commandParser = new CommandParser(taskList);
        commandParser.parseCommand("todo Buy bread");

        commandParser.parseCommand("find");

        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    public void parseCommand_contactAddWithAllFields_addsContact() {
        TaskList taskList = new TaskList();
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(taskList, contactList);

        commandParser.parseCommand("contact add /n Tom /p 91234567 /d Friend");

        assertEquals("Tom", contactList.getContacts().get(0).getName());
        assertEquals("91234567", contactList.getContacts().get(0).getPhoneNumber());
        assertEquals("Friend", contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactAddWithInvalidPhone_doesNotAddContact() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);

        commandParser.parseCommand("contact add /n Tom /p 1234");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_contactUpdateWithSomeFields_retainsOtherFields() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom /p 91234567 /d Friend");

        commandParser.parseCommand("contact update Tom /n Tommy /d Best friend");

        assertEquals("Tommy", contactList.getContacts().get(0).getName());
        assertEquals("91234567", contactList.getContacts().get(0).getPhoneNumber());
        assertEquals("Best friend", contactList.getContacts().get(0).getDescription());
    }

    @Test
    public void parseCommand_contactDeleteAliases_deleteContacts() {
        zinc.contact.ContactList contactList = new zinc.contact.ContactList();
        CommandParser commandParser = new CommandParser(new TaskList(), contactList);
        commandParser.parseCommand("contact add /n Tom");
        commandParser.parseCommand("contact add /n Jane");

        commandParser.parseCommand("contact del /n Tom");
        commandParser.parseCommand("contact delete /n Jane");

        assertEquals(0, contactList.getContactCount());
    }

    @Test
    public void parseCommand_uiBackgroundAliases_selectBackground() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);

        commandParser.parseCommand("ui background morning");
        assertEquals(BackgroundType.MORNING, ui.getBackgroundType());

        commandParser.parseCommand("ui bg night");
        assertEquals(BackgroundType.NIGHT, ui.getBackgroundType());
    }

    @Test
    public void parseCommand_uiBackgroundAuto_restoresAutomaticSelection() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);
        commandParser.parseCommand("ui bg evening");

        commandParser.parseCommand("ui bg auto");

        assertEquals(BackgroundType.AUTO, ui.getBackgroundType());
    }

    @Test
    public void parseCommand_uiBackgroundWithInvalidArguments_keepsPreviousSelection() {
        Ui ui = new Ui();
        CommandParser commandParser = new CommandParser(new TaskList(), new zinc.contact.ContactList(), ui);
        commandParser.parseCommand("ui bg morning");

        commandParser.parseCommand("ui background dawn");
        commandParser.parseCommand("ui background night extra");

        assertEquals(BackgroundType.MORNING, ui.getBackgroundType());
    }
}
