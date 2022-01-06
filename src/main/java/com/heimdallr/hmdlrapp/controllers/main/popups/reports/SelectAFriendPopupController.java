package com.heimdallr.hmdlrapp.controllers.main.popups.reports;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.controllers.misc.ListedSelectAFriendRowController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SelectAFriendPopupController extends Subscriber implements CustomController {

    ImageView closeSelectAFriendPopupButton;
    TextField scrollableSelectAFriendSearch;
    VBox scrollableSelectAFriendContainer;
    BorderPane selectAFriendPopupContainer;

    EventDispatcher eventDispatcher;
    UserService userService;
    FriendshipsService friendshipsService;

    public SelectAFriendPopupController(
            BorderPane selectAFriendPopupContainer,
            ImageView closeSelectAFriendPopupButton,
            TextField scrollableSelectAFriendSearch,
            VBox scrollableSelectAFriendContainer
    ) {
        this.closeSelectAFriendPopupButton = closeSelectAFriendPopupButton;
        this.scrollableSelectAFriendContainer = scrollableSelectAFriendContainer;
        this.scrollableSelectAFriendSearch = scrollableSelectAFriendSearch;
        this.selectAFriendPopupContainer = selectAFriendPopupContainer;
    }

    @Override
    public void initialize() {
        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        this.eventDispatcher.subscribeTo(this, Channel.onFriendSelectedForReports);
        this.eventDispatcher.subscribeTo(this, Channel.onFriendshipsChanged);
        this.setListeners();
        this.populate();
    }

    @Override
    protected void newContent(String info) {
        // When the selected username comes, we close this window
        this.populate();
        this.eventDispatcher.dispatch(Channel.guiVisibleSelectAFriend, "invisible");
    }

    private void setListeners() {
        this.closeSelectAFriendPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            eventDispatcher.dispatch(Channel.guiVisibleSelectAFriend, "invisible");
        });
    }

    private void populate() {
        scrollableSelectAFriendContainer.getChildren().clear();
        List<UserFriendshipDTO> friendships = friendshipsService.findAllWithUser(userService.getCurrentUser());
        friendships.forEach(f -> {
            User other = userService.findById(differentIdFromYours(f.getUserOne().getId(), f.getUserTwo().getId()));
            ListedSelectAFriendRowController listedSelectAFriendRowController = new ListedSelectAFriendRowController(
                    null,
                    userService.getChatHeadPreviewLetters(other),
                    other.getUsername(),
                    other.getFirstName() + " " + other.getLastName()
            );
            scrollableSelectAFriendContainer.getChildren().add(listedSelectAFriendRowController);
        });
    }

    private int differentIdFromYours(int uidOne, int uidTwo) {
        return uidOne != userService.getCurrentUser().getId() ?
                uidOne : uidTwo;
    }
}
