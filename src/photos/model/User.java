package photos.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single user of the Photos application.
 * A user has a username and a collection of albums.
 *
 * Usernames are unique (enforced by the higher-level model/manager).
 * Album names must be unique per user (case-insensitive).
 *
 * @author Youssef, Srimaan
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Login name for this user (e.g., "admin", "stock", "alice"). */
    private final String username;

    /** All albums belonging to this user. */
    private final List<Album> albums = new ArrayList<>();

    /**
     * Constructs a User with the given username.
     *
     * @param username non-null, non-blank username
     * @throws IllegalArgumentException if username is null/blank
     */
    public User(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        this.username = username.trim();
    }

    /**
     * @return the username for this user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return unmodifiable view of this user's albums.
     */
    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    /**
     * Finds an album by name (case-insensitive).
     *
     * @param name album name to search for
     * @return Album if found, otherwise null
     */
    public Album getAlbum(String name) {
        if (name == null) return null;
        String target = name.trim().toLowerCase();
        for (Album album : albums) {
            if (album.getName().toLowerCase().equals(target)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Creates and adds a new album with the given name.
     * Fails if an album with the same name (case-insensitive) already exists.
     *
     * @param albumName name for the new album
     * @return the created Album, or null if duplicate / invalid name
     */
    public Album createAlbum(String albumName) {
        if (albumName == null || albumName.isBlank()) {
            return null;
        }
        if (getAlbum(albumName) != null) {
            // Duplicate album name for this user
            return null;
        }
        Album album = new Album(albumName);
        albums.add(album);
        return album;
    }

    /**
     * Deletes the album with the given name, if it exists.
     *
     * @param albumName name of the album to delete
     * @return true if the album was found and removed
     */
    public boolean deleteAlbum(String albumName) {
        Album album = getAlbum(albumName);
        if (album == null) {
            return false;
        }
        return albums.remove(album);
    }

    /**
     * Renames an existing album.
     * Fails if:
     *  - source album doesn't exist, or
     *  - another album already uses the new name (case-insensitive).
     *
     * @param oldName current album name
     * @param newName desired new name
     * @return true if rename succeeded
     */
    public boolean renameAlbum(String oldName, String newName) {
        if (newName == null || newName.isBlank()) {
            return false;
        }

        Album album = getAlbum(oldName);
        if (album == null) {
            return false;
        }

        // Check uniqueness of newName
        Album conflict = getAlbum(newName);
        if (conflict != null && conflict != album) {
            return false; // another album already has this name
        }

        album.setName(newName);
        return true;
    }

    /**
     * Users are considered equal if their usernames match (case-insensitive).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", numAlbums=" + albums.size() +
                '}';
    }
}
