package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.GroupChatsService;
import com.heimdallr.hmdlrapp.services.MessagesService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatAreaController extends Subscriber implements CustomController {

    private EventDispatcher eventDispatcher;
    private UserService userService;
    private GroupChatsService groupChatsService;
    private MessagesService messagesService;

    AnchorPane injectableCharArea;
    AnchorPane chatAreaContainer;
    VBox scrollableChatAreaContainer;
    TextArea messageTextArea;
    ImageView sendMessageButton;
    ImageView messageButton;
    HBox chatTopLeftBar;
    Label chatUsernameLabel;
    HBox messageTextAreaContainer;
    ScrollPane parentScrollPane;

    private int uid = 0;
    private String gid = null;
    private User currentUser;

    private User other;
    private GroupChat groupChat;
    private boolean isConvo;
    private double originalHeight;

    public ChatAreaController() {

    }

    private void initArgs(AnchorPane injectableCharArea,
                          AnchorPane chatAreaContainer,
                          VBox scrollableChatAreaContainer,
                          TextArea messageTextArea,
                          ImageView sendMessageButton,
                          ImageView messageButton,
                          HBox chatTopLeftBar,
                          Label chatUsernameLabel,
                          HBox messageTextAreaContainer,
                          ScrollPane parentScrollPane) {
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

    public void setArgs(AnchorPane injectableCharArea,
                        AnchorPane chatAreaContainer,
                        VBox scrollableChatAreaContainer,
                        TextArea messageTextArea,
                        ImageView sendMessageButton,
                        ImageView messageButton,
                        HBox chatTopLeftBar,
                        Label chatUsernameLabel,
                        HBox messageTextAreaContainer,
                        ScrollPane parentScrollPane,
                        int uid) {
        this.uid = uid;
        this.isConvo = true;
        this.initArgs(injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer, parentScrollPane);
    }

    public void setArgs(AnchorPane injectableCharArea,
                        AnchorPane chatAreaContainer,
                        VBox scrollableChatAreaContainer,
                        TextArea messageTextArea,
                        ImageView sendMessageButton,
                        ImageView messageButton,
                        HBox chatTopLeftBar,
                        Label chatUsernameLabel,
                        HBox messageTextAreaContainer,
                        ScrollPane parentScrollPane,
                        String gid) {
        this.isConvo = false;
        this.gid = gid;
        this.initArgs(injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer, parentScrollPane);
    }

    @Override
    public void initialize() {
        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.groupChatsService = (GroupChatsService) HmdlrDI.getContainer().getService(GroupChatsService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
        this.currentUser = userService.getCurrentUser();
        this.originalHeight = this.messageTextArea.getPrefHeight();
        this.setGoodDisplayData();
        this.setEventHandlers();
        this.loadMessages();
        this.eventDispatcher.subscribeTo(this, Channel.onNewMessage);
    }

    private void setGoodDisplayData() {
        this.messageTextArea.setDisable(false);
        this.messageTextArea.setPromptText("Write a message");
        // Making the scroll to bottom automatically
        this.parentScrollPane.setVvalue(1.0);
        this.scrollableChatAreaContainer.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                parentScrollPane.setVvalue((Double) newValue);
            }
        });

        // Inject the chat head with letters and the username
        if (this.chatTopLeftBar.getChildren().size() > 1)
            this.chatTopLeftBar.getChildren().remove(0);
        if (this.isConvo) {
            this.groupChat = null;
            this.other = userService.findById(uid);
            if (this.other.getId() == 1) {
                this.messageTextArea.setDisable(true);
                this.messageTextArea.setPromptText("You can't talk to the system! Find some friends.");
            }
            this.chatUsernameLabel.setText(this.other.getDisplayUsername());
            ProfileHeadController profileHeadController = new ProfileHeadController(
                    null,
                    userService.getChatHeadPreviewLetters(uid)
            );
            this.chatTopLeftBar.getChildren().add(0, profileHeadController);
        } else {
            this.other = null;
            this.groupChat = groupChatsService.findById(gid);
            this.chatUsernameLabel.setText(this.groupChat.getAlias());
            ProfileHeadController profileHeadController = new ProfileHeadController(
                    null,
                    groupChatsService.getChatHeadPreviewLetters(gid)
            );
            this.chatTopLeftBar.getChildren().add(0, profileHeadController);
        }
    }

    private void setEventHandlers() {

        messageTextArea.setText("");

        ScrollBar scrollBarv = (ScrollBar) messageTextArea.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);

        messageTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (messageTextArea.getText().isBlank()) messageTextArea.setPrefHeight(56);
        });

        messageTextAreaContainer.lookup(".content").boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                messageTextArea.setPrefHeight(t1.getHeight());
            }
        });

/*        try {
            sendMessageButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, sendMessageButton.getOnMouseClicked());
        }
        catch (Exception ignored) {}*/

        EventHandler<InputEvent> handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!messageTextArea.getText().isBlank()) {
                    // send message
                    System.out.println("Sending message to: " + (isConvo ? other.getUsername() : groupChat.getAlias()));
                    if (isConvo)
                        messagesService.sendMessage(currentUser, other, messageTextArea.getText());
                    else
                        messagesService.sendMessage(currentUser, groupChat, messageTextArea.getText());

                    messageTextArea.clear();
                }
            }
        };

/*
        try {
            sendMessageButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
*/

        sendMessageButton.setOnMouseClicked(handler);

        /*messageTextAreaContainer.lookup(".text").boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                messageTextArea.setPrefHeight(newValue.getHeight());
            }
        });*/
    }

    @Override
    protected void newContent(String info) {
        this.loadMessages();
    }

    private void loadMessages() {
        this.scrollableChatAreaContainer.getChildren().clear();
        List<Message> allMessages = new ArrayList<>();
        if (this.isConvo) {
            System.out.println("Loading convo with " + other.getUsername());
            allMessages = messagesService.findAllBetweenUsers(currentUser, other);
        } else {
            System.out.println("Loading convo with " + groupChat.getAlias());
            allMessages = messagesService.findAllForGroup(groupChat);
        }
        for (Message message : allMessages) {
            if (message.getSenderId() == 1 || message.getReplyTo() == -1) {
                // message from system
                SystemNotificationController systemNotificationController = new SystemNotificationController(
                        message.getMessageBody()
                );

                HBox hBox = new HBox();
                hBox.setPrefWidth(scrollableChatAreaContainer.getPrefWidth());
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(10, 5, 5, 5));
                hBox.getChildren().add(systemNotificationController);
                this.scrollableChatAreaContainer.getChildren().add(hBox);
                continue;
            }
            if(message.getReplyTo() > 0) {
                ReplyToMessageController replyToMessageController = new ReplyToMessageController(
                        message
                );
                this.scrollableChatAreaContainer.getChildren().add(replyToMessageController);
            }
            else {
                MessageChatController messageChatController = new MessageChatController(
                        message
                );
                this.scrollableChatAreaContainer.getChildren().add(messageChatController);
            }

        }
        this.parentScrollPane.setVvalue(1.0);

    }
}
