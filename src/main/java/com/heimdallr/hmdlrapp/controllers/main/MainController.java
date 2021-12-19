package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

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

        ChatHeadController chatHeadController = new ChatHeadController();
        try {
            recentChatsContainer.getChildren().add(chatHeadController);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
