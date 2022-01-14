package com.heimdallr.hmdlrapp.controllers.main.popups.users;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.controllers.main.sliderMenu.UserRowController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.User;
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

/**
 * Controller class for the all-users popup component
 */
public class AllUsersController implements CustomController, Subscriber {
    private EventDispatcher eventDispatcher;
    private UserService userService;
    private FriendshipsService friendshipsService;

    private String query = "";


    ImageView closeAllUsersPopupButton;
    TextField scrollableUsersSearch;
    VBox scrollableUsersContainer;
    BorderPane parent;
    BorderPane allUsersPopupContainer;

    public AllUsersController(
            ImageView closeAllUsersPopupButton,
            TextField scrollableUsersSearch,
            VBox scrollableUsersContainer,
            BorderPane parent,
            BorderPane allUsersPopupContainer
    ) {
        this.closeAllUsersPopupButton = closeAllUsersPopupButton;
        this.scrollableUsersContainer = scrollableUsersContainer;
        this.scrollableUsersSearch = scrollableUsersSearch;
        this.parent = parent;
        this.allUsersPopupContainer = allUsersPopupContainer;

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newContent(String info) {
        loadAllUsersAndTheFriendships();
    }

    @Override
    public void initialize() {
        this.setListeners();
        this.eventDispatcher.subscribeTo(this, Channel.guiVisibleAllUsersController);
        this.eventDispatcher.subscribeTo(this, Channel.onFriendshipsChanged);
    }

    private void setListeners() {
        closeAllUsersPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                allUsersPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });

        scrollableUsersSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            this.query = scrollableUsersSearch.getText();
            this.eventDispatcher.dispatch(Channel.guiVisibleAllUsersController, null);
        });
    }

    private void loadAllUsersAndTheFriendships() {
        try {
            scrollableUsersContainer.getChildren().clear();
        } catch (Exception ignored) {
        }

        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            if (user.getId() == 1 ||
                    Objects.equals(user.getId(), userService.getCurrentUser().getId()) ||
                    !user.getUsername().contains(query)) continue;
            UserRowController userRowController = new UserRowController(
                    null,
                    userService.getChatHeadPreviewLetters(user),
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName()
            );

            userRowController.setThisParent(parent);

            try {
                scrollableUsersContainer.getChildren().add(userRowController);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
