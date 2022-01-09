package com.heimdallr.hmdlrapp.controllers.main.popups.users.requests;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.controllers.main.sliderMenu.RequestRowController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendRequestDTO;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendRequestService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestsController implements CustomController, Subscriber {
    private EventDispatcher eventDispatcher;
    private UserService userService;
    private FriendRequestService friendRequestService;

    BorderPane requestsPopupContainer;
    ImageView closeRequestsPopupButton;
    TextField scrollableRequestsSearchBar;
    VBox scrollableRequestsContainer;
    BorderPane parent;

    private String query = "";
    private User currentUser;


    public RequestsController(
            BorderPane requestsPopupContainer,
            ImageView closeRequestsPopupButton,
            TextField scrollableRequestsSearchBar,
            VBox scrollableRequestsContainer,
            BorderPane parent
    ) {
        this.requestsPopupContainer = requestsPopupContainer;
        this.closeRequestsPopupButton = closeRequestsPopupButton;
        this.scrollableRequestsSearchBar = scrollableRequestsSearchBar;
        this.scrollableRequestsContainer = scrollableRequestsContainer;
        this.parent = parent;

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendRequestService = (FriendRequestService) HmdlrDI.getContainer().getService(FriendRequestService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
        this.currentUser = userService.getCurrentUser();
    }

    @Override
    public void initialize() {
        this.setListeners();
        this.eventDispatcher.subscribeTo(this, Channel.guiVisibleRequestsController);
        this.eventDispatcher.subscribeTo(this, Channel.onFriendshipsChanged);
    }

    @Override
    public void newContent(String info) {
        this.loadAllRequests();
    }

    private void setListeners() {
        closeRequestsPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                requestsPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });

        scrollableRequestsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            this.query = scrollableRequestsSearchBar.getText();
            this.eventDispatcher.dispatch(Channel.guiVisibleRequestsController, null);
        });
    }

    private void loadAllRequests() {
        try {
            scrollableRequestsContainer.getChildren().clear();
        } catch (Exception ignored) {
        }

        List<FriendRequest> friendRequests = friendRequestService.findAllActiveForUser(currentUser);

        List<UserFriendRequestDTO> userFriendshipDTOS = new ArrayList<>();
        userFriendshipDTOS = friendRequests.stream()
                .map(friendRequest -> {
                    return new UserFriendRequestDTO(
                            friendRequest,
                            userService.findById(differentIdFromYours(friendRequest.getSenderId(), friendRequest.getReceiverId())));
                })
                .collect(Collectors.toList());

        for(UserFriendRequestDTO userFriendRequestDTO : userFriendshipDTOS) {
            if (userFriendRequestDTO.getSenderUser().getId() == 1 ||
                    Objects.equals(userFriendRequestDTO.getSenderUser().getId(), userService.getCurrentUser().getId()) ||
                    !userFriendRequestDTO.getSenderUser().getUsername().contains(query)) continue;
            RequestRowController requestRowController = new RequestRowController(
                    null,
                    userService.getChatHeadPreviewLetters(userFriendRequestDTO.getSenderUser()),
                    userFriendRequestDTO.getSenderUser().getUsername(),
                    userFriendRequestDTO.getSenderUser().getFirstName() + " " + userFriendRequestDTO.getSenderUser().getLastName(),
                    userFriendRequestDTO.getFriendRequest().getTimestamp()
            );
            try {
                scrollableRequestsContainer.getChildren().add(requestRowController);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int differentIdFromYours(int uidOne, int uidTwo) {
        return uidOne != currentUser.getId() ?
                uidOne : uidTwo;
    }
}
