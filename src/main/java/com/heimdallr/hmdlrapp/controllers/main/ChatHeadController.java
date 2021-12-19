package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ChatHeadController extends AnchorPane {
    private UserService userService;

    public ChatHeadController() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/friendsAndGroupsTabComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
