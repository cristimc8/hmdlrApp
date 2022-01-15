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
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;

public class ReplyToMessageController extends AnchorPane {
    private UserService userService;
    private MessagesService messagesService;
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
    Label repliedMessageAuthorLabel;
    @FXML
    ImageView replyButton;
    VBox parent;

    int mid;
    int senderId;
    private User currentUser;
    private Message message;

    public ReplyToMessageController(Message message, VBox scrollableChatAreaContainer) {
        this.mid = message.getId();
        this.senderId = message.getSenderId();
        this.message = message;
        this.parent = scrollableChatAreaContainer;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/replyToMessageComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, hh:mm");
        String formatDateTime = message.getTimestamp().toLocalDateTime().format(formatter);
        Tooltip.install(this, new Tooltip(formatDateTime));

        try {
            fxmlLoader.load();

            try {
//            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
                this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
                this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }
            this.currentUser = userService.getCurrentUser();
            this.setReplyListener();
            if (senderId == currentUser.getId()) {
                // set border to your sender color
                this.messageAnchorPane.setStyle("-fx-border-color: crimson;");
            }
            this.messageBodyLabel.setText(message.getMessageBody());
            this.messageSenderLabel.setText(userService.findById(senderId).getDisplayUsername());
            if (this.message.getReplyTo() > 0) {
                String repliedMessageBody = messagesService.findById(message.getReplyTo()).getMessageBody();
                String repliedMessageAuthor = "";
                if (repliedMessageBody == null) repliedMessageBody = "Deleted message";
                else {
                    repliedMessageAuthor = userService.findById(
                            messagesService.findById(message.getReplyTo()).getSenderId()
                    ).getDisplayUsername();
                    repliedMessageAuthor += "'s";
                }
                System.out.println(message.getMessageBody());

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

                this.messageSenderLabel.setText(userService.findById(senderId).getDisplayUsername());
                this.repliedMessageAuthorLabel.setText("Replying to " + repliedMessageAuthor + " " + repliedMessageBody);
                this.messageAnchorPane.setPrefWidth(messageAnchorPane.getParent().getLayoutBounds().getWidth());

            }
        } catch (Exception ignored) {
        }
    }

    private void setResponsiveWidth(Double newValue) {
        this.messageAnchorPane.setPrefWidth((Double) newValue - 60);
        this.textFlowContainer.setPrefWidth((Double) newValue - 60);
        this.repliedMessageAuthorLabel.setPrefWidth(this.messageAnchorPane.getPrefWidth());
    }

    private void setReplyListener() {
        this.replyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("Replying to: " + messageSenderLabel.getText() + " with id: " + this.senderId);
            this.eventDispatcher.dispatch(Channel.onReplyStarted, String.valueOf(this.mid));
        });
    }
}
