package zinc.contact;

import java.util.ArrayList;
import java.util.List;

import zinc.ui.Ui;

/**
 * Maintains and persists the user's contacts.
 */
public class ContactList {
    /** The stored contacts. */
    private final List<Contact> contacts = new ArrayList<>();

    /** The component responsible for persisting contacts. */
    private final ContactStorage contactStorage = new ContactStorage();

    /** Creates a contact list and restores contacts saved by a previous run. */
    public ContactList() {
        contacts.addAll(contactStorage.loadContacts());
    }

    /**
     * Adds and saves a contact.
     *
     * @param contact The contact to add.
     */
    public void addContact(Contact contact) {
        assert contact != null : "Contact to add must not be null";
        contacts.add(contact);
        saveContacts();
        System.out.println(Ui.SEPARATOR + "\n"
                + "Contact added:\n"
                + contact + "\n"
                + Ui.SEPARATOR + "\n");
    }

    /**
     * Deletes and saves the first contact whose name exactly matches the supplied name.
     *
     * @param name The name of the contact to delete.
     */
    public void deleteContact(String name) {
        int contactIndex = findContactIndex(name);
        if (contactIndex < 0) {
            System.out.println("No contact named \"" + name + "\" found.\n");
            return;
        }

        Contact deletedContact = contacts.remove(contactIndex);
        saveContacts();
        System.out.println(Ui.SEPARATOR + "\n"
                + "Contact deleted:\n"
                + deletedContact + "\n"
                + Ui.SEPARATOR + "\n");
    }

    /**
     * Updates and saves the first contact whose name exactly matches the supplied name.
     * A {@code null} replacement retains that field's current value.
     *
     * @param currentName The current name used to locate the contact.
     * @param updatedName The replacement name, or {@code null} to retain it.
     * @param updatedPhoneNumber The replacement phone number, or {@code null} to retain it.
     * @param updatedDescription The replacement description, or {@code null} to retain it.
     */
    public void updateContact(String currentName, String updatedName, String updatedPhoneNumber,
                              String updatedDescription) {
        int contactIndex = findContactIndex(currentName);
        if (contactIndex < 0) {
            System.out.println("No contact named \"" + currentName + "\" found.\n");
            return;
        }

        Contact existingContact = contacts.get(contactIndex);
        String replacementName = updatedName == null ? existingContact.getName() : updatedName;
        String replacementPhoneNumber = updatedPhoneNumber == null
                ? existingContact.getPhoneNumber() : updatedPhoneNumber;
        String replacementDescription = updatedDescription == null
                ? existingContact.getDescription() : updatedDescription;
        Contact updatedContact = existingContact.withUpdatedDetails(replacementName, replacementPhoneNumber,
                replacementDescription);
        contacts.set(contactIndex, updatedContact);
        saveContacts();
        System.out.println(Ui.SEPARATOR + "\n"
                + "Contact updated:\n"
                + updatedContact + "\n"
                + Ui.SEPARATOR + "\n");
    }

    /**
     * Returns a defensive copy of the stored contacts.
     *
     * @return All stored contacts.
     */
    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    /** Prints all contacts currently stored. */
    public void printContacts() {
        if (contacts.isEmpty()) {
            System.out.println("You have no contacts :(\n");
            return;
        }
        contacts.forEach(System.out::println);
    }

    /**
     * Prints all contacts whose names exactly match the supplied name.
     *
     * @param name The exact, case-sensitive name to match.
     */
    public void printContactsNamed(String name) {
        contacts.stream()
                .filter(contact -> contact.getName().equals(name))
                .forEach(System.out::println);
    }
    /**
     * Returns the number of stored contacts.
     *
     * @return The contact count.
     */
    public int getContactCount() {
        return contacts.size();
    }

    /** Finds the first contact with an exact, case-sensitive name match. */
    private int findContactIndex(String name) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Saves the current contact list. */
    private void saveContacts() {
        contactStorage.saveContacts(contacts);
    }
}
