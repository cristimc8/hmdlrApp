package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.GroupChat;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LeftBarController extends Subscriber implements CustomController {
    private UserService userService;
    private MessagesService messagesService;
    private GroupChatsService groupChatsService;
    private EventDispatcher eventDispatcher;

    private String query = "";
    private ChatAreaController chatAreaController = new ChatAreaController();

    VBox recentChatsContainer;
    ImageView burgerMenuButton;
    TextField searchTextBox;
    BorderPane parent;
    AnchorPane sliderMenu;
    ImageView closeSliderMenuButton;
    BorderPane selectAChatSystemContainer;
    AnchorPane injectableCharArea;

    // Chat area
    AnchorPane chatAreaContainer;
    VBox scrollableChatAreaContainer;
    TextArea messageTextArea;
    ImageView sendMessageButton;
    ImageView messageButton;
    HBox chatTopLeftBar;
    Label chatUsernameLabel;
    HBox messageTextAreaContainer;
    ScrollPane parentScrollPane;

    public LeftBarController(VBox recentChatsContainer,
                             ImageView burgerMenuButton,
                             TextField searchTextBox,
                             BorderPane parent,
                             AnchorPane sliderMenu,
                             ImageView closeSliderMenuButton,
                             BorderPane selectAChatSystemContainer,
                             AnchorPane injectableCharArea,
                             AnchorPane chatAreaContainer,
                             VBox scrollableChatAreaContainer,
                             TextArea messageTextArea,
                             ImageView sendMessageButton,
                             ImageView messageButton,
                             HBox chatTopLeftBar,
                             Label chatUsernameLabel,
                             HBox messageTextAreaContainer,
                             ScrollPane parentScrollPane) {
        this.recentChatsContainer = recentChatsContainer;
        this.burgerMenuButton = burgerMenuButton;
        this.searchTextBox = searchTextBox;
        this.parent = parent;
        this.sliderMenu = sliderMenu;
        this.closeSliderMenuButton = closeSliderMenuButton;
        this.selectAChatSystemContainer = selectAChatSystemContainer;
        this.injectableCharArea = injectableCharArea;

        this.chatAreaContainer = chatAreaContainer;
        this.scrollableChatAreaContainer = scrollableChatAreaContainer;
        this.messageTextArea = messageTextArea;
        this.sendMessageButton = sendMessageButton;
        this.messageButton = messageButton;
        this.chatTopLeftBar = chatTopLeftBar;
        this.chatUsernameLabel = chatUsernameLabel;
        this.messageTextAreaContainer = messageTextAreaContainer;
        this.parentScrollPane = parentScrollPane;
    }

    @Override
    public void initialize() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
            this.groupChatsService = (GroupChatsService) HmdlrDI.getContainer().getService(GroupChatsService.class);
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        this.eventDispatcher.subscribeTo(this, Channel.onNewMessage);

        this.setEventListeners();
        this.loadMessages();
        this.prepareSlideMenuAnimation();
    }

    @Override
    protected void newContent(String info) {
        this.loadMessages();
    }

    private void setEventListeners() {
        searchTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            this.query = searchTextBox.getText();
            this.eventDispatcher.dispatch(Channel.onNewMessage, null);
        });
    }


    private void prepareSlideMenuAnimation() {
        TranslateTransition openNav = new TranslateTransition(new Duration(150), sliderMenu);
        openNav.setToX(0);
        TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
        burgerMenuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sliderMenu.setVisible(true);

                // Animation
                if (sliderMenu.getTranslateX() != 0) {
                    openNav.play();
                    parent.setOpacity(0.12);
//                    coveringBorderInjectable.setPickOnBounds(true);
                } else {
                    closeNav.setToX(-(sliderMenu.getWidth()));
                    closeNav.play();
                    Async.setTimeout(() -> {
                        sliderMenu.setVisible(false);
                        parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
                    }, 150);

                }
            }
        });

        closeSliderMenuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Animation
                closeNav.setToX(-(sliderMenu.getWidth()));
                closeNav.play();
                Async.setTimeout(() -> {
                    sliderMenu.setVisible(false);
                    parent.setOpacity(1);
                }, 150);
            }
        });
    }

    /**
     * Queries all user's messages(previews) and then loads them in the GUI.
     */
    private void loadMessages() {
        this.clearMessagesArea();
        List<Message> allMessages = this.getAllUserMessages();
        this.loadUserMessagesInGUI(allMessages);
    }

    private void clearMessagesArea() {
        try {
            ObservableList<Node> children = this.recentChatsContainer.getChildren();
            children.remove(1, children.size());
        } catch (Exception ignored) {
        }
    }

    private List<Message> getAllUserMessages() {
        List<GroupChat> userGroups = groupChatsService.getAllForUser(userService.getCurrentUser());
        Iterable<Message> messages = messagesService.getAllUserPreviews(userService.getCurrentUser(), userGroups);
        List<Message> filtered = new ArrayList<>();

        messages.forEach(message -> {
            if (message.getGroupId() == null) {
                int id = getDifferentIdFromMessage(message);
                if (userService.findById(id).getUsername().contains(query))
                    filtered.add(message);
            } else {
                if (groupChatsService.findById(message.getGroupId()).getAlias().contains(query))
                    filtered.add(message);
            }
        });

        return filtered;
    }

    private void loadUserMessagesInGUI(List<Message> userMessages) {
        userMessages.forEach(this::addMessageToGUI);
    }

    private void addMessageToGUI(Message message) {
        ChatHeadController chatHeadController;
        if (message.getGroupId() == null) {
            chatHeadController = new ChatHeadController(
                    null,
                    getChatHeadPreviewLetters(message),
                    getFriendOrGroupName(message),
                    message.getMessageBody(),
                    getDifferentIdFromMessage(message)
            );
        } else {
            chatHeadController = new ChatHeadController(
                    null,
                    getChatHeadPreviewLetters(message),
                    getFriendOrGroupName(message),
                    message.getMessageBody(),
                    message.getGroupId()
            );
        }
        chatHeadController.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (chatHeadController.getGid() == null) {
                // Could do some events here but im lazy and this aint that complicated
                // means it's convo
                int otId = chatHeadController.getUid();
                chatAreaController.setArgs(
                        injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                        sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer, parentScrollPane,
                        otId
                );
            } else {
                // Means group
                String gid = chatHeadController.getGid();
                chatAreaController.setArgs(
                        injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                        sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer, parentScrollPane,
                        gid
                );
            }

            chatAreaController.initialize();

            injectableCharArea.setVisible(true);
            selectAChatSystemContainer.setVisible(false);
        });
        try {
            recentChatsContainer.getChildren().add(chatHeadController);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the id of either the sender or receiver of a message,
     * id that is different from the currently logged-in user.
     *
     * @param message Message to extract info from
     * @return The id of the user
     */
    private int getDifferentIdFromMessage(Message message) {
        return message.getReceiverId() != userService.getCurrentUser().getId() ?
                message.getReceiverId() :
                message.getSenderId();
    }

    /**
     * Returns the chat head associated name, either a friend's username or
     * a group's alias.
     *
     * @param message Message to extract info from
     * @return The chat head associated name String
     */
    private String getFriendOrGroupName(Message message) {
        if (message.getGroupId() != null)
            return groupChatsService.findById(message.getGroupId()).getAlias();
        return userService.findById(getDifferentIdFromMessage(message)).getDisplayUsername();
    }

    /**
     * Returns the chatHead's preview letters (if no image is assigned)
     * For a group, we return the first and last letter of the group's alias (or the first)
     * For a user, the initials of the user's names.
     *
     * @param message Message to extract info from
     * @return The chatHead's preview letters
     */
    private String getChatHeadPreviewLetters(Message message) {
        if (message.getGroupId() != null)
            return groupChatsService.getChatHeadPreviewLetters(message.getGroupId());
        // else return the user's names initials
        return userService.getChatHeadPreviewLetters(getDifferentIdFromMessage(message));
    }
}
