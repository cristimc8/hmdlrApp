package com.heimdallr.hmdlrapp.controllers.chat;

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

    private int uid = 0;
    private String gid = null;

    public ChatHeadController(String imagePath, String letters, String groupNameOrFriend, String lastSentMessage, int uid) {

        this.uid = uid;
        this.init(imagePath, letters, groupNameOrFriend, lastSentMessage);

    }

    public ChatHeadController(String imagePath, String letters, String groupNameOrFriend, String lastSentMessage, String gid) {
        this.gid = gid;
        this.init(imagePath, letters, groupNameOrFriend, lastSentMessage);
    }

    private void init(String imagePath, String letters, String groupNameOrFriend, String lastSentMessage) {
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

    public int getUid() {
        return this.uid;
    }

    public String getGid() {
        return this.gid;
    }
}
