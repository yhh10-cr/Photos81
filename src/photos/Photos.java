package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.UserManager;

import java.io.IOException;

/**
 * Entry point for the Photos application.
 *
 * Loads/saves all user data and shows the initial login screen.
 *
 * @author Youssef, Srimaan
 */
public class Photos extends Application {

    /** Where serialized user data will be stored. */
    private static final String DATA_FILE = "data/users.dat";

    /** Shared user manager for the entire application. */
    private static UserManager userManager;

    /**
     * @return the global UserManager instance.
     */
    public static UserManager getUserManager() {
        return userManager;
    }

    /**
     * Saves current user data to disk.
     */
    public static void saveUserData() {
        if (userManager == null) {
            return;
        }
        try {
            userManager.saveToFile(DATA_FILE);
        } catch (IOException e) {
            // In final version you should show an Alert instead of printing stack trace.
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load users from disk and ensure admin/stock exist
        userManager = UserManager.loadFromFile(DATA_FILE);
        userManager.ensureBuiltInUsers();

        // Load login screen (we will create login.fxml next)
        Parent root = loadLoginScreen();

        primaryStage.setTitle("Photos");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        // On window close, save data
        primaryStage.setOnCloseRequest(event -> saveUserData());

        primaryStage.show();
    }

    /**
     * Loads the login screen from FXML.
     *
     * @return root node of the login scene
     * @throws IOException if loading fails
     */
    private Parent loadLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/login.fxml")
        );
        return loader.load();
    }

    /**
     * Standard Java main method. Launches JavaFX application.
     *
     * @param args command-line args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
