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
    /** Identifies contact field prefixes in a command. */
    private static final Pattern CONTACT_FIELD_PATTERN = Pattern.compile("(?:^|\\s)/(n|p|d)(?:\\s|$)");

    /** The contact list affected by commands. */
    private final InputList contactInputs;

    /** The UI used to display validation messages. */
    private final Ui ui;

    /** Contact subcommands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /**
     * Creates a handler for commands that affect the supplied contact list.
     *
     * @param contactInputs The contact list to update.
     * @param ui The UI used to display validation messages.
     */
    public ContactCommandHandler(InputList contactInputs, Ui ui) {
        assert contactInputs != null && ui != null : "Contact command dependencies must not be null";
        this.contactInputs = contactInputs;
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
                "ls", parameters -> contactInputs.printContacts());
    }

    /** Lists every contact or only contacts with a supplied name. */
    private void listContacts(String parameters) {
        if (parameters.isEmpty()) {
            contactInputs.printContacts();
        } else {
            contactInputs.listContactsWithName(parameters);
        }
    }

    /** Validates contact fields and adds a contact. */
    private void addContact(String arguments) {
        Map<String, String> fields = parseContactFields(arguments, 0);
        if (fields == null || !fields.containsKey("n") || fields.get("n").isBlank()) {
            ui.printContactUsage();
            return;
        }

        String phoneNumber = fields.getOrDefault("p", "");
        if (fields.containsKey("p") && !isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }

        contactInputs.addContact(new Contact(fields.get("n"), phoneNumber,
                fields.getOrDefault("d", "")));
    }

    /** Validates a contact name and deletes its matching contact. */
    private void deleteContact(String arguments) {
        Map<String, String> fields = parseContactFields(arguments, 0);
        if (fields == null || fields.size() != 1 || !fields.containsKey("n")
                || fields.get("n").isBlank()) {
            ui.printContactUsage();
            return;
        }
        contactInputs.deleteContact(fields.get("n"));
    }

    /** Validates replacement fields and updates their matching contact. */
    private void updateContact(String arguments) {
        Matcher firstField = CONTACT_FIELD_PATTERN.matcher(arguments);
        if (!firstField.find()) {
            ui.printContactUsage();
            return;
        }

        String currentName = arguments.substring(0, firstField.start()).trim();
        Map<String, String> fields = parseContactFields(arguments, firstField.start());
        if (currentName.isBlank() || fields == null || fields.isEmpty()) {
            ui.printContactUsage();
            return;
        }

        if (fields.containsKey("n") && fields.get("n").isBlank()) {
            ui.printContactUsage();
            return;
        }

        String phoneNumber = fields.get("p");
        if (fields.containsKey("p") && !isValidPhoneNumber(phoneNumber)) {
            ui.printContactNumberError();
            return;
        }

        contactInputs.updateContact(currentName, fields.get("n"), fields.get("p"), fields.get("d"));
    }

    /** Parses prefixed contact fields, returning {@code null} for invalid or duplicate fields. */
    private Map<String, String> parseContactFields(String arguments, int startIndex) {
        String fieldsText = arguments.substring(startIndex).trim();
        Matcher matcher = CONTACT_FIELD_PATTERN.matcher(fieldsText);
        Map<String, String> fields = new LinkedHashMap<>();
        int previousValueStart = -1;
        String previousKey = null;

        while (matcher.find()) {
            if (previousKey == null && matcher.start() != 0) {
                return null;
            }
            if (previousKey != null) {
                String value = fieldsText.substring(previousValueStart, matcher.start()).trim();
                fields.put(previousKey, value);
            }
            String key = matcher.group(1);
            if (fields.containsKey(key) || key.equals(previousKey)) {
                return null;
            }
            previousKey = key;
            previousValueStart = matcher.end();
        }

        if (previousKey == null) {
            return null;
        }
        fields.put(previousKey, fieldsText.substring(previousValueStart).trim());
        return fields;
    }

    /** Returns whether a supplied phone number contains exactly eight digits. */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{8}");
    }
}
