package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.User;
import photos.model.UserManager;

import java.io.IOException;

/**
 * Controller for the admin user management screen.
 *
 * @author Youssef, Srimaan
 */
public class AdminController {

    @FXML
    private ListView<String> userListView;

    @FXML
    private TextField newUserField;

    @FXML
    private Label errorLabel;

    /** Called automatically after FXML is loaded. */
    @FXML
    private void initialize() {
        refreshUserList();
    }

    private UserManager getUserManager() {
        return Photos.getUserManager();
    }

    private void refreshUserList() {
        UserManager mgr = getUserManager();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (User u : mgr.getUsers()) {
            items.add(u.getUsername());
        }
        userListView.setItems(items);
    }

    @FXML
    private void handleAddUser() {
        String username = newUserField.getText();
        if (username == null || username.isBlank()) {
            showError("Enter a username to add.");
            return;
        }
        username = username.trim();

        UserManager mgr = getUserManager();
        User created = mgr.addUser(username);
        if (created == null) {
            showError("User already exists or name is invalid.");
            return;
        }

        newUserField.clear();
        showError("");
        refreshUserList();
        Photos.saveUserData();
    }

    @FXML
    private void handleDeleteUser() {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a user to delete.");
            return;
        }

        if (selected.equalsIgnoreCase("admin")) {
            showError("Cannot delete admin.");
            return;
        }

        UserManager mgr = getUserManager();
        boolean deleted = mgr.deleteUser(selected);
        if (!deleted) {
            showError("Could not delete user.");
            return;
        }

        showError("");
        refreshUserList();
        Photos.saveUserData();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // later replace with Alert
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
