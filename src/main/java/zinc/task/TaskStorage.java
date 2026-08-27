package zinc.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to, and restores tasks from, Zinc's local data file.
 */
public class TaskStorage {
    /** The file used to retain tasks between application runs. */
    private static final Path STORAGE_FILE = Path.of("data", "zinc.txt");

    /**
     * Returns the tasks currently saved in the storage file.
     * If any line has an invalid layout, the storage file is cleared and no
     * tasks are loaded. This avoids restoring only part of a damaged list.
     * When an I/O error occurs, the storage file is also reset.
     *
     * @return all valid saved tasks, in their stored order
     */
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(STORAGE_FILE)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(STORAGE_FILE)) {
                tasks.add(parseTask(line));
            }
        } catch (IllegalArgumentException exception) {
            System.out.println("Saved task file has an invalid layout. Clearing saved tasks.\n");
            resetStorage();
            tasks.clear();
        } catch (IOException exception) {
            System.out.println("Unable to load saved tasks. Starting with an empty list.\n");
            resetStorage();
        }
        return tasks;
    }

    /**
     * Writes every task in the supplied array to the storage file.
     *
     * @param tasks the task array to save
     * @param taskCount the number of populated entries in {@code tasks}
     */
    public void saveTasks(Task[] tasks, int taskCount) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(formatTask(tasks[i]));
        }

        try {
            Files.createDirectories(STORAGE_FILE.getParent());
            Files.write(STORAGE_FILE, lines);
        } catch (IOException exception) {
            System.out.println("Unable to save tasks. Error: " + exception + "\n");
        }
    }

    /**
     * Converts one correctly formatted storage line into its corresponding task.
     *
     * @param line the serialized task line read from the storage file
     * @return the task represented by {@code line}
     * @throws IllegalArgumentException if the line does not match the storage layout
     */
    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 2 || !parts[1].matches("[01]")) {
            throw new IllegalArgumentException("Invalid task type or completion status");
        }

        Task task;
        switch (parts[0]) {
            case "T":
                requireFieldCount(parts, 3);
                task = new Todo(parts[2]);
                break;
            case "D":
                requireFieldCount(parts, 4);
                task = new Deadline(parts[2], LocalDateTime.parse(parts[3]));
                break;
            case "E":
                requireFieldCount(parts, 5);
                task = new Event(parts[2], LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
        for (int i = 2; i < parts.length; i++) {
            if (parts[i].isBlank()) {
                throw new IllegalArgumentException("Task information cannot be blank");
            }
        }
        if (parts[1].equals("1")) {
            task.complete();
        }
        return task;
    }

    /** Ensures that a saved task has exactly the expected number of fields. */
    private void requireFieldCount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect number of task fields");
        }
    }

    /** Clears a malformed storage file so Zinc can start with an empty list. */
    private void resetStorage() {
        try {
            Files.write(STORAGE_FILE, List.of());
        } catch (IOException exception) {
            System.out.println("\tUnable to clear the invalid saved task file.\n");
        }
    }

    /** Converts a task into the pre-determined storage format. */
    private String formatTask(Task task) {
        String status = task.isCompleted() ? "1" : "0";
        switch (task.getTaskType()) {
            case TODO:
                return "T | " + status + " | " + task.getTaskName();
            case DEADLINE:
                Deadline deadline = (Deadline) task;
                return "D | " + status + " | " + deadline.getTaskName() + " | " + deadline.getDeadline();
            case EVENT:
                Event event = (Event) task;
                return "E | " + status + " | " + event.getTaskName()
                        + " | " + event.getStart() + " | " + event.getEnd();
            default:
                throw new IllegalArgumentException("Unsupported task type");
        }
    }
}
