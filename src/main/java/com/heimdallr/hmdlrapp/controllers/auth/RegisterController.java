package com.heimdallr.hmdlrapp.controllers.auth;

import com.heimdallr.hmdlrapp.HelloApplication;
import com.heimdallr.hmdlrapp.exceptions.*;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.utils.Async;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RegisterController {
    @FXML
    TextField usernameTextBox;
    @FXML
    TextField emailTextBox;
    @FXML
    TextField firstNameTextBox;
    @FXML
    TextField lastNameTextBox;
    @FXML
    PasswordField passwordTextBox;
    @FXML
    PasswordField password2TextBox;
    @FXML
    Label errorLabel;
    @FXML
    Label secondErrorLabel;
    @FXML
    Button createAccountButton;
    @FXML
    AnchorPane secondScreenComponent;
    @FXML
    BorderPane secondRegisterScreenBorderPane;
    @FXML
    AnchorPane loadingComponent;

    private UserService userService;

    @FXML
    protected void initialize() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            prepareSlideMenuAnimation();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    private void prepareSlideMenuAnimation() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), secondScreenComponent);
        openNav.setToX(0);
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), secondScreenComponent);
        createAccountButton.setOnAction((ActionEvent evt) -> {

            String username = usernameTextBox.getText();
            String email = emailTextBox.getText();
            String password1 = passwordTextBox.getText();
            String password2 = password2TextBox.getText();

            try {
                userService.completeFirstStep(username, email, password1, password2);

                // Switch to second register step
            } catch (ValueExistsException | ValidationException | NoEmptyFieldsException | WrongInputException e) {
                errorLabel.setText(e.getMessage());
                errorLabel.setVisible(true);
                return;
            }

            secondScreenComponent.setVisible(true);

            // Animation
            if (secondScreenComponent.getTranslateX() != 0) {
                openNav.play();
            } else {
                closeNav.setToX(-(secondScreenComponent.getWidth()));
                closeNav.play();
            }
        });
    }

    @FXML
    protected void goToAuthButtonClicked() {
        goToAuthScreen();
    }

    public void goToAuthScreen() {
        Stage stage = (Stage) usernameTextBox.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
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

    /**
     * This is called when the register process is finished,
     * on the last screen of register.
     */
    @FXML
    protected void finishCreateButtonClicked() {
        String firstName = firstNameTextBox.getText();
        String lastName = lastNameTextBox.getText();

        try {
            userService.completeRegister(firstName, lastName);

            // Animations
            secondRegisterScreenBorderPane.setOpacity(0.12);
            loadingComponent.setVisible(true);

            Async.setTimeout(this::goToAuthScreen, 2000);

        } catch (NoEmptyFieldsException e) {
            secondErrorLabel.setText(e.getMessage());
            secondErrorLabel.setVisible(true);
        }
    }
}
