package com.heimdallr.hmdlrapp.controllers.main.sliderMenu;

import com.heimdallr.hmdlrapp.controllers.main.popups.profile.ProfilePageController;
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
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class UserRowController extends AnchorPane {
    private FriendshipsService friendshipsService;
    private UserService userService;
    private FriendRequestService friendRequestService;
    private EventDispatcher eventDispatcher;

    User user;

    @FXML
    Label lettersLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label userFullNameLabel;

    @FXML
    Button friendActionButton;

    public UserRowController(String imagePath, String letters, String username, String userFullName) {

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
            this.friendRequestService = (FriendRequestService) HmdlrDI.getContainer().getService(FriendRequestService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/listedUser.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            if(imagePath == null) {
                // circle color and letters
                lettersLabel.setText(letters);
            }

            this.user = userService.findByUsername(username);
            usernameLabel.setText(user.getDisplayUsername());
            usernameLabel.setCursor(Cursor.HAND);
            try {
                usernameLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.eventDispatcher.dispatch(Channel.guiVisibleProfilePage, "id:" + user.getId());
                });
            }
            catch (Exception ignored){}
            userFullNameLabel.setText(userFullName);


            String buttonAction;
            Constants.FriendshipRequestStatus status = this.getFriendshipStatusForUser(username);
            switch (status) {
                case APPROVED -> buttonAction = "Remove friend"; // delete a friendship and that's it
                case INCOMING -> buttonAction = "Accept request"; // update an existing to accepted and create friendship object
                case PENDING -> buttonAction = "Delete request"; // update an existing to canceled
                default -> buttonAction = "Add friend"; // create a new friendship request object
            }
            friendActionButton.setText(buttonAction);
            friendActionButton.setOnAction(event -> {
                switch (status) {
                    case APPROVED -> {
                        friendshipsService.deleteFriendship(
                                userService.findByUsername(username).getId(),
                                userService.getCurrentUser().getId()
                        );
                        eventDispatcher.dispatch(Channel.onFriendshipsChanged, null);

                    }
                    case INCOMING -> {
                        // Find the friendship request object
                        FriendRequest betweenTheseTwo = friendRequestService.findForTwoUsers(userService.findByUsername(username), this.userService.getCurrentUser());
                        // sets it as accepted
                        friendRequestService.setFriendRequestStatus(
                                betweenTheseTwo,
                                Constants.FriendshipRequestStatus.APPROVED
                        );
                        // creates the friendship object
                        friendshipsService.createFriendship(
                                userService.findByUsername(username),
                                userService.getCurrentUser()
                        );
                        eventDispatcher.dispatch(Channel.onFriendshipsChanged, null);
                    }
                    case PENDING -> {
                        // Find the existing friendship request object
                        FriendRequest betweenTheseTwo = friendRequestService.findForTwoUsers(userService.findByUsername(username), this.userService.getCurrentUser());
                        // update it to canceled
                        friendRequestService.setFriendRequestStatus(
                                betweenTheseTwo,
                                Constants.FriendshipRequestStatus.CANCELED
                        );
                        eventDispatcher.dispatch(Channel.guiVisibleAllUsersController, null);
                    }
                    default -> {
                        // create a friend request
                        friendRequestService.createFriendRequest(
                                userService.getCurrentUser().getId(),
                                userService.findByUsername(username).getId()
                        );
                        eventDispatcher.dispatch(Channel.guiVisibleAllUsersController, null);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * return friendship status for these 2 lovebirds
     * @param username stalin lover 69
     * @return
     */
    private Constants.FriendshipRequestStatus getFriendshipStatusForUser(String username) {
        Constants.FriendshipRequestStatus status;
        User user = this.userService.findByUsername(username);
        User loggedIn = this.userService.getCurrentUser();
        Friendship friendshipBetween = this.friendshipsService.findForTwoUsers(user, loggedIn);
        FriendRequest requestBetween = this.friendRequestService.findForTwoUsers(user, this.userService.getCurrentUser());

        if (friendshipBetween == null) {
            // no direct friendship, but maybe a sent request?
            if(requestBetween == null) {
                // no friend request either, so able to send first
                status = Constants.FriendshipRequestStatus.REJECTED; // we set rejected to enter on last case
            }
            else {
                if(Objects.equals(userService.findById(requestBetween.getSenderId()).getUsername(), username)) {
                    // if the sender is that user in label, then we set new status of INCOMING
                    status = Constants.FriendshipRequestStatus.INCOMING;
                }
                else status = requestBetween.getFriendshipRequestStatus();
            }
        }
        else status = Constants.FriendshipRequestStatus.APPROVED;

        return status;
    }
}
