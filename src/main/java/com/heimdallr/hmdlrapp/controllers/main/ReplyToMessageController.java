package com.heimdallr.hmdlrapp.controllers.main;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.MessagesService;
import com.heimdallr.hmdlrapp.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ReplyToMessageController extends AnchorPane {
    private UserService userService;
    private MessagesService messagesService;


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

    int mid;
    int senderId;
    private User currentUser;
    private Message message;

    public ReplyToMessageController(Message message) {
        this.mid = message.getId();
        this.senderId = message.getSenderId();
        this.message = message;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/replyToMessageComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            try {
//            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
                this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }
            this.currentUser = userService.getCurrentUser();
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

                setLeftAnchor(this.messageAnchorPane, 0.0);
                setRightAnchor(this.messageAnchorPane, 0.0);
                this.messageSenderLabel.setText(userService.findById(senderId).getDisplayUsername());
                this.repliedMessageAuthorLabel.setText("Replying to " + repliedMessageAuthor + " " + repliedMessageBody);
                this.repliedMessageAuthorLabel.setPrefWidth(this.messageAnchorPane.getPrefWidth());
                this.messageAnchorPane.setPrefWidth(messageAnchorPane.getParent().getLayoutBounds().getWidth());

            }
        } catch (Exception ignored) {
        }
    }
}
