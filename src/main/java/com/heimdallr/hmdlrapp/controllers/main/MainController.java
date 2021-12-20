package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.nio.charset.Charset;

public class MainController {
    private UserService userService;

    @FXML
    VBox recentChatsContainer;

    @FXML
    protected void initialize() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        ChatHeadController chatHeadController = new ChatHeadController(null, "HM", "System", "Welcome to hmdlrNet!");
        try {
            recentChatsContainer.getChildren().add(chatHeadController);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
