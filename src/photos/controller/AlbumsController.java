package photos.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.Album;
import photos.model.User;

import java.io.IOException;

/**
 * Controller for the normal user's album list screen.
 *
 * Shows all albums for a user and allows create/rename/delete/open.
 *
 * @author Youssef, Srimaan
 */
public class AlbumsController {

    @FXML
    private Label userLabel;

    @FXML
    private TableView<Album> albumTable;

    @FXML
    private TableColumn<Album, String> nameColumn;

    @FXML
    private TableColumn<Album, String> countColumn;

    @FXML
    private TableColumn<Album, String> dateRangeColumn;

    @FXML
    private TextField newAlbumField;

    @FXML
    private TextField renameAlbumField;

    @FXML
    private Label errorLabel;

    private User user;
    private final ObservableList<Album> albumsObs = FXCollections.observableArrayList();

    /**
     * Called by LoginController after loading FXML.
     */
    public void setUser(User user) {
        this.user = user;
        userLabel.setText("User: " + user.getUsername());
        refreshAlbums();
    }

    @FXML
    private void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        countColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(data.getValue().getNumPhotos())
                ));

        dateRangeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedDateRange()));

        albumTable.setItems(albumsObs);
        showError("");
    }

    private void refreshAlbums() {
        if (user == null) return;
        albumsObs.setAll(user.getAlbums());
        albumTable.refresh();
    }

    @FXML
    private void handleCreateAlbum() {
        if (user == null) return;

        String name = newAlbumField.getText();
        if (name == null || name.isBlank()) {
            showError("Enter a name for the new album.");
            return;
        }
        name = name.trim();

        Album created = user.createAlbum(name);
        if (created == null) {
            showError("Album name already exists or is invalid.");
            return;
        }

        newAlbumField.clear();
        showError("");
        refreshAlbums();
        Photos.saveUserData();
    }

    @FXML
    private void handleRenameAlbum() {
        if (user == null) return;

        Album selected = albumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an album to rename.");
            return;
        }

        String newName = renameAlbumField.getText();
        if (newName == null || newName.isBlank()) {
            showError("Enter a new name.");
            return;
        }
        newName = newName.trim();

        boolean ok = user.renameAlbum(selected.getName(), newName);
        if (!ok) {
            showError("Album name already exists or is invalid.");
            return;
        }

        renameAlbumField.clear();
        showError("");
        refreshAlbums();
        Photos.saveUserData();
    }

    @FXML
    private void handleDeleteAlbum() {
        if (user == null) return;

        Album selected = albumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an album to delete.");
            return;
        }

        boolean ok = user.deleteAlbum(selected.getName());
        if (!ok) {
            showError("Could not delete album.");
            return;
        }

        showError("");
        refreshAlbums();
        Photos.saveUserData();
    }

    @FXML
    private void handleOpenAlbum() {
        Album selected = albumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an album to open.");
            return;
        }

        // TODO: later load album-view FXML (photos inside album)
        System.out.println("Opening album: " + selected.getName());
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) albumTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            Photos.saveUserData();
        } catch (IOException e) {
            e.printStackTrace(); // later replace with Alert
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg == null ? "" : msg);
    }
}
