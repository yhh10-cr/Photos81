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

    public Album(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Album name cannot be null or blank");
        }
        this.name = newName.trim();
    }

    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    public int getNumPhotos() {
        return photos.size();
    }

    public boolean addPhoto(Photo photo) {
        if (photo == null) return false;
        if (photos.contains(photo)) { // uses Photo.equals (file path)
            return false;
        }
        return photos.add(photo);
    }

    public boolean removePhoto(Photo photo) {
        if (photo == null) return false;
        return photos.remove(photo);
    }

    public boolean containsPhoto(Photo photo) {
        if (photo == null) return false;
        return photos.contains(photo);
    }

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

    public String getFormattedDateRange() {
        LocalDateTime earliest = getEarliestDate();
        LocalDateTime latest = getLatestDate();
        if (earliest == null || latest == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return earliest.toLocalDate().format(fmt) + " - " + latest.toLocalDate().format(fmt);
    }

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
