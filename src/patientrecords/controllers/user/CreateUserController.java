package patientrecords.controllers.user;

import patientrecords.models.User;
import patientrecords.controllers.role.RoleDashboardController;
import patientrecords.models.Role;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;

import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Stage;

import org.bson.Document;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;

public class CreateUserController extends UserDashboardController implements Initializable {

    // Dashboard CSS file URL
    FXMLLoader loader = new FXMLLoader();

    private final URL url = this.getClass().getResource("/patientrecords/styles/form.css");

    public Stage stage;
    private Logger logger;
    private CreateUserController main;

    private User user;

    private AnchorPane createUserForm;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXTextField lastNameField;

    @FXML
    private Label lastNameRulesLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private JFXTextField otherNameField;

    @FXML
    private Label otherNameLabel;

    @FXML
    private Label otherNameRulesLabel;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private Label usernameRulesLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXTextField jobTitleField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label passwordRulesLabel;

    @FXML
    private JFXPasswordField confirmPwdField;

    @FXML
    private JFXComboBox<Title> uTitleComboBox;

    @FXML
    private JFXComboBox<Role> uGroupComboBox;

    @FXML
    private CheckBox isActiveCheckBox;

    @FXML
    private Label errorMsgLabel;

    @FXML
    private JFXButton newGroupButton;

    @FXML
    private JFXButton saveButton;

    @FXML
    public JFXButton cancelButton;

    @FXML
    private Label confirmPwdReqLabel;

    @FXML
    private Label usernameReqLabel;

    @FXML
    private Label passwordReqLabel;

    @FXML
    private Label lastNameReqLabel;

    @FXML
    private Label otherNameReqLabel;

    @FXML
    private Label userGroupReqLabel;

    /**
     * TODO: Class CreateUserController
     * 1. Link from UserDashboardController + MenuBar
     */

