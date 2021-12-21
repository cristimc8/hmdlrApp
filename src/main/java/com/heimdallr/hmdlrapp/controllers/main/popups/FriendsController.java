package com.heimdallr.hmdlrapp.controllers.main.popups;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.controllers.main.UserRowController;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendsController extends Subscriber implements CustomController {
    private EventDispatcher eventDispatcher;
    private UserService userService;
    private FriendshipsService friendshipsService;

    BorderPane friendsPopupContainer;
    ImageView closeFriendsPopupButton;
    TextField scrollableFriendsSearch;
    VBox scrollableFriendsContainer;
    BorderPane parent;

    private String query = "";

    public FriendsController(BorderPane friendsPopupContainer,
                             ImageView closeFriendsPopupButton,
                             TextField scrollableFriendsSearch,
                             VBox scrollableFriendsContainer,
                             BorderPane parent) {
        this.friendsPopupContainer = friendsPopupContainer;
        this.closeFriendsPopupButton = closeFriendsPopupButton;
        this.scrollableFriendsContainer = scrollableFriendsContainer;
        this.scrollableFriendsSearch = scrollableFriendsSearch;
        this.parent = parent;

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
        this.eventDispatcher.subscribeTo(this, Channel.guiVisibleFriendsController);
        this.eventDispatcher.subscribeTo(this, Channel.onFriendshipsChanged);
    }

    @Override
    protected void newContent(String info) {
        loadAllUsersAndTheFriendships();
    }

    private void setListeners() {
        closeFriendsPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                friendsPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });

        scrollableFriendsSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            this.query = scrollableFriendsSearch.getText();
            this.eventDispatcher.dispatch(Channel.guiVisibleFriendsController, null);
        });
    }

    private void loadAllUsersAndTheFriendships() {
        try {
            scrollableFriendsContainer.getChildren().clear();
        } catch (Exception ignored) {
        }

        List<UserFriendshipDTO> allUsers = friendshipsService.findAllWithUser(userService.getCurrentUser());
        List<User> users = allUsers.stream().map(dto -> getDifferentUserFromTwo(dto.getUserOne(), dto.getUserTwo())).toList();
        for (User user : users) {
            if (user.getId() == 1 ||
                    Objects.equals(user.getId(), userService.getCurrentUser().getId()) ||
                    !user.getUsername().contains(query)) continue;
            UserRowController userRowController = new UserRowController(
                    null,
                    userService.getChatHeadPreviewLetters(user),
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName()
            );

            try {
                scrollableFriendsContainer.getChildren().add(userRowController);
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
