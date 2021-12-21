package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendRequestService;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class UserRowController extends AnchorPane {
    private FriendshipsService friendshipsService;
    private UserService userService;
    private FriendRequestService friendRequestService;

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

            usernameLabel.setText(userService.findByUsername(username).getDisplayUsername());
            userFullNameLabel.setText(userFullName);


            String buttonAction;
            Constants.FriendshipRequestStatus status = this.getFriendshipStatusForUser(username);
            switch (status) {
                case APPROVED -> buttonAction = "Remove friend";
                case INCOMING -> buttonAction = "Accept request";
                case PENDING -> buttonAction = "Delete request";
                default -> buttonAction = "Add friend";
            }
            friendActionButton.setText(buttonAction);
            friendActionButton.setOnAction(event -> {
                switch (status) {
                    case APPROVED -> {
                        friendshipsService.deleteFriendship(
                                userService.findByUsername(username).getId(),
                                userService.getCurrentUser().getId()
                        );

                    }
                    case INCOMING -> {
                        FriendRequest betweenTheseTwo = friendRequestService.findForTwoUsers(userService.findByUsername(username), this.userService.getCurrentUser());
                        System.out.println(betweenTheseTwo.getFriendRequestStatusAsString());
                        friendRequestService.setFriendRequestStatus(
                                betweenTheseTwo,
                                Constants.FriendshipRequestStatus.APPROVED
                        );
                        friendshipsService.createFriendship(
                                userService.findByUsername(username),
                                userService.getCurrentUser()
                        );
                    }
                    case PENDING -> {
                        FriendRequest betweenTheseTwo = friendRequestService.findForTwoUsers(userService.findByUsername(username), this.userService.getCurrentUser());
                        friendRequestService.setFriendRequestStatus(
                                betweenTheseTwo,
                                Constants.FriendshipRequestStatus.CANCELED
                        );
                    }
                    default -> {
                        // create a friend request
                        friendRequestService.createFriendRequest(
                                userService.getCurrentUser().getId(),
                                userService.findByUsername(username).getId()
                        );
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

        if (this.friendshipsService.findForTwoUsers(user, this.userService.getCurrentUser()) == null) {
            // no direct friendship, but maybe a sent request?
            FriendRequest betweenTheseTwo = this.friendRequestService.findForTwoUsers(user, this.userService.getCurrentUser());
            if(betweenTheseTwo == null) {
                // no friend request either, so able to send first
                status = Constants.FriendshipRequestStatus.REJECTED; // we set rejected to enter on last case
            }
            else {
                if(Objects.equals(userService.findById(betweenTheseTwo.getSenderId()).getUsername(), username)) {
                    // if the sender is that user in label, then we set new status of INCOMING
                    status = Constants.FriendshipRequestStatus.INCOMING;
                }
                else status = betweenTheseTwo.getFriendshipRequestStatus();
            }
        }
        else status = Constants.FriendshipRequestStatus.APPROVED;

        return status;
    }
}
