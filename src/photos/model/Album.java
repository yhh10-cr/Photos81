package photos.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a photo album for a single user.
 * An album has a name and contains zero or more Photos.
 *
 * The same Photo object may be referenced by multiple albums,
 * so changes to caption/tags of a Photo are visible everywhere.
 *
 * @author Youssef, Srimaan
 */
public class Album implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Album name (must be unique per user, enforced by User class). */
    private String name;

    /** Ordered list of photos in this album (no duplicates). */
    private final List<Photo> photos = new ArrayList<>();

    /**
     * Creates a new Album with the given name.
     *
     * @param name album name (non-null, non-blank)
     * @throws IllegalArgumentException if name is null/blank
     */
    public Album(String name) {
        setName(name);
    }

    /**
     * @return the album name
     */
    public String getName() {
        return name;
    }

    /**
     * Renames the album.
     * Note: User class must enforce uniqueness of names per user.
     *
     * @param newName new album name
     * @throws IllegalArgumentException if newName is null/blank
     */
    public void setName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Album name cannot be null or blank");
        }
        this.name = newName.trim();
    }

    /**
     * @return unmodifiable view of photos in this album.
     */
    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    /**
     * @return number of photos in this album.
     */
    public int getNumPhotos() {
        return photos.size();
    }

    /**
     * Adds a photo to this album if it is not already present.
     *
     * @param photo photo to add
     * @return true if added, false if null or already present
     */
    public boolean addPhoto(Photo photo) {
        if (photo == null) {
            return false;
        }
        if (photos.contains(photo)) { // uses Photo.equals (file path)
            return false;            // no duplicates in same album
        }
        return photos.add(photo);
    }

    /**
     * Removes a photo from this album.
     *
     * @param photo photo to remove
     * @return true if removed, false otherwise
     */
    public boolean removePhoto(Photo photo) {
        if (photo == null) {
            return false;
        }
        return photos.remove(photo);
    }

    /**
     * @param photo photo to check
     * @return true if this album already contains the photo
     */
    public boolean containsPhoto(Photo photo) {
        if (photo == null) {
            return false;
        }
        return photos.contains(photo);
    }

    /**
     * @return earliest dateTaken among photos in this album,
     *         or null if the album is empty or dates are unavailable.
     */
    public LocalDateTime getEarliestDate() {
        LocalDateTime earliest = null;
        for (Photo p : photos) {
            LocalDateTime dt = p.getDateTaken();
            if (dt == null) continue;
            if (earliest == null || dt.isBefore(earliest)) {
                earliest = dt;
            }
        }
        return earliest;
    }

    /**
     * @return latest dateTaken among photos in this album,
     *         or null if the album is empty or dates are unavailable.
     */
    public LocalDateTime getLatestDate() {
        LocalDateTime latest = null;
        for (Photo p : photos) {
            LocalDateTime dt = p.getDateTaken();
            if (dt == null) continue;
            if (latest == null || dt.isAfter(latest)) {
                latest = dt;
            }
        }
        return latest;
    }

    /**
     * Convenience for UI: formatted date range string.
     *
     * @return "earliest - latest" or "" if no photos.
     */
    public String getFormattedDateRange() {
        LocalDateTime earliest = getEarliestDate();
        LocalDateTime latest = getLatestDate();
        if (earliest == null || latest == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return earliest.toLocalDate().format(fmt) + " - " + latest.toLocalDate().format(fmt);
    }

    /**
     * Albums are considered equal if their names match (case-insensitive).
     * Uniqueness per user should be enforced by the User class.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album album)) return false;
        return name.equalsIgnoreCase(album.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", numPhotos=" + getNumPhotos() +
                ", dateRange='" + getFormattedDateRange() + '\'' +
                '}';
    }
}
