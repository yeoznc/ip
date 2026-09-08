package zinc.contact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves contacts to, and restores contacts from, Zinc's local contact file.
 */
public class Storage {
    /** The file used to retain contacts between application runs. */
    private static final Path STORAGE_FILE = Path.of("data", "zincContacts.txt");

    /**
     * Returns all contacts currently saved in the contact file.
     *
     * @return The saved contacts in their original order.
     */
    public List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        if (!Files.exists(STORAGE_FILE)) {
            return contacts;
        }

        try {
            Files.readAllLines(STORAGE_FILE).stream()
                    .map(this::parseContact)
                    .forEach(contacts::add);
        } catch (IllegalArgumentException exception) {
            System.out.println("Saved contact file has an invalid layout. Clearing saved contacts.\n");
            resetStorage();
            contacts.clear();
        } catch (IOException exception) {
            System.out.println("Unable to load saved contacts. Starting with an empty list.\n");
            resetStorage();
        }
        return contacts;
    }

    /**
     * Writes every supplied contact to the contact file.
     *
     * @param contacts The contacts to save.
     */
    public void saveContacts(List<Contact> contacts) {
        assert contacts != null : "Contacts to save must not be null";
        List<String> lines = contacts.stream()
                .map(this::formatContact)
                .toList();

        try {
            Files.createDirectories(STORAGE_FILE.getParent());
            Files.write(STORAGE_FILE, lines);
        } catch (IOException exception) {
            System.out.println("Unable to save contacts. Error: " + exception + "\n");
        }
    }

    /** Converts a stored line into a contact. */
    private Contact parseContact(String line) {
        String[] fields = line.split(" \\| ", 3);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Incorrect number of contact fields");
        }

        String name = fields[0];
        String phoneNumber = fields[1];
        String description = fields[2];
        if (name.isBlank() || (!phoneNumber.isEmpty() && !phoneNumber.matches("\\d{8}"))) {
            throw new IllegalArgumentException("Invalid contact details");
        }
        return new Contact(name, phoneNumber, description);
    }

    /** Converts a contact into the storage format. */
    private String formatContact(Contact contact) {
        return contact.getName() + " | "
                + contact.getPhoneNumber() + " | "
                + contact.getDescription();
    }

    /** Clears an invalid contact file. */
    private void resetStorage() {
        try {
            Files.write(STORAGE_FILE, List.of());
        } catch (IOException exception) {
            System.out.println("Unable to clear the invalid saved contact file.\n");
        }
    }
}
