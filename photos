package photos.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single tag on a photo, as a (name, value) pair.
 *
 * @author Youssef, Srimaan
 */
public class Tag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String value;

    public Tag(String name, String value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be null or blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tag value cannot be null or blank");
        }
        this.name = name.trim();
        this.value = value.trim();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;
        return name.equalsIgnoreCase(tag.name)
                && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), value.toLowerCase());
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}