    /**
     * Controller for creating a new user
     * @param db
     */
    public void createUserLoader(MongoDatabase db) {
        this.db = db;
        this.collection = db.getCollection("Users");
        this.logger = Logger.getLogger(getClass().getName());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/patientrecords/views/user/CreateUserForm.fxml"));
        loader.setController(this);

        try {
            createUserForm = (AnchorPane) loader.load();
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Failed to load loader", exception);
        }

        try {
            Scene scene = new Scene(createUserForm, 450, 482, Color.TRANSPARENT);

            // Add css file
            if (url != null) {
                String css = url.toExternalForm();
                scene.getStylesheets().add(css);
            } else {
                logger.log(Level.WARNING, "CSS URL not found!");
            }

            stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Create New User");
            stage.setFullScreen(false);
            stage.initOwner(primaryStage); // UserDashBoardController.primaryStage: UserDashboeard stage
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    public void saveAction() {

        // If any input field is not valid variable validInput is set to false
        boolean validInput = true;

        user = new User();
        errorMsgLabel.setText(null);

        /** Validate lastName
         * Mandatory single name, WITHOUT spaces, WITH special characters:
         * lastName should not contain a number
         * lastName should be longer than 2 characters
         */
        final String lname = lastNameField.getText();
        if (lname != null && lname.matches("^[A-Za-z]{3,10}(((\\'|\\-|\\.)?([A-Za-z])+))?$")) {
            if (lname.trim() == null) {
                errorMsgLabel.setText("Invalid last name");
                lastNameReqLabel.setStyle("-fx-text-fill: RED;");
                validInput = false;
            } else {
                lastNameReqLabel.setStyle("-fx-text-fill: WHITE;");
                user.setLastName(lname);
            }
        } else {
            errorMsgLabel.setText("Invalid last name");
            lastNameReqLabel.setStyle("-fx-text-fill: RED;");
            validInput = false;
        }

        /** Validate otherNames
         * Mandatory single name, optional additional names, WITH spaces, WITH special characters:
         * otherNames should not contain a number
         * otherNames should be longer than 2 characters
         * otherNames can contain special characters
         */
        final String oNames = otherNameField.getText();
        if (oNames != null && oNames.matches("^[A-Za-z]{3,10}((\\s)?((\\'|\\-|\\.)?[A-Za-z]{3,10}))*$")) {
            otherNameReqLabel.setStyle("-fx-text-fill: WHITE;");
            user.setOtherName(oNames);
        } else {
            errorMsgLabel.setText("Invalid name");
            otherNameReqLabel.setStyle("-fx-text-fill: RED;");
            validInput = false;
        }

        /** Validate username
         * Username should not start with a number
         * Username should be longer than 6 characters
         * Username should NOT contain special characters
         */
        final String username = usernameField.getText();
        if (username != null && username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{4,10}\\b")) {
            // Check if username exists
            if (checkUserExists(username)) {
                errorMsgLabel.setText("Username exists");
                usernameReqLabel.setStyle("-fx-text-fill: RED;");
                validInput = false;
            } else {
                usernameReqLabel.setStyle("-fx-text-fill: WHITE;");
                user.setUsername(username);
            }
        } else {
            errorMsgLabel.setText("Invalid username");
            usernameReqLabel.setStyle("-fx-text-fill: RED;");
            validInput = false;
        }

        /**
         * Validate userGroup
         */
        Role userGroup = uGroupComboBox.getValue();
        if (userGroup != null && !userGroup.getName().equals("")) {
            userGroupReqLabel.setStyle("-fx-text-fill: WHITE;");
            user.setRole(userGroup.getName());
        } else {
            errorMsgLabel.setText("User group required");
            userGroupReqLabel.setStyle("-fx-text-fill: RED;");
            validInput = false;
        }

        /** Validate password
         * password should be longer than 6 characters
         */
        String password = passwordField.getText();
        final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,15}$";
        if (password != null && password.matches(passwordPattern)) {
            passwordReqLabel.setStyle("-fx-text-fill: WHITE;");
        } else {
            errorMsgLabel.setText("Invalid password");
            passwordReqLabel.setStyle("-fx-text-fill: RED;");
            validInput = false;
        }

        /**
         * Confirm passwords match
         */
        String confirmPwd = confirmPwdField.getText();
        if (confirmPwd != null && confirmPwd.trim().length() > 0 && confirmPwd.equals(password)) {
            confirmPwdReqLabel.setStyle("-fx-text-fill: WHITE;");
            user.setPassword(password);
        } else {
            errorMsgLabel.setText("Passwords do NOT match");

            confirmPwdField.clear();
            confirmPwdReqLabel.setStyle("-fx-text-fill: RED;");

            passwordField.clear();
            passwordReqLabel.setStyle("-fx-text-fill: RED;");

            validInput = false;
        }

        // Get uTitle
        String uTitle;
        if (uTitleComboBox.getValue() != null) {
            uTitle = uTitleComboBox.getValue().getEnumTitle();
            user.setTitle(uTitle);
        }

        // Get job
        String job;
        if (jobTitleField.getText() != null || !(jobTitleField.getText().equals(""))) {
            job = jobTitleField.getText();
            user.setJob(job);
        }

        // Get isActive
        Boolean isActive = false;
        if (isActiveCheckBox.isSelected()) {
            isActive = true;
        }
        user.setIsActive(isActive);

        /**
         * Insert user to DB
         */
        if (validInput) {
            createUser(user);

            // Clear all fields
            uTitleComboBox.setValue(null);
            lastNameField.clear();
            otherNameField.clear();
            usernameField.clear();
            jobTitleField.clear();
            uGroupComboBox.setValue(null);
            passwordField.clear();
            confirmPwdField.clear();
            isActiveCheckBox.setSelected(true);

            errorMsgLabel.setText("User created successfully");
        }
    }

    /**Creates a new user
     * @param user
     */
    private void createUser(User user) {
        Document customData = new Document("title", user.getTitle()).append("lastName", user.getLastName())
                .append("otherName", user.getOtherName()).append("job", user.getJob())
                .append("active", user.getIsActive()).append("created", new Date()).append("lastLogin", null);

        Document command = new Document("createUser", user.getUsername()).append("pwd", user.getPassword())
                .append("customData", customData).append("roles", Collections.singletonList(user.getRole()))
                .append("digestPassword", true);

        try {
            db.runCommand(command);

        } catch (MongoCommandException e) {
            if (e.getErrorCode() == 31) {
                logger.log(Level.SEVERE, "RoleNotFound", e);
            } else {
                logger.log(Level.SEVERE, null, e);
            }
        }
    }

    public void newGroupAction() {
        // TODO: CreateUserContoller.newGroupAction()
    }

    public void cancelAction() {
        // TODO: CreateUserContoller.cancelAction()
        stage.close();
    }

    public void setMain(CreateUserController main) {
        this.main = main;
    }

    /**
     * Initializes the controller class.
     * @param url (URL)
     * @param rb (ResourceBundle)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMsgLabel.setText(null);

        // Populate the UTitle JFXComboBox
        uTitleComboBox.getItems().setAll(Title.values());
        uTitleComboBox.getSelectionModel().select(1);
        uTitleComboBox.setVisibleRowCount(3);
        uTitleComboBox.setValue(null);

        // Populate the uGroupComboBox JFXComboBox
        RoleDashboardController rdc = new RoleDashboardController();
        rdc.db = db;
        ObservableList<Role> roles = rdc.getRoleNameList();
        roles.sort(Comparator.comparing(Role::getCode)); // Sort the list
        uGroupComboBox.setItems(roles);
        uGroupComboBox.setVisibleRowCount(3);
        uGroupComboBox.setValue(null);

        // Show hint for lastName rules
        final String lastNameRules = "Last Name requirements:\n" + " 1. Last name is mandatory\n"
                + "  ... and should be/have:\n" + " 2. 3-10 characters\n" + " 3. allowed alphabets a-zA-Z\n";

        lastNameRulesLabel.setText(lastNameRules);
        lastNameLabel.setOnMouseEntered(event -> lastNameRulesLabel.setVisible(true));

        lastNameLabel.setOnMouseExited((MouseEvent e) -> {
            lastNameRulesLabel.setVisible(false);
        });

        // Show hint for otherName rules
        final String otherNameRules = "Other Name requirements:\n" + " 1. Other name is mandatory\n"
                + "  ... and should be/have:\n" + " 2. 3-15 characters\n" + " 3. allowed alphabets a-zA-Z\n";

        otherNameRulesLabel.setText(otherNameRules);
        otherNameLabel.setOnMouseEntered(event -> otherNameRulesLabel.setVisible(true));

        otherNameLabel.setOnMouseExited((MouseEvent e) -> {
            otherNameRulesLabel.setVisible(false);
        });

        // Show hint for username rules
        final String usernameRules = "Username requirements:\n" + " 1. Username is mandatory\n"
                + "  ... and should (be/have):\n" + " 2. 5-10 characters\n" + " 3. start with a letter\n"
                + " 4. Allowed a-z, A-Z, 0-9, points, dashes and underscores\n";

        usernameRulesLabel.setText(usernameRules);
        usernameLabel.setOnMouseEntered(event -> usernameRulesLabel.setVisible(true));

        usernameLabel.setOnMouseExited((MouseEvent e) -> {
            usernameRulesLabel.setVisible(false);
        });

        // Show hint for password rules
        final String passwordRules = "Password requirements:\n" + " 1. Password is mandatory\n"
                + "\t... and should be/have:\n" + " 2. 6-15 characters\n" + " 3. a digit must occur at least once\n"
                + " 4. a lower case letter must occur at least once\n"
                + " 5. an upper case letter must occur at least once\n"
                + " 6. no whitespace allowed in the entire string";

        passwordRulesLabel.setText(passwordRules);
        passwordLabel.setOnMouseEntered(event -> passwordRulesLabel.setVisible(true));

        passwordLabel.setOnMouseExited((MouseEvent e) -> {
            passwordRulesLabel.setVisible(false);
        });
    }
}