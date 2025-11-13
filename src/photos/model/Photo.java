package photos.model;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single photo in the application.
 * Stores its file path, caption, date taken (from last-modified time),
 * and associated tags.
 *
 * @author Youssef, Srimaan
 */
public class Photo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Normalized absolute path to the photo file. */
    private final String filePath;

    /** Optional user caption. */
    private String caption;

    /** Date/time the photo was taken (from file last-modified). */
    private LocalDateTime dateTaken;

    /** Tags attached to this photo. No duplicate (name,value) pairs. */
    private final List<Tag> tags = new ArrayList<>();

    /**
     * Creates a Photo for the given file path.
     * The file must exist; its last-modified time is used as dateTaken.
     *
     * @param filePath path to the image file
     * @throws IOException              if the file cannot be read
     * @throws IllegalArgumentException if filePath is null/blank
     */
    public Photo(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("filePath cannot be null or blank");
        }

        // Normalize to absolute path so the same photo is recognized everywhere
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new IOException("Photo file does not exist: " + path);
        }

        this.filePath = path.toString();
        this.caption = "";

        // Set dateTaken from file's last modified time
        refreshDateTakenFromFile();
    }

    /**
     * Re-reads the file's last-modified time and updates dateTaken.
     *
     * @throws IOException if file attributes cannot be read
     */
    public void refreshDateTakenFromFile() throws IOException {
        Path path = Paths.get(filePath);
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        Instant instant = attrs.lastModifiedTime().toInstant();
        this.dateTaken = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = (caption == null) ? "" : caption.trim();
    }

    public LocalDateTime getDateTaken() {
        return dateTaken;
    }

    /**
     * Convenience method for UI: formatted date string.
     */
    public String getFormattedDateTaken() {
        if (dateTaken == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTaken.format(fmt);
    }

    /**
     * @return unmodifiable view of tags list.
     */
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Adds a tag if it is not already present.
     *
     * @param tag tag to add
     * @return true if added, false if it was already present
     */
    public boolean addTag(Tag tag) {
        if (tag == null) return false;
        if (tags.contains(tag)) {
            return false; // prevent duplicate (name,value)
        }
        return tags.add(tag);
    }

    /**
     * Removes the given tag if present.
     *
     * @param tag tag to remove
     * @return true if removed
     */
    public boolean removeTag(Tag tag) {
        if (tag == null) return false;
        return tags.remove(tag);
    }

    /**
     * Photos are considered equal if they refer to the same normalized file path.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo photo)) return false;
        return Objects.equals(filePath, photo.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "filePath='" + filePath + '\'' +
                ", caption='" + caption + '\'' +
                ", dateTaken=" + getFormattedDateTaken() +
                '}';
    }
}
