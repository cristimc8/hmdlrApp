package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.controllers.main.popups.reports.GenerateReportsController;
import com.heimdallr.hmdlrapp.controllers.main.popups.reports.SelectAFriendPopupController;
import com.heimdallr.hmdlrapp.controllers.main.popups.users.AllUsersController;
import com.heimdallr.hmdlrapp.controllers.main.popups.groupChats.CreateGCController;
import com.heimdallr.hmdlrapp.controllers.main.popups.users.FriendsController;
import com.heimdallr.hmdlrapp.controllers.main.popups.users.requests.RequestsController;
import com.heimdallr.hmdlrapp.controllers.main.sliderMenu.SliderMenuController;
import com.heimdallr.hmdlrapp.exceptions.*;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.GroupChatsService;
import com.heimdallr.hmdlrapp.services.MessagesService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;


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
    HBox friendRequestsRow;

    @FXML
    HBox createGCRow;

    @FXML
    HBox generateReportsRow;

    @FXML
    BorderPane generateReportsPopupContainer;

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
    BorderPane requestsPopupContainer;

    @FXML
    ImageView closeRequestsPopupButton;

    @FXML
    VBox scrollableRequestsContainer;

    @FXML
    TextField scrollableRequestsSearchBar;


    // Create GC
    @FXML
    BorderPane createGCPopupContainer;

    @FXML
    ImageView closeCreateGCPopup;

    @FXML
    TextField chatAliasTextBox;

    @FXML
    Button finishCreateGCActionButton;

    @FXML
    VBox scrollableGCFriendsContainer;

    @FXML
    VBox scrollableMembersContainer;

    @FXML
    TextField scrollableSearchMembersTextBox;

    // Chat area
    @FXML
    AnchorPane chatAreaContainer;

    @FXML
    BorderPane selectAChatSystemContainer;

    @FXML
    AnchorPane injectableCharArea;

    @FXML
    VBox scrollableChatAreaContainer;

    @FXML
    TextArea messageTextArea;

    @FXML
    ImageView sendMessageButton;

    @FXML
    HBox chatTopLeftBar;

    @FXML
    Label chatUsernameLabel;

    @FXML
    HBox messageTextAreaContainer;

    @FXML
    ScrollPane parentScrollPane;

    // generate reports
    @FXML
    Button friendsAndMessagesButton;
    @FXML
    Button messagesWithFriendsButton;
    @FXML
    DatePicker firtstDatePicker;
    @FXML
    DatePicker secondDatePicker;
    @FXML
    ImageView closeGenerateReportsContainer;

    // Select a friend
    @FXML
    BorderPane selectAFriendPopupContainer;
    @FXML
    ImageView closeSelectAFriendPopupButton;
    @FXML
    TextField scrollableSelectAFriendSearch;
    @FXML
    VBox scrollableSelectAFriendContainer;

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

//        this.eventDispatcher.subscribeTo(this, Channel.onNewMessage);
        this.eventDispatcher.subscribeTo(this, Channel.guiVisibleSelectAFriend);
        this.assignComponentsToControllers();
    }

    @Override
    protected void newContent(String info) {
//        this.loadMessages();
        if (Objects.equals(info, "visible")) {
            this.selectAFriendPopupContainer.setVisible(true);
            this.generateReportsPopupContainer.setOpacity(0.16);
        }
        else {
            this.selectAFriendPopupContainer.setVisible(false);
            this.generateReportsPopupContainer.setOpacity(1);
        }
    }

    private void assignComponentsToControllers() {
        this.assignToSliderMenu();
        this.assignToAllUsersPopup();
        this.assignToLeftBar();
        this.assignToFriends();
        this.assignToRequests();
        this.assignToCreateGC();
        this.assignToGenerateReports();
        this.assignToSelectAFriend();
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
                friendsPopupContainer,
                requestsPopupContainer,
                friendRequestsRow,
                createGCRow,
                createGCPopupContainer,
                generateReportsRow,
                generateReportsPopupContainer,
                selectAFriendPopupContainer
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
                closeSliderMenuButton,
                selectAChatSystemContainer,
                injectableCharArea,
                chatAreaContainer,
                scrollableChatAreaContainer,
                messageTextArea,
                sendMessageButton,
                sendMessageButton,
                chatTopLeftBar,
                chatUsernameLabel,
                messageTextAreaContainer,
                parentScrollPane
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

    private void assignToRequests() {
        RequestsController requestsController = new RequestsController(
                requestsPopupContainer,
                closeRequestsPopupButton,
                scrollableRequestsSearchBar,
                scrollableRequestsContainer,
                mainChildrenComponents
        );
        requestsController.initialize();
    }

    private void assignToCreateGC() {
        CreateGCController createGCController = new CreateGCController(
                createGCPopupContainer,
                closeCreateGCPopup,
                chatAliasTextBox,
                finishCreateGCActionButton,
                scrollableGCFriendsContainer,
                scrollableMembersContainer,
                mainChildrenComponents,
                scrollableSearchMembersTextBox
        );
        createGCController.initialize();
    }

    private void assignToGenerateReports() {
        GenerateReportsController generateReportsController = new GenerateReportsController(
                friendsAndMessagesButton,
                messagesWithFriendsButton,
                firtstDatePicker,
                secondDatePicker,
                closeGenerateReportsContainer,
                generateReportsPopupContainer,
                mainChildrenComponents
        );
        generateReportsController.initialize();
    }

    private void assignToSelectAFriend() {
        SelectAFriendPopupController selectAFriendPopupController = new SelectAFriendPopupController(
                selectAFriendPopupContainer,
                closeSelectAFriendPopupButton,
                scrollableSelectAFriendSearch,
                scrollableSelectAFriendContainer
        );
        selectAFriendPopupController.initialize();
    }
}
