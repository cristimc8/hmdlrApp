package com.heimdallr.hmdlrapp.controllers.misc;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class SystemNotificationController extends BorderPane {
    @FXML
    BorderPane notificationContainer;

    @FXML
    Label notificationText;

    public SystemNotificationController(String notificationText) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/res/notificationLabel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            this.notificationText.setText(notificationText);
        }
        catch (Exception ignored) {}
    }
}
