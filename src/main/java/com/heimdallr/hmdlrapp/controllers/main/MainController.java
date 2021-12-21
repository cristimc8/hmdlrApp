package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.controllers.main.popups.AllUsersController;
import com.heimdallr.hmdlrapp.controllers.main.popups.FriendsController;
import com.heimdallr.hmdlrapp.exceptions.*;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.GroupChatsService;
import com.heimdallr.hmdlrapp.services.MessagesService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import com.heimdallr.hmdlrapp.utils.Async;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class MainController extends Subscriber {
    private UserService userService;
    private MessagesService messagesService;
    private GroupChatsService groupChatsService;
    private EventDispatcher eventDispatcher;

    @FXML
    VBox recentChatsContainer;

    @FXML
    AnchorPane mainComponent;

    @FXML
    ImageView burgerMenuButton;

    @FXML
    BorderPane mainChildrenComponents;

    @FXML
    BorderPane coveringBorderInjectable;

    @FXML
    AnchorPane sliderMenu;

    @FXML
    ImageView closeSliderMenuButton;

    @FXML
    Label nameLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label lettersLabel;

    @FXML
    HBox allUsersRow;

    @FXML
    HBox yourFriendsRow;

    @FXML
    BorderPane allUsersPopupContainer;

    @FXML
    ImageView closeAllUsersPopupButton;

    @FXML
    VBox scrollableUsersContainer;

    @FXML
    TextField scrollableUsersSearch;

    @FXML
    TextField searchTextBox;

    @FXML
    BorderPane friendsPopupContainer;

    @FXML
    ImageView closeFriendsPopupButton;

    @FXML
    TextField scrollableFriendsSearch;

    @FXML
    VBox scrollableFriendsContainer;

    @FXML
    protected void initialize() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
            this.groupChatsService = (GroupChatsService) HmdlrDI.getContainer().getService(GroupChatsService.class);
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        this.eventDispatcher.subscribeTo(this, Channel.onNewMessage);
        this.assignComponentsToControllers();
    }

    @Override
    protected void newContent() {
//        this.loadMessages();
    }

    private void assignComponentsToControllers() {
        this.assignToSliderMenu();
        this.assignToAllUsersPopup();
        this.assignToLeftBar();
        this.assignToFriends();
    }

    private void assignToSliderMenu() {
        SliderMenuController sliderMenuController = new SliderMenuController(
                sliderMenu,
                usernameLabel,
                nameLabel,
                lettersLabel,
                allUsersRow,
                allUsersPopupContainer,
                mainChildrenComponents,
                yourFriendsRow,
                friendsPopupContainer
                );
        sliderMenuController.initialize();
    }

    private void assignToAllUsersPopup() {
        AllUsersController allUsersController = new AllUsersController(
                closeAllUsersPopupButton,
                scrollableUsersSearch,
                scrollableUsersContainer,
                mainChildrenComponents,
                allUsersPopupContainer
        );

        allUsersController.initialize();
    }

    private void assignToLeftBar() {
        LeftBarController leftBarController = new LeftBarController(
                recentChatsContainer,
                burgerMenuButton,
                searchTextBox,
                mainChildrenComponents,
                sliderMenu,
                closeSliderMenuButton
        );
        leftBarController.initialize();
    }

    private void assignToFriends() {
        FriendsController friendsController = new FriendsController(
                friendsPopupContainer,
                closeFriendsPopupButton,
                scrollableFriendsSearch,
                scrollableFriendsContainer,
                mainChildrenComponents
        );
        friendsController.initialize();
    }
}
