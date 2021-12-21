package com.heimdallr.hmdlrapp.controllers.main.popups;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateGCController extends Subscriber implements CustomController {
    private EventDispatcher eventDispatcher;
    private UserService userService;
    private FriendshipsService friendshipsService;

    BorderPane createGCPopupContainer;
    ImageView closeCreateGCPopup;
    TextField chatAliasTextBox;
    Button finishCreateGCActionButton;
    VBox scrollableGCFriendsContainer;
    VBox scrollableMembersContainer;
    BorderPane parent;
    TextField scrollableSearchMembersTextBox;

    private String query = "";
    private List<User> joint = new ArrayList<>();

    public CreateGCController(
            BorderPane createGCPopupContainer,
            ImageView closeCreateGCPopup,
            TextField chatAliasTextBox,
            Button finishCreateGCActionButton,
            VBox scrollableGCFriendsContainer,
            VBox scrollableMembersContainer,
            BorderPane parent,
            TextField scrollableSearchMembersTextBox
    ) {
        this.createGCPopupContainer = createGCPopupContainer;
        this.closeCreateGCPopup = closeCreateGCPopup;
        this.chatAliasTextBox = chatAliasTextBox;
        this.finishCreateGCActionButton = finishCreateGCActionButton;
        this.scrollableMembersContainer = scrollableMembersContainer;
        this.scrollableGCFriendsContainer = scrollableGCFriendsContainer;
        this.parent = parent;
        this.scrollableSearchMembersTextBox = scrollableSearchMembersTextBox;

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        this.setListeners();
        this.eventDispatcher.subscribeTo(this, Channel.guiVisibleGCController);
        this.eventDispatcher.subscribeTo(this, Channel.groupContentChanged);
    }

    @Override
    protected void newContent(String info) {
        if(info != null) {
            User receivedUser = userService.findByUsername(info);

            // will receive the username we want to add / remove from group
            if (this.joint.contains(receivedUser)) {
                this.joint.remove(receivedUser);
            } else {
                this.joint.add(receivedUser);
            }
        }
        this.loadAllUsersAndTheFriendships();
    }

    private void setListeners() {
        closeCreateGCPopup.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                createGCPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });

        scrollableSearchMembersTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            this.query = scrollableSearchMembersTextBox.getText();
            this.eventDispatcher.dispatch(Channel.guiVisibleGCController, null);
        });

        finishCreateGCActionButton.setOnAction(e -> {
            // Create GC
        });
    }

    private void loadAllUsersAndTheFriendships() {
        try {
            scrollableGCFriendsContainer.getChildren().clear();
        } catch (Exception ignored) {
        }

        List<UserFriendshipDTO> allUsers = friendshipsService.findAllWithUser(userService.getCurrentUser());
        List<User> users = allUsers.stream().map(dto -> getDifferentUserFromTwo(dto.getUserOne(), dto.getUserTwo())).toList();
        for (User user : users) {
            if (user.getId() == 1 ||
                    Objects.equals(user.getId(), userService.getCurrentUser().getId()) ||
                    !user.getUsername().contains(query)) continue;
            JoinableToGroupRowController userRowController = new JoinableToGroupRowController(
                    null,
                    userService.getChatHeadPreviewLetters(user),
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName(),
                    this.joint.contains(user)
            );

            try {
                scrollableGCFriendsContainer.getChildren().add(userRowController);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private User getDifferentUserFromTwo(User one, User two) {
        return !Objects.equals(one.getId(), userService.getCurrentUser().getId()) ?
                one : two;
    }
}
