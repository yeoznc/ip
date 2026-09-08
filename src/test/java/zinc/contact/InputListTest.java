package zinc.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests adding, updating, and deleting contacts. */
public class InputListTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincContacts.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void addContact_withOptionalFields_success() {
        InputList inputList = new InputList();

        inputList.addContact(new Contact("Tom", "", ""));

        assertEquals("Tom", inputList.getContacts().get(0).getName());
        assertEquals("", inputList.getContacts().get(0).getPhoneNumber());
    }

    @Test
    public void update_existingContact_success() {
        InputList inputList = new InputList();
        inputList.addContact(new Contact("Tom", "91234567", "Friend"));

        inputList.updateContact("Tom", "Tommy", "94449999", "Best friend");

        Contact updatedContact = inputList.getContacts().get(0);
        assertEquals("Tommy", updatedContact.getName());
        assertEquals("94449999", updatedContact.getPhoneNumber());
        assertEquals("Best friend", updatedContact.getDescription());
    }

    @Test
    public void delete_existingContact_success() {
        InputList inputList = new InputList();
        inputList.addContact(new Contact("Tom", "91234567", "Friend"));

        inputList.deleteContact("Tom");

        assertEquals(0, inputList.getContactCount());
    }
}
