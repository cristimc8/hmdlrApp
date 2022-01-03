package com.heimdallr.hmdlrapp.controllers.misc;


import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendRequestService;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class ListedSelectAFriendRowController extends AnchorPane {
    private UserService userService;
    private EventDispatcher eventDispatcher;

    @FXML
    Label lettersLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label userFullNameLabel;

    @FXML
    Button friendActionButton;

    public ListedSelectAFriendRowController(String imagePath, String letters, String username, String userFullName) {

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/listedSelectAFriendRow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            if(imagePath == null) {
                // circle color and letters
                lettersLabel.setText(letters);
            }

            usernameLabel.setText(userService.findByUsername(username).getDisplayUsername());
            userFullNameLabel.setText(userFullName);

            friendActionButton.setOnAction(e -> {
                this.eventDispatcher.dispatch(Channel.onFriendSelectedForReports, username);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
