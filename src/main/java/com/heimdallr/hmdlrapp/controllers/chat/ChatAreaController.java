package com.heimdallr.hmdlrapp.controllers.chat;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.controllers.misc.ProfileHeadController;
import com.heimdallr.hmdlrapp.controllers.misc.SystemNotificationController;
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
import com.heimdallr.hmdlrapp.utils.Async;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAreaController implements CustomController, Subscriber {

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
    VBox messageTextAreaContainer;
    ScrollPane parentScrollPane;

    Tooltip gcMembers;

    private int uid = 0;
    private String gid = null;
    private User currentUser;

    private User other;
    private GroupChat groupChat;
    private boolean isConvo;
    private double originalHeight;
    private Message messageToReplyTo;

    private boolean scrollListenerSet = false;

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
                          VBox messageTextAreaContainer,
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
                        VBox messageTextAreaContainer,
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
                        VBox messageTextAreaContainer,
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
        this.firstLoad();
        this.eventDispatcher.subscribeTo(this, Channel.onNewMessage);
        this.eventDispatcher.subscribeTo(this, Channel.onReplyStarted);
    }

    private void setGoodDisplayData() {
        this.messageTextArea.setDisable(false);
        setReplyMode(false);
        this.messageTextArea.setPromptText("Write a message");
        if(!scrollListenerSet) {
            this.scrollableChatAreaContainer.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    System.out.println(oldValue + " " + newValue);
                    if (parentScrollPane.getVmin() != parentScrollPane.getVvalue()) {
                        parentScrollPane.setVvalue((Double) newValue);
                    }
                }
            });
        }

        // Inject the chat head with letters and the username
        if (this.chatTopLeftBar.getChildren().size() > 1)
            this.chatTopLeftBar.getChildren().remove(0);
        if (this.isConvo) {
            Tooltip.uninstall(chatTopLeftBar, gcMembers);
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
            if(gcMembers == null) {
                gcMembers = new Tooltip();
            }
            setGCMembersForTooltip(gcMembers);
            Tooltip.install(chatTopLeftBar, gcMembers);
        }
    }

    private void setGCMembersForTooltip(Tooltip tooltip) {
        StringBuilder stringBuilder = new StringBuilder("");
        List<User> users = groupChatsService.findById(this.gid).getParticipants()
                .stream().map(id -> {
                    return userService.findById(id);
                }).toList();
        users.forEach(user -> {
            stringBuilder.append(user.getDisplayUsername()).append("\n");
        });
        tooltip.setText(stringBuilder.toString());
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

        EventHandler<InputEvent> handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!messageTextArea.getText().isBlank()) {
                    // send message
                    System.out.println("Sending message to: " + (isConvo ? other.getUsername() : groupChat.getAlias()));
                    if (isConvo){
                        if(messageToReplyTo == null)
                            messagesService.sendMessage(currentUser, other, messageTextArea.getText());
                        else messagesService.replyToMessage(currentUser, other, messageToReplyTo.getId(), messageTextArea.getText());
                    }
                    else{
                        if(messageToReplyTo == null)
                            messagesService.sendMessage(currentUser, groupChat, messageTextArea.getText());
                        else messagesService.replyToMessage(currentUser, groupChat, messageToReplyTo.getId(), messageTextArea.getText());
                    }
                    setReplyMode(false);
                    parentScrollPane.setVvalue(1.0);
                    messageTextArea.clear();
                }
            }
        };

        sendMessageButton.setOnMouseClicked(handler);
        // for loading messages when scroll is at the top
        this.setScrollWheelEvents();
    }

    private void setScrollWheelEvents() {
        if(!scrollListenerSet) {
            this.parentScrollPane.vvalueProperty().addListener(
                    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        if (newValue.doubleValue() == 0 && oldValue.doubleValue() != 1.0) {
                            this.loadMoreMessages();
                        }
                    });
            this.scrollListenerSet = true;
        }
    }

    @Override
    public void newContent(String info) {
        // Retrieve the last message without deleting the rest of them
        if(info == null) {
            Message latest = this.fetchLastMessage();
            displayLastMessage(latest);
        }
        else {
            int mid = Integer.parseInt(info);
            // set gui for reply
            messageToReplyTo = messagesService.findById(mid);
            setReplyMode(true);
        }
    }

    private void setReplyMode(boolean replying) {
        if(replying) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPrefHeight(30);
            hBox.setPadding(new Insets(5, 15, 5, 15));
            Label label = new Label();
            label.setText("Replying to: " + userService.findById(messageToReplyTo.getSenderId()).getDisplayUsername() + "'s: " + messageToReplyTo.getMessageBody());
            label.setFont(Font.font("Garuda"));
            label.setTextFill(new Color(0.8, 0.8, 0.8, 1));

            HBox hBox1 = new HBox();
            hBox1.setPadding(new Insets(0, 0, 0, 15));

            Button button = new Button();
            button.setText("X");
            button.setOnAction(e -> {
                setReplyMode(false);
            });

            hBox1.getChildren().add(button);

            hBox.getChildren().add(label);
            hBox.getChildren().add(hBox1);
            messageTextAreaContainer.getChildren().add(0, hBox);
        }
        else {
            messageToReplyTo = null;
            if(messageTextAreaContainer.getChildren().size() == 2)
                messageTextAreaContainer.getChildren().remove(0);
        }
    }

    private void displayLastMessage(Message latest) {
        if (this.isReplyMessage(latest)) {
            ReplyToMessageController replyToMessageController = new ReplyToMessageController(
                    latest,
                    scrollableChatAreaContainer
            );
            scrollableChatAreaContainer.getChildren().add(replyToMessageController);
        } else {
            MessageChatController messageChatController = new MessageChatController(
                    latest,
                    scrollableChatAreaContainer
            );
            scrollableChatAreaContainer.getChildren().add(messageChatController);
        }
    }

    // This method is bound to the scroll wheel when it reaches the 0 position
    private void loadMoreMessages() {
        List<Message> allMessages = this.fetchMessages();
        this.displayMessages(allMessages);
    }

    private void firstLoad() {
        // Clears chat for first loading
        this.scrollableChatAreaContainer.getChildren().clear();
        // Sets service page to 0
        this.messagesService.resetPage();
        this.loadMessages();
    }

    private void loadMessages() {
        // Retrieves next page of conversation
        List<Message> allMessages = this.fetchMessages();
        // Display messages in the GUI
        this.displayMessages(allMessages);
        Async.setTimeout(() -> {
            parentScrollPane.setVvalue(1.0);
        }, 50);
    }

    private List<Message> fetchMessages() {
        List<Message> allMessages = new ArrayList<>();
        if (this.isConvo) {
            System.out.println("Loading messages with " + other.getUsername());
            allMessages = messagesService.findAllBetweenUsers(currentUser, other);
        } else {
            System.out.println("Loading messages with " + groupChat.getAlias());
            allMessages = messagesService.findAllForGroup(groupChat);
        }
        return allMessages;
    }

    private Message fetchLastMessage() {
        Message latest;
        if(this.isConvo)
            latest = this.messagesService.latestMessageBetweenUsers(currentUser.getId(), other.getId());
        else latest = this.messagesService.latestMessageForGroup(gid);
        return latest;
    }

    private void displayMessages(Iterable<Message> allMessages) {
        List<Pane> displayedMessages = new ArrayList<>();
        for (Message message : allMessages) {
            if (this.isSystemMessage(message)) {
                HBox hBox = this.getSystemNotification(message);
                displayedMessages.add(hBox);
                continue;
            }
            if (this.isReplyMessage(message)) {
                ReplyToMessageController replyToMessageController = new ReplyToMessageController(
                        message,
                        scrollableChatAreaContainer
                );
                displayedMessages.add(replyToMessageController);
            } else {
                MessageChatController messageChatController = new MessageChatController(
                        message,
                        scrollableChatAreaContainer
                );
                displayedMessages.add(messageChatController);
            }
        }
        this.scrollableChatAreaContainer.getChildren().addAll(0, displayedMessages);
    }

    /**
     * Gets the system notification wrapped in a HBox
     */
    private HBox getSystemNotification(Message message) {
        // message from system
        SystemNotificationController systemNotificationController = new SystemNotificationController(
                message.getMessageBody()
        );

        HBox hBox = new HBox();
        hBox.setPrefWidth(scrollableChatAreaContainer.getPrefWidth());
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 5, 5, 5));
        hBox.getChildren().add(systemNotificationController);
        return hBox;
    }

    private boolean isSystemMessage(Message message) {
        return message.getSenderId() == 1 || message.getReplyTo() == -1;
    }

    private boolean isReplyMessage(Message message) {
        return message.getReplyTo() > 0;
    }
}
