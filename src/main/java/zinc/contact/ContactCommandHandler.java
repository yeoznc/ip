package zinc.contact;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zinc.ui.Ui;

/**
 * Parses and executes contact subcommands.
 */
public class ContactCommandHandler {
    /** The field key identifying a contact name. */
    private static final String NAME_FIELD = "n";

    /** The field key identifying a contact phone number. */
    private static final String PHONE_NUMBER_FIELD = "p";

    /** The field key identifying a contact description. */
    private static final String DESCRIPTION_FIELD = "d";

    /** Identifies contact field prefixes in a command. */
    private static final Pattern CONTACT_FIELD_PATTERN = Pattern.compile("(?:^|\\s)/(n|p|d)(?:\\s|$)");

    /** The contact list affected by commands. */
    private final ContactList contactList;

    /** The UI used to display validation messages. */
    private final Ui ui;

    /** Contact subcommands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /**
     * Creates a handler for commands that affect the supplied contact list.
     *
     * @param contactList The contact list to update.
     * @param ui The UI used to display validation messages.
     */
    public ContactCommandHandler(ContactList contactList, Ui ui) {
        assert contactList != null && ui != null : "Contact command dependencies must not be null";
        this.contactList = contactList;
        this.ui = ui;
        this.commands = createCommands();
    }

    /**
     * Parses and executes a contact subcommand.
     *
     * @param parameters The subcommand and its arguments.
     */
    public void execute(String parameters) {
        assert parameters != null : "Contact command input must not be null";
        String[] commandParts = parameters.split("\\s+", 2);
        String subcommand = commandParts[0];
        String arguments = commandParts.length > 1 ? commandParts[1].trim() : "";

        Consumer<String> selectedCommand = commands.get(subcommand);
        if (selectedCommand == null) {
            ui.printContactUsage();
            return;
        }
        selectedCommand.accept(arguments);
    }

    /** Creates the command registry used to dispatch contact operations. */
    private Map<String, Consumer<String>> createCommands() {
        return Map.of(
                "add", this::addContact,
                "del", this::deleteContact,
                "delete", this::deleteContact,
                "update", this::updateContact,
                "list", this::listContacts,
                "ls", ignoredParameters -> contactList.printContacts());
    }

    /** Lists every contact or only contacts with a supplied name. */
    private void listContacts(String parameters) {
        if (parameters.isEmpty()) {
            contactList.printContacts();
        } else {
            contactList.printContactsNamed(parameters);
        }
    }

    /** Validates contact fields and adds a contact. */
    private void addContact(String arguments) {
        Map<String, String> contactFields = parseContactFields(arguments, 0);
        if (contactFields == null || !contactFields.containsKey(NAME_FIELD)
                || contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }

        String phoneNumber = contactFields.getOrDefault(PHONE_NUMBER_FIELD, "");
        if (contactFields.containsKey(PHONE_NUMBER_FIELD) && !Contact.isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }

        contactList.addContact(new Contact(contactFields.get(NAME_FIELD), phoneNumber,
                contactFields.getOrDefault(DESCRIPTION_FIELD, "")));
    }

    /** Validates a contact name and deletes its matching contact. */
    private void deleteContact(String arguments) {
        Map<String, String> contactFields = parseContactFields(arguments, 0);
        if (contactFields == null || contactFields.size() != 1 || !contactFields.containsKey(NAME_FIELD)
                || contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }
        contactList.deleteContact(contactFields.get(NAME_FIELD));
    }

    /** Validates replacement fields and updates their matching contact. */
    private void updateContact(String arguments) {
        Matcher firstField = CONTACT_FIELD_PATTERN.matcher(arguments);
        if (!firstField.find()) {
            ui.printContactUsage();
            return;
        }

        String currentName = arguments.substring(0, firstField.start()).trim();
        Map<String, String> contactFields = parseContactFields(arguments, firstField.start());
        if (currentName.isBlank() || contactFields == null || contactFields.isEmpty()) {
            ui.printContactUsage();
            return;
        }

        if (contactFields.containsKey(NAME_FIELD) && contactFields.get(NAME_FIELD).isBlank()) {
            ui.printContactUsage();
            return;
        }

        String phoneNumber = contactFields.get(PHONE_NUMBER_FIELD);
        if (contactFields.containsKey(PHONE_NUMBER_FIELD) && !Contact.isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }

        contactList.updateContact(currentName, contactFields.get(NAME_FIELD), phoneNumber,
                contactFields.get(DESCRIPTION_FIELD));
    }

    /** Parses prefixed contact fields, returning {@code null} for invalid or duplicate fields. */
    private Map<String, String> parseContactFields(String arguments, int startIndex) {
        String fieldsText = arguments.substring(startIndex).trim();
        Matcher matcher = CONTACT_FIELD_PATTERN.matcher(fieldsText);
        Map<String, String> contactFields = new LinkedHashMap<>();
        int previousValueStart = -1;
        String previousFieldName = null;

        while (matcher.find()) {
            if (previousFieldName == null && matcher.start() != 0) {
                return null;
            }
            if (previousFieldName != null) {
                String value = fieldsText.substring(previousValueStart, matcher.start()).trim();
                contactFields.put(previousFieldName, value);
            }
            String fieldName = matcher.group(1);
            if (contactFields.containsKey(fieldName) || fieldName.equals(previousFieldName)) {
                return null;
            }
            previousFieldName = fieldName;
            previousValueStart = matcher.end();
        }

        if (previousFieldName == null) {
            return null;
        }
        contactFields.put(previousFieldName, fieldsText.substring(previousValueStart).trim());
        return contactFields;
    }
}
