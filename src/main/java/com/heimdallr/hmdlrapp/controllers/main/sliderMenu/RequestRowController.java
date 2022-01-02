package com.heimdallr.hmdlrapp.controllers.main.sliderMenu;

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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Objects;

public class RequestRowController extends AnchorPane {
    private FriendshipsService friendshipsService;
    private UserService userService;
    private FriendRequestService friendRequestService;
    private EventDispatcher eventDispatcher;

    private User currentUser;

    @FXML
    Label lettersLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label userFullNameLabel;

    @FXML
    Button acceptRequestActionButton;

    @FXML
    Button denyRequestActionButton;

    @FXML
    ImageView infoButton;

    @FXML
    BorderPane chatHead;

    public RequestRowController(String imagePath, String letters, String username, String userFullName, Timestamp timestamp) {
        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
            this.friendRequestService = (FriendRequestService) HmdlrDI.getContainer().getService(FriendRequestService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/listedFriendRequest.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            if(imagePath == null) {
                // circle color and letters
                lettersLabel.setText(letters);
            }

            this.currentUser = userService.getCurrentUser();

            usernameLabel.setText(userService.findByUsername(username).getDisplayUsername());
            userFullNameLabel.setText(userFullName);

            acceptRequestActionButton.setOnAction(e -> {
                // accept request
                FriendRequest friendRequest = friendRequestService.findForTwoUsers(
                        currentUser,
                        userService.findByUsername(username)
                );
                friendRequest.setAccepted(true);
                friendRequest.setFriendshipRequestStatus(Constants.FriendshipRequestStatus.APPROVED);
                friendRequestService.setFriendRequestStatus(
                        friendRequest,
                        Constants.FriendshipRequestStatus.APPROVED
                );
                friendshipsService.createFriendship(
                        userService.findByUsername(username).getId(),
                        currentUser.getId()
                );
            });

            denyRequestActionButton.setOnAction(e -> {
                FriendRequest friendRequest = friendRequestService.findForTwoUsers(
                        currentUser,
                        userService.findByUsername(username)
                );
                friendRequest.setAccepted(true);
                friendRequest.setFriendshipRequestStatus(Constants.FriendshipRequestStatus.REJECTED);
                friendRequestService.setFriendRequestStatus(
                        friendRequest,
                        Constants.FriendshipRequestStatus.REJECTED
                );
                this.eventDispatcher.dispatch(Channel.guiVisibleRequestsController, null);
            });

            infoButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if(chatHead.getBottom() != null) {
                    chatHead.setBottom(null);
                }
                else {
                    // adding timestamp field
                    HBox hbox = new HBox();
                    hbox.setId("timestampContainer");
                    hbox.setPadding(new Insets(10, 5, 5, 5));
                    Label timestampLabel = new Label();
                    timestampLabel.setText("Sent on: " + timestamp.toString());
                    timestampLabel.setStyle("-fx-text-fill: #f9f9f9");
                    hbox.getChildren().add(timestampLabel);

                    chatHead.setBottom(hbox);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
