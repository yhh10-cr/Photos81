package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.User;
import photos.model.UserManager;

/**
 * Controller for the login screen.
 *
 * @author Youssef, Srimaan
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

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

        // At this point login is successful.
        // Next step (future pieces): switch to admin or user view.
        // For now we just clear any error.
        showError("");

        // TODO: replace with actual scene switching once admin/user views exist.
        System.out.println("Logged in as: " + user.getUsername());

        // Optional: save data before switching screens
        Photos.saveUserData();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
