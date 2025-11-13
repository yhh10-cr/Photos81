package photos.controller;
import photos.controller.AlbumsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.User;
import photos.model.UserManager;

/**
 * Controller for the login screen.
 *
 * Handles logging in as either the admin user or a normal user.
 *
 * @author Youssef, Srimaan
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        showError("");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();

        if (username == null || username.isBlank()) {
            showError("Please enter a username.");
            return;
        }

        username = username.trim();

        UserManager mgr = Photos.getUserManager();
        User user = mgr.getUser(username);

        if (user == null) {
            showError("User does not exist.");
            return;
        }

        showError("");

        if (username.equalsIgnoreCase("admin")) {
            // Admin goes to admin management screen
            goToAdminScreen();
        } else {
            // Normal user goes to albums screen
            goToUserScreen(user);
        }

        Photos.saveUserData();
    }

    /** Switch to the admin user management screen. */
    private void goToAdminScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/admin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // later replace with an Alert in GUI
        }
    }

    /** Switch to the normal user albums screen. */
    private void goToUserScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/albums.fxml"));
            Parent root = loader.load();

            // Get controller and pass the User
            AlbumsController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // later replace with an Alert in GUI
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg == null ? "" : msg);
    }
}
