package photos.model;

import java.io.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central model class that stores all users of the application
 * and handles loading/saving them via Java serialization.
 *
 * Typical usage:
 *   UserManager mgr = UserManager.loadFromFile("data/users.dat");
 *   mgr.ensureBuiltInUsers();   // admin, stock
 *   ...
 *   mgr.saveToFile("data/users.dat");
 *
 * @author Youssef, Srimaan
 */
public class UserManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** All users in the system (including "admin" and "stock"). */
    private final List<User> users = new ArrayList<>();

    public UserManager() {
    }

    /**
     * @return unmodifiable list of users.
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    /**
     * Finds a user by username (case-insensitive).
     *
     * @param username username to search for
     * @return User if found, otherwise null
     */
    public User getUser(String username) {
        if (username == null) return null;
        String target = username.trim().toLowerCase();
        for (User u : users) {
            if (u.getUsername().toLowerCase().equals(target)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Adds a new user with the given username.
     * Fails if a user with the same username already exists.
     *
     * @param username username for new user
     * @return created User, or null if invalid/duplicate
     */
    public User addUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        if (getUser(username) != null) {
            return null; // duplicate
        }
        User u = new User(username);
        users.add(u);
        return u;
    }

    /**
     * Deletes the user with the given username, if present.
     *
     * @param username username to delete
     * @return true if user was found and removed
     */
    public boolean deleteUser(String username) {
        User u = getUser(username);
        if (u == null) {
            return false;
        }
        return users.remove(u);
    }

    /**
     * Makes sure built-in users "admin" and "stock" exist.
     * Call this after loading from file.
     */
    public void ensureBuiltInUsers() {
        if (getUser("admin") == null) {
            addUser("admin");
        }
        if (getUser("stock") == null) {
            addUser("stock");
        }
    }

    /* =====================  Persistence helpers  ===================== */

    /**
     * Loads a UserManager from the given file.
     * If the file does not exist or cannot be read, returns a new empty manager.
     *
     * @param filePath path to serialized users file (e.g., "data/users.dat")
     * @return loaded UserManager or new empty one
     */
    public static UserManager loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new UserManager();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof UserManager manager) {
                return manager;
            }
        } catch (IOException | ClassNotFoundException e) {
            // In a real app, you might log this; for now just fall back to empty.
        }

        return new UserManager();
    }

    /**
     * Saves this UserManager to the given file.
     *
     * @param filePath path to serialized users file (e.g., "data/users.dat")
     * @throws IOException if saving fails
     */
    public void saveToFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
            oos.flush();
        }
    }
}
