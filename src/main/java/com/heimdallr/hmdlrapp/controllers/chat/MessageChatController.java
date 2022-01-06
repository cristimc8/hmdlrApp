package com.heimdallr.hmdlrapp.controllers.chat;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.MessagesService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MessageChatController extends AnchorPane {
    private UserService userService;
    private EventDispatcher eventDispatcher;

    @FXML
    AnchorPane messageAnchorPane;
    @FXML
    TextFlow textFlowContainer;
    @FXML
    Text messageBodyLabel;
    @FXML
    Label messageSenderLabel;
    @FXML
    ImageView replyButton;

    VBox parent;

    int mid;
    int senderId;
    private User currentUser;
    private Message message;

    public MessageChatController(Message message, VBox scrollableChatAreaContainer) {
        this.mid = message.getId();
        this.senderId = message.getSenderId();
        this.message = message;

        this.parent = scrollableChatAreaContainer;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/messageComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            try {
//            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
                this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }
            this.currentUser = userService.getCurrentUser();
            if(senderId == currentUser.getId()) {
                // set border to your sender color
                this.messageAnchorPane.setStyle("-fx-border-color: crimson;");
            }

            setResponsiveWidth(parent.getWidth());
            this.parent.widthProperty().addListener((observable, oldValue, newValue) -> {
                setResponsiveWidth((Double) newValue);
            });
            this.messageAnchorPane.hoverProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    replyButton.setVisible(newValue);
                }
            });

            this.messageBodyLabel.setText(message.getMessageBody());
            this.messageSenderLabel.setText(userService.findById(senderId).getDisplayUsername());

            this.setReplyListener();
        }
        catch (Exception ignored) {}
    }

    private void setResponsiveWidth(Double newValue) {
        this.messageAnchorPane.setPrefWidth((Double) newValue - 60);
        this.textFlowContainer.setPrefWidth((Double) newValue - 60);
    }

    private void setReplyListener() {
        this.replyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("Replying to: " + messageSenderLabel.getText() + " with id: " + this.senderId);
            this.eventDispatcher.dispatch(Channel.onReplyStarted, String.valueOf(this.mid));
        });
    }

}
