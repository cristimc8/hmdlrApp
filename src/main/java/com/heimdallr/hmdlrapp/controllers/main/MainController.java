package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.*;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
        this.loadMessages();
        this.prepareSlideMenuAnimation();
        this.assignComponentsToControllers();
    }

    @Override
    protected void newContent() {
        this.loadMessages();
    }

    private void assignComponentsToControllers() {
        this.assignToSliderMenu();
    }

    private void assignToSliderMenu() {
        SliderMenuController sliderMenuController = new SliderMenuController(
                sliderMenu,
                usernameLabel,
                nameLabel,
                lettersLabel
                );
        sliderMenuController.setGUIForUser();
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
                    mainChildrenComponents.setOpacity(0.12);
//                    coveringBorderInjectable.setPickOnBounds(true);
                } else {
                    closeNav.setToX(-(sliderMenu.getWidth()));
                    closeNav.play();
                    Async.setTimeout(() -> {
                        sliderMenu.setVisible(false);
                        mainChildrenComponents.setOpacity(1);
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
                    mainChildrenComponents.setOpacity(1);
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
            ObservableList<javafx.scene.Node> children = this.recentChatsContainer.getChildren();
            children.remove(1, children.size() - 1);
        } catch (Exception ignored) {
        }
    }

    private List<Message> getAllUserMessages() {
        return messagesService.getAllUserPreviews(userService.getCurrentUser());
    }

    private void loadUserMessagesInGUI(List<Message> userMessages) {
        userMessages.forEach(this::addMessageToGUI);
    }

    private void addMessageToGUI(Message message) {
        ChatHeadController chatHeadController = new ChatHeadController(
                null,
                getChatHeadPreviewLetters(message),
                getFriendOrGroupName(message),
                message.getMessageBody()
        );
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
