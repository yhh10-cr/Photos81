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
 * @author Youssef, Srimaan
 */
public class UserManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** All users in the system (including "admin" and "stock"). */
    private final List<User> users = new ArrayList<>();

    public UserManager() { }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

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

    public User addUser(String username) {
        if (username == null || username.isBlank()) return null;
        if (getUser(username) != null) return null; // duplicate
        User u = new User(username);
        users.add(u);
        return u;
    }

    public boolean deleteUser(String username) {
        User u = getUser(username);
        if (u == null) return false;
        return users.remove(u);
    }

    public void ensureBuiltInUsers() {
        if (getUser("admin") == null) {
            addUser("admin");
        }
        if (getUser("stock") == null) {
            addUser("stock");
        }
    }

    /* ================= Persistence ================= */

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
            // fall through to new empty manager
        }
        return new UserManager();
    }

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
