package com.heimdallr.hmdlrapp.controllers.auth;

import com.heimdallr.hmdlrapp.HelloApplication;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField usernameTextBox;

    @FXML
    PasswordField passwordTextBox;

    @FXML
    Label errorLabel;

    private UserService userService;

    @FXML
    protected void initialize() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void loginButtonClicked() {
        Stage stage = (Stage) usernameTextBox.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainScreen.fxml"));

        String username = usernameTextBox.getText();
        String plainTextPassword = passwordTextBox.getText();

        try {
            userService.authenticate(username, plainTextPassword);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle(Constants.appName);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void goToCreateButtonClicked(){
        Stage stage = (Stage) usernameTextBox.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createAccountScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            stage.setTitle(Constants.appName);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
