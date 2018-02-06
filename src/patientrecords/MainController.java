package patientrecords;

import java.io.*;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
//import javafx.scene.Parent;
//import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.value.ObservableValue;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.bson.Document;

import patientrecords.authentication.DBAuthentication;
import patientrecords.controllers.DashboardController;


public class MainController implements Initializable {
    // public class MainController extends AnchorPane {
    private Main main;
    public Stage stage;
    
    private MongoDatabase db;
    private Logger logger;

    //     @FXML
    //     private AnchorPane rootPane;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label uNameReqLabel;

    @FXML
    private Label uPwdReqLabel;

    @FXML
    private Label errorMsgLabel;

    @FXML
    private Button exitButton;
    @FXML
    private Button learnMoreButton;
    @FXML
    private Button signinButton;

    @FXML
    private JFXTextField username_field;
    @FXML
    private JFXPasswordField password_field;

    public void loaderInit() {
        //        try {
        // view
        this.logger = Logger.getLogger(getClass().getName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        //        loader.setRoot(this);
        loader.setController(this);

        //            Parent pane = (Parent) loader.load();
        //            pane = loader.load();

        try {
            //            loader.load();
            //            rootPane = (AnchorPane)loader.load();
            rootPane = loader.load();

        } catch (IOException exception) {
            //            throw new RuntimeException(exception);
            exception.printStackTrace();
        }

        // scene on stage
        Scene scene = new Scene(rootPane, 760, 297);
        // stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Patient Management System");
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
        
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);   
    }

    //     connect main class to controller
    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    public void exitButtonAction(ActionEvent event) throws IOException {
        Platform.exit();
    }

    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {
//        String username = username_field.getText();
//        String password = password_field.getText();
        
        String username = "tina";
        String password = "zyx321";

//        if (username.equals(" ") || password.equals(" ")) {
//            errorMsgLabel.setText("Invalid username/password");
//        } else {
            DBAuthentication dbAuth = new DBAuthentication(username, password);
            HashMap<String, HashMap<String, Object>> conn = dbAuth.getConn();
            Boolean auth = (Boolean) conn.get(username).get("auth");
            MongoDatabase db = (MongoDatabase) conn.get(username).get("database");
            
            
            Document command = new Document("rolesInfo", 1)
                        .append("showPrivileges", true)
                        .append("showBuiltinRoles", true);
        
            Document result = db.runCommand(command);
            ArrayList    roleInfo = (ArrayList) result.get("roles");
                    Iterator iterator = roleInfo.iterator();



            while (iterator.hasNext()){
                
                // iterator.next(): class org.bson.Document
                System.out.println("\n------ MainController -------");
                
                Document doc = (Document) iterator.next();
                // Document{{role=readWrite, db=EyGlas, isBuiltin=true, roles=[], inheritedRoles=[], privileges=[Document{{resource=Document{{db=EyGlas, collection=}}, actions=[changeStream, collStats, convertToCapped, createCollection, createIndex, dbHash, dbStats, dropCollection, dropIndex, emptycapped, find, insert, killCursors, listCollections, listIndexes, planCacheRead, remove, renameCollectionSameDB, update]}}, Document{{resource=Document{{db=EyGlas, collection=system.indexes}}, actions=[changeStream, collStats, dbHash, dbStats, find, killCursors, listCollections, listIndexes, planCacheRead]}}, Document{{resource=Document{{db=EyGlas, collection=system.js}}, actions=[changeStream, collStats, convertToCapped, createCollection, createIndex, dbHash, dbStats, dropCollection, dropIndex, emptycapped, find, insert, killCursors, listCollections, listIndexes, planCacheRead, remove, renameCollectionSameDB, update]}}, Document{{resource=Document{{db=EyGlas, collection=system.namespaces}}, actions=[changeStream, collStats, dbHash, dbStats, find, killCursors, listCollections, listIndexes, planCacheRead]}}], inheritedPrivileges=[Document{{resource=Document{{db=EyGlas, collection=}}, actions=[changeStream, collStats, convertToCapped, createCollection, createIndex, dbHash, dbStats, dropCollection, dropIndex, emptycapped, find, insert, killCursors, listCollections, listIndexes, planCacheRead, remove, renameCollectionSameDB, update]}}, Document{{resource=Document{{db=EyGlas, collection=system.indexes}}, actions=[changeStream, collStats, dbHash, dbStats, find, killCursors, listCollections, listIndexes, planCacheRead]}}, Document{{resource=Document{{db=EyGlas, collection=system.js}}, actions=[changeStream, collStats, convertToCapped, createCollection, createIndex, dbHash, dbStats, dropCollection, dropIndex, emptycapped, find, insert, killCursors, listCollections, listIndexes, planCacheRead, remove, renameCollectionSameDB, update]}}, Document{{resource=Document{{db=EyGlas, collection=system.namespaces}}, actions=[changeStream, collStats, dbHash, dbStats, find, killCursors, listCollections, listIndexes, planCacheRead]}}]}}

                
                System.out.println(doc);
                System.out.println(doc.get("role"));
                System.out.println(doc.get("isBuiltin"));

            }

//            if (auth) {
                // TODO MainController. !isActive log out user
//                // errorMsgLabel.setText("Login successful");
                DashboardController dc = new DashboardController();
                dc.dashboardLoader(stage, db);
                
                // TODO: Update lastLogin in User db.collections
//                
//                
//            } else {
//                errorMsgLabel.setText("Invalid username/password");
//            }
//        }
    }
    
    /**
     * Logout user from application
     */
    @FXML
    public void logOut(){
        loaderInit();
        db.runCommand(new Document("logout", 1));
    }

    @Override
    //    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Check to avoid NullPointerException
        assert password_field != null : "fx:id=\"password_field\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert username_field != null : "fx:id=\"username_field\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert signinButton != null : "fx:id=\"signinButton\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

        username_field.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> o, Boolean olduNameVal, Boolean newuNameVal) -> {
                    if (!newuNameVal
                            || (username_field.getText() != null && username_field.getText().trim().length() != 0)) {
                        uNameReqLabel.setText(null);
                    } else {
                        uNameReqLabel.setText("Username required");
                    }

                    if (password_field.getText().trim().length() != 0 && password_field.getText() != null) {
                        uPwdReqLabel.setText(null);
                    } else {
                        uPwdReqLabel.setText("Password required");
                    }
                });

        password_field.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> o, Boolean oldPwdVal, Boolean newPwdVal) -> {
                    if (!newPwdVal
                            || (password_field.getText() != null && password_field.getText().trim().length() != 0)) {
                        uPwdReqLabel.setText(null);
                    } else {
                        uPwdReqLabel.setText("Password required");
                    }

                    if (username_field.getText() != null && username_field.getText().trim().length() != 0) {
                        uNameReqLabel.setText(null);
                    } else {
                        uNameReqLabel.setText("Username required");
                    }
                });
    }
}
