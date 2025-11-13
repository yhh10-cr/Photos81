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

    public User(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        this.username = username.trim();
    }

    public String getUsername() {
        return username;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

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

    public Album createAlbum(String albumName) {
        if (albumName == null || albumName.isBlank()) {
            return null;
        }
        if (getAlbum(albumName) != null) {
            return null; // duplicate
        }
        Album album = new Album(albumName);
        albums.add(album);
        return album;
    }

    public boolean deleteAlbum(String albumName) {
        Album album = getAlbum(albumName);
        if (album == null) {
            return false;
        }
        return albums.remove(album);
    }

    public boolean renameAlbum(String oldName, String newName) {
        if (newName == null || newName.isBlank()) {
            return false;
        }

        Album album = getAlbum(oldName);
        if (album == null) {
            return false;
        }

        Album conflict = getAlbum(newName);
        if (conflict != null && conflict != album) {
            return false; // someone else already has that name
        }

        album.setName(newName);
        return true;
    }

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
