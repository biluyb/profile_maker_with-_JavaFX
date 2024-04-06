

package javafxapplication1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import static javafx.application.Application.launch;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class PersonalInformationApp extends Application {

    private ObservableList<UserProfile> profiles;
    private ListView<UserProfile> listView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        profiles = FXCollections.observableArrayList();
        loadProfiles(); // Load profiles from files

        listView = new ListView<>(profiles);

        Button newButton = new Button("New Profile");
        Button updateButton = new Button("Update Profile");
        Button removeButton = new Button("Remove Profile");
//        Button uploadButton = new Button("Upload Photo");
        Button listButton = new Button("View Profiles");

        newButton.setOnAction(event -> showNewProfileDialog());
        updateButton.setOnAction(event -> showUpdateProfileDialog());
        removeButton.setOnAction(event -> removeProfile());
//        uploadButton.setOnAction(event -> uploadPhoto());
        listButton.setOnAction(event -> listProfiles());

        HBox buttonBox = new HBox(10, newButton, updateButton, removeButton, listButton);
        VBox root = new VBox(10, listView, buttonBox);
        root.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(root, 1280, 640, Color.IVORY));
        primaryStage.setTitle("Personal Information App");
        primaryStage.show();
    }

    private void loadProfiles() {
        File directory = new File(".");
        File[] files = directory.listFiles((File dir, String name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                    UserProfile profile = (UserProfile) inputStream.readObject();
                    profiles.add(profile);
                } catch (IOException | ClassNotFoundException e) {
                    showAlert("Error", "Failed to load profiles.");
                }
            }
        }
    }


   private void showNewProfileDialog() {
    Dialog<UserProfile> dialog = new Dialog<>();
    dialog.setTitle("Add Profile");
    dialog.setHeaderText("Create a New Profile");

    // Set the dialog's size
    dialog.setWidth(400);
    dialog.setHeight(500);

    ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

    GridPane gridPane = new GridPane();
    gridPane.setHgap(50);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(20));

    TextField fullNameTextField = new TextField();
    TextField hobbyTextField = new TextField();
    TextField futureTextField = new TextField();
    TextField musicTextField = new TextField();
    TextField filmTextField = new TextField();
    TextField skillsTextField = new TextField();
    ImageView photoView = new ImageView();
    Button uploadButton = new Button("Upload Photo");

    // Set the preferred size for the photo view
    photoView.setFitWidth(200);
    photoView.setFitHeight(200);

    uploadButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            photoView.setImage(image);
        }
    });

    gridPane.add(new Label("Full Name:"), 0, 0);
    gridPane.add(fullNameTextField, 1, 0);
    gridPane.add(new Label("Hobby:"), 0, 1);
    gridPane.add(hobbyTextField, 1, 1);
    gridPane.add(new Label("Where do you see yourself after 5 years:"), 0, 2);
    gridPane.add(futureTextField, 1, 2);
    gridPane.add(new Label("Favorite Music:"), 0, 3);
    gridPane.add(musicTextField, 1, 3);
    gridPane.add(new Label("Favorite Film:"), 0, 4);
    gridPane.add(filmTextField, 1, 4);
    gridPane.add(new Label("Skills:"), 0, 5);
    gridPane.add(skillsTextField, 1, 5);
    gridPane.add(uploadButton, 0, 6);
    gridPane.add(photoView, 1, 6);

    dialog.getDialogPane().setContent(gridPane);

    dialog.setResultConverter(buttonType -> {
        if (buttonType == addButtonType) {
            String fullName = fullNameTextField.getText().trim();
            String hobby = hobbyTextField.getText().trim();
            String future = futureTextField.getText().trim();
            String music = musicTextField.getText().trim();
            String film = filmTextField.getText().trim();
            String skills = skillsTextField.getText().trim();
            String photoPath = photoView.getImage() != null ? photoView.getImage().getUrl() : null;

            if (!fullName.isEmpty() && !hobby.isEmpty() && !future.isEmpty() && !music.isEmpty() && !film.isEmpty() && !skills.isEmpty()) {
                UserProfile profile = new UserProfile(fullName, hobby, future, music, film, skills);
                profile.setPhotoPath(photoPath);
                return profile;
            } else {
                showAlert("Incomplete Form", "Please fill in all fields.");
            }
        }
        return null;
    });

    dialog.showAndWait().ifPresent(result -> {
        profiles.add(result);
        saveProfile(result);
        listView.refresh();
    });
}



    private void showUpdateProfileDialog() {
        UserProfile selectedProfile = listView.getSelectionModel().getSelectedItem();
        if (selectedProfile == null) {
            showAlert("No profile selected", "Please select a profile to update.");
            return;
        }

        Dialog<UserProfile> dialog = new Dialog<>();
        dialog.setTitle("Update Profile");
        dialog.setHeaderText("Update the selected user profile");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField(selectedProfile.getFullName());
        TextField hobbyField = new TextField(selectedProfile.getHobby());
        TextField futureField = new TextField(selectedProfile.getFuture());
        TextField musicField = new TextField(selectedProfile.getMusic());
        TextField filmField = new TextField(selectedProfile.getFilm());
        TextField skillsField = new TextField(selectedProfile.getSkills());

        gridPane.add(new Label("Full Name:"), 0, 0);
        gridPane.add(fullNameField, 1, 0);
        gridPane.add(new Label("Hobby:"), 0, 1);
        gridPane.add(hobbyField, 1, 1);
        gridPane.add(new Label("Where do you see yourself after 5 years:"), 0, 2);
        gridPane.add(futureField, 1, 2);
        gridPane.add(new Label("Favorite Music:"), 0, 3);
        gridPane.add(musicField, 1, 3);
        gridPane.add(new Label("Favorite Film:"), 0, 4);
        gridPane.add(filmField, 1, 4);
        gridPane.add(new Label("Skills:"), 0, 5);
        gridPane.add(skillsField, 1, 5);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String fullName = fullNameField.getText();
                String hobby = hobbyField.getText();
                String future = futureField.getText();
                String music = musicField.getText();
                String film = filmField.getText();
                String skills = skillsField.getText();
                selectedProfile.setFullName(fullName);
                selectedProfile.setHobby(hobby);
                selectedProfile.setFuture(future);
                selectedProfile.setMusic(music);
                selectedProfile.setFilm(film);
                selectedProfile.setSkills(skills);
                return selectedProfile;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            updateProfile(result);
            listView.refresh();
        });
    }

    
    
    private void removeProfile() {
        UserProfile selectedProfile = listView.getSelectionModel().getSelectedItem();
        if (selectedProfile == null) {
            showAlert("No profile selected", "Please select a profile to remove.");
            return;
        }

        profiles.remove(selectedProfile);
        removeProfileFile(selectedProfile);
    }


    
    
    private void listProfiles() {
    UserProfile selectedProfile = listView.getSelectionModel().getSelectedItem();
    if (selectedProfile == null) {
        showAlert("No profile selected", "Please select a profile to view.");
        return;
    }

    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Profile Details");
    dialog.setHeaderText("Profile Information");

    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

    GridPane gridPane = new GridPane();
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(20));

    Label fullNameLabel = new Label("Full Name:");
    Label hobbyLabel = new Label("Hobby:");
    Label futureLabel = new Label("Where do you see yourself after 5 years:");
    Label musicLabel = new Label("Favorite Music:");
    Label filmLabel = new Label("Favorite Film:");
    Label skillsLabel = new Label("Skills:");

    gridPane.add(fullNameLabel, 0, 0);
    gridPane.add(new Label(selectedProfile.getFullName()), 1, 0);
    gridPane.add(hobbyLabel, 0, 1);
    gridPane.add(new Label(selectedProfile.getHobby()), 1, 1);
    gridPane.add(futureLabel, 0, 2);
    gridPane.add(new Label(selectedProfile.getFuture()), 1, 2);
    gridPane.add(musicLabel, 0, 3);
    gridPane.add(new Label(selectedProfile.getMusic()), 1, 3);
    gridPane.add(filmLabel, 0, 4);
    gridPane.add(new Label(selectedProfile.getFilm()), 1, 4);
    gridPane.add(skillsLabel, 0, 5);
    gridPane.add(new Label(selectedProfile.getSkills()), 1, 5);

    if (selectedProfile.getPhotoPath() != null) {
        ImageView photoView = new ImageView(selectedProfile.getPhotoPath());
        photoView.setFitWidth(200);
        photoView.setPreserveRatio(true);
        gridPane.add(photoView, 0, 6, 2, 1);
    }

    dialog.getDialogPane().setContent(gridPane);

    dialog.showAndWait();
}


    private void saveProfile(UserProfile profile) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(profile.getFullName() + ".dat"))) {
            outputStream.writeObject(profile);
        } catch (IOException e) {
            showAlert("Error", "Failed to save the profile.");
        }
    }

    private void updateProfile(UserProfile profile) {
        removeProfileFile(profile);
        saveProfile(profile);
    }

    private void removeProfileFile(UserProfile profile) {
        File file = new File(profile.getFullName() + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class UserProfile implements Serializable {
        private String fullName;
        private String hobby;
        private String future;
        private String music;
        private String film;
        private String skills;
        private String photoPath;

        public UserProfile(String fullName, String hobby, String future, String music, String film, String skills) {
            this.fullName = fullName;
            this.hobby = hobby;
            this.future = future;
            this.music = music;
            this.film = film;
            this.skills = skills;
        }

        // Getters and setters

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getHobby() {
            return hobby;
        }

        public void setHobby(String hobby) {
            this.hobby = hobby;
        }

        public String getFuture() {
            return future;
        }

        public void setFuture(String future) {
            this.future = future;
        }

        public String getMusic() {
            return music;
        }

        public void setMusic(String music) {
            this.music = music;
        }

        public String getFilm() {
            return film;
        }

        public void setFilm(String film) {
            this.film = film;
        }

        public String getSkills() {
            return skills;
        }

        public void setSkills(String skills) {
            this.skills = skills;
        }

        public String getPhotoPath() {
            return photoPath;
        }

        public void setPhotoPath(String photoPath) {
            this.photoPath = photoPath;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }
}
