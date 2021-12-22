package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.GroupChatsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatAreaController extends Subscriber implements CustomController {

    private EventDispatcher eventDispatcher;
    private UserService userService;
    private GroupChatsService groupChatsService;

    AnchorPane injectableCharArea;
    AnchorPane chatAreaContainer;
    VBox scrollableChatAreaContainer;
    TextArea messageTextArea;
    ImageView sendMessageButton;
    ImageView messageButton;
    HBox chatTopLeftBar;
    Label chatUsernameLabel;
    HBox messageTextAreaContainer;

    private int uid = 0;
    private String gid = null;
    private User currentUser;

    private User other;
    private GroupChat groupChat;
    private boolean isConvo;
    private double originalHeight;

    public ChatAreaController(AnchorPane injectableCharArea,
                              AnchorPane chatAreaContainer,
                              VBox scrollableChatAreaContainer,
                              TextArea messageTextArea,
                              ImageView sendMessageButton,
                              ImageView messageButton,
                              HBox chatTopLeftBar,
                              Label chatUsernameLabel,
                              HBox messageTextAreaContainer,
                              int uid) {
        this.uid = uid;
        this.isConvo = true;
        this.initArgs(injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer);
    }

    public ChatAreaController(AnchorPane injectableCharArea,
                              AnchorPane chatAreaContainer,
                              VBox scrollableChatAreaContainer,
                              TextArea messageTextArea,
                              ImageView sendMessageButton,
                              ImageView messageButton,
                              HBox chatTopLeftBar,
                              Label chatUsernameLabel,
                              HBox messageTextAreaContainer,
                              String gid) {
        this.isConvo = false;
        this.gid = gid;
        this.initArgs(injectableCharArea, chatAreaContainer, scrollableChatAreaContainer, messageTextArea,
                sendMessageButton, messageButton, chatTopLeftBar, chatUsernameLabel, messageTextAreaContainer);
    }

    private void initArgs(AnchorPane injectableCharArea,
                          AnchorPane chatAreaContainer,
                          VBox scrollableChatAreaContainer,
                          TextArea messageTextArea,
                          ImageView sendMessageButton,
                          ImageView messageButton,
                          HBox chatTopLeftBar,
                          Label chatUsernameLabel,
                          HBox messageTextAreaContainer) {
        this.injectableCharArea = injectableCharArea;
        this.chatAreaContainer = chatAreaContainer;
        this.scrollableChatAreaContainer = scrollableChatAreaContainer;
        this.messageTextArea = messageTextArea;
        this.sendMessageButton = sendMessageButton;
        this.messageButton = messageButton;
        this.chatTopLeftBar = chatTopLeftBar;
        this.chatUsernameLabel = chatUsernameLabel;
        this.messageTextAreaContainer = messageTextAreaContainer;

    }

    @Override
    public void initialize() {
        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.groupChatsService = (GroupChatsService) HmdlrDI.getContainer().getService(GroupChatsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
        this.currentUser = userService.getCurrentUser();
        this.originalHeight = this.messageTextArea.getPrefHeight();
        this.setGoodDisplayData();
        this.setEventHandlers();
    }

    private void setGoodDisplayData() {
        // Inject the chat head with letters and the username
        if(this.chatTopLeftBar.getChildren().size() > 1)
            this.chatTopLeftBar.getChildren().remove(0);
        if(this.isConvo) {
            this.other = userService.findById(uid);
            this.chatUsernameLabel.setText(this.other.getDisplayUsername());
            ProfileHeadController profileHeadController = new ProfileHeadController(
                    null,
                    userService.getChatHeadPreviewLetters(uid)
            );
            this.chatTopLeftBar.getChildren().add(0, profileHeadController);
        }
        else {
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

        ScrollBar scrollBarv = (ScrollBar)messageTextArea.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);

        messageTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(messageTextArea.getText().isBlank()) messageTextArea.setPrefHeight(56);
        });

        messageTextAreaContainer.lookup(".content").boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                messageTextArea.setPrefHeight(t1.getHeight());
            }
        });

        /*messageTextAreaContainer.lookup(".text").boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                messageTextArea.setPrefHeight(newValue.getHeight());
            }
        });*/
    }

    @Override
    protected void newContent(String info) {

    }
}
