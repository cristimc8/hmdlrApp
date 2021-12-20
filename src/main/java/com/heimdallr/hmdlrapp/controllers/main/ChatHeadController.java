package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ChatHeadController extends AnchorPane {

    @FXML
    Label lettersLabel;

    @FXML
    AnchorPane profilePictureCircle;

    @FXML
    Label groupOrFriendNameLabel;

    @FXML
    Label lastSentMessageLabel;

    public ChatHeadController(String imagePath, String letters, String groupNameOrFriend, String lastSentMessage) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/friendsAndGroupsTabComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            if(imagePath == null) {
                // circle color and letters
                lettersLabel.setText(letters);
            }

            groupOrFriendNameLabel.setText(groupNameOrFriend);

            lastSentMessageLabel.setText(lastSentMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
