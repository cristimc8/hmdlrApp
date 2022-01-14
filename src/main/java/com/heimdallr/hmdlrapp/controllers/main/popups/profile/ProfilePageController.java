package com.heimdallr.hmdlrapp.controllers.main.popups.profile;

import com.heimdallr.hmdlrapp.controllers.misc.ProfileHeadController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.Page;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.List;

public class ProfilePageController extends AnchorPane implements Subscriber {

    BorderPane parent;
    UserService userService;
    EventDispatcher eventDispatcher;
    EventsService eventsService;
    Page page;

    @FXML
    AnchorPane profileContainer;

    @FXML
    VBox profileVbox;

    @FXML
    Label name;

    @FXML
    ImageView closeButton;

    @FXML
    Label username;

    @FXML
    Label email;

    @FXML
    Label attendance;

    @FXML
    VBox injectHistoryBox;

    public ProfilePageController(BorderPane parent, User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/profile/profilePage.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.parent = parent;

        try {
            fxmlLoader.load();

            try {
                userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
                eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }

            eventDispatcher.subscribeTo(this, Channel.onEventDeleted);
            eventDispatcher.subscribeTo(this, Channel.onEventsChanged);

            this.page = new Page(user);

            this.populate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newContent(String info) {
        this.populate();
    }

    private void populate() {
        this.name.setText(page.getFname() + " " + page.getLname());
        this.username.setText("Socialising as " + page.getUser().getDisplayUsername());
        this.email.setText("Emailing as " + page.getUser().getEmail());
        this.profileVbox.getChildren().remove(0);
        this.profileVbox.getChildren().add(0, new ProfileHeadController(null, userService.getChatHeadPreviewLetters(this.page.getUser())));
        this.attendance.setText(this.page.getUser().getDisplayUsername() + " is attending " + eventsService.findAllForUser(page.getUser()).size() + " public events");
        List<FriendRequest> pastFriendRequests = this.page.getFriendRequests();
        List<String> past = pastFriendRequests.stream().map(e -> {
            String word;
            String prop = "";
            switch (e.getFriendshipRequestStatus()) {
                case APPROVED -> word = " approved a request from ";
                case CANCELED -> word = " retracted a request to ";
                case REJECTED -> word = " rejected a request from ";
                case PENDING -> word = " waiting for response from ";
                default -> word = " sent a request to ";
            }
            switch (e.getFriendshipRequestStatus()) {
                case APPROVED, REJECTED -> prop = userService.findById(e.getReceiverId()).getDisplayUsername() + word + userService.findById(e.getSenderId()).getDisplayUsername();
                case CANCELED, PENDING -> prop = userService.findById(e.getSenderId()).getDisplayUsername() + word + userService.findById(e.getReceiverId()).getDisplayUsername();

            }
            return prop;
        }).toList();

        injectHistoryBox.getChildren().clear();
        past.forEach(s -> {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            Label label = new Label();
            label.setText(s);
            label.setTextFill(Paint.valueOf("#ffffff"));
            label.setFont(Font.font("Garuda"));
            hbox.getChildren().add(label);

            injectHistoryBox.getChildren().add(hbox);
        });

        this.setListeners();
    }

    private void setListeners() {
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.profileContainer.setVisible(false);
        });
    }
}
