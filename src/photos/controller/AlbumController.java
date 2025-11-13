package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for a single album view.
 *
 * Shows photos in the album, lets the user add/remove photos,
 * edit captions, and manage tags.
 *
 * @author Youssef, Srimaan
 */
public class AlbumController {

    @FXML
    private Label albumLabel;

    @FXML
    private ListView<Photo> photoListView;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField captionField;

    @FXML
    private ListView<Tag> tagListView;

    @FXML
    private TextField tagNameField;

    @FXML
    private TextField tagValueField;

    @FXML
    private Label errorLabel;

    private User user;
    private Album album;

    private final ObservableList<Photo> photosObs = FXCollections.observableArrayList();
    private final ObservableList<Tag> tagsObs = FXCollections.observableArrayList();

    /**
     * Called by AlbumsController after loading this FXML.
     */
    public void setContext(User user, Album album) {
        this.user = user;
        this.album = album;

        albumLabel.setText("Album: " + album.getName());
        refreshPhotoList();
    }

    @FXML
    private void initialize() {
        photoListView.setItems(photosObs);
        tagListView.setItems(tagsObs);

        // Show nice text for each photo in the list
        photoListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Photo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String caption = item.getCaption();
                    if (caption == null || caption.isBlank()) {
                        caption = new File(item.getFilePath()).getName();
                    }
                    setText(caption);
                }
            }
        });

        // When selection changes, update detail view
        photoListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showPhotoDetails(newVal)
        );

        showError("");
    }

    private void refreshPhotoList() {
        if (album == null) return;
        List<Photo> albumPhotos = album.getPhotos();
        photosObs.setAll(albumPhotos);
        photoListView.refresh();

        if (!photosObs.isEmpty()) {
            photoListView.getSelectionModel().selectFirst();
        } else {
            showPhotoDetails(null);
        }
    }

    private void showPhotoDetails(Photo photo) {
        if (photo == null) {
            photoImageView.setImage(null);
            captionField.clear();
            dateLabel.setText("Date:");
            tagsObs.clear();
            return;
        }

        // Load the image
        File file = new File(photo.getFilePath());
        Image img = new Image(file.toURI().toString(), false);
        photoImageView.setImage(img);

        captionField.setText(photo.getCaption());
        dateLabel.setText("Date: " + photo.getFormattedDateTaken());

        tagsObs.setAll(photo.getTags());
        tagListView.refresh();
    }

    @FXML
    private void handleAddPhoto() {
        if (album == null) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Photo");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png")
        );

        Stage stage = (Stage) albumLabel.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return; // user cancelled
        }

        try {
            Photo photo = new Photo(file.getAbsolutePath());
            boolean added = album.addPhoto(photo);
            if (!added) {
                showError("Photo already exists in this album.");
                return;
            }

            showError("");
            refreshPhotoList();
            Photos.saveUserData();
        } catch (IOException e) {
            showError("Could not load photo file.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemovePhoto() {
        if (album == null) return;

        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a photo to remove.");
            return;
        }

        boolean ok = album.removePhoto(selected);
        if (!ok) {
            showError("Could not remove photo.");
            return;
        }

        showError("");
        refreshPhotoList();
        Photos.saveUserData();
    }

    @FXML
    private void handleSaveCaption() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a photo to caption.");
            return;
        }

        String caption = captionField.getText();
        selected.setCaption(caption);

        showError("");
        photoListView.refresh();
        Photos.saveUserData();
    }

    @FXML
    private void handleAddTag() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a photo first.");
            return;
        }

        String name = tagNameField.getText();
        String value = tagValueField.getText();

        if (name == null || name.isBlank() || value == null || value.isBlank()) {
            showError("Enter both tag type and value.");
            return;
        }

        Tag tag;
        try {
            tag = new Tag(name, value);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        boolean added = selected.addTag(tag);
        if (!added) {
            showError("Tag already exists for this photo.");
            return;
        }

        tagNameField.clear();
        tagValueField.clear();
        showError("");
        tagsObs.setAll(selected.getTags());
        tagListView.refresh();
        Photos.saveUserData();
    }

    @FXML
    private void handleDeleteTag() {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a photo first.");
            return;
        }

        Tag selectedTag = tagListView.getSelectionModel().getSelectedItem();
        if (selectedTag == null) {
            showError("Select a tag to delete.");
            return;
        }

        boolean ok = selected.removeTag(selectedTag);
        if (!ok) {
            showError("Could not delete tag.");
            return;
        }

        showError("");
        tagsObs.setAll(selected.getTags());
        tagListView.refresh();
        Photos.saveUserData();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/photos/view/albums.fxml"));
            Parent root = loader.load();

            AlbumsController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) albumLabel.getScene().getWindow();
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
