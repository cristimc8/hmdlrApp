package com.heimdallr.hmdlrapp.controllers.main.sliderMenu;


import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import com.heimdallr.hmdlrapp.utils.Async;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


public class SliderMenuController implements CustomController, Subscriber {
    private EventDispatcher eventDispatcher;
    private EventsService eventsService;

    private AnchorPane sliderMenu;
    private Label usernameLabel;
    private Label nameLabel;
    private Label lettersLabel;
    private HBox allUsersRow;
    private BorderPane allUsersPopupContainer;
    private BorderPane friendsPopupContainer;
    private BorderPane requestsPopupContainer;
    private BorderPane parent;
    private HBox yourFriendsRow;
    private HBox friendRequestsRow;
    private HBox createGCRow;
    private BorderPane createGCPopupContainer;
    private HBox generateReportsRow;
    private BorderPane generateReportsPopupContainer;
    BorderPane selectAFriendPopupContainer;

    Label numberOfNotificationsLabel;

    private UserService userService;

    public SliderMenuController(AnchorPane sliderMenu,
                                Label usernameLabel,
                                Label nameLabel,
                                Label lettersLabel,
                                HBox allUsersRow,
                                BorderPane allUsersPopupContainer,
                                BorderPane mainChildrenComponents,
                                HBox yourFriendsRow,
                                BorderPane friendsPopupContainer,
                                BorderPane requestsPopupContainer,
                                HBox friendRequestsRow,
                                HBox createGCRow,
                                BorderPane createGCPopupContainer,
                                HBox generateReportsRow,
                                BorderPane generateReportsPopupContainer,
                                BorderPane selectAFriendPopupContainer,
                                Label numberOfNotificationsLabel) {
        this.sliderMenu = sliderMenu;
        this.usernameLabel = usernameLabel;
        this.nameLabel = nameLabel;
        this.lettersLabel = lettersLabel;
        this.allUsersRow = allUsersRow;
        this.allUsersPopupContainer = allUsersPopupContainer;
        this.parent = mainChildrenComponents;
        this.yourFriendsRow = yourFriendsRow;
        this.friendsPopupContainer = friendsPopupContainer;
        this.friendRequestsRow = friendRequestsRow;
        this.requestsPopupContainer = requestsPopupContainer;
        this.createGCRow = createGCRow;
        this.createGCPopupContainer = createGCPopupContainer;
        this.generateReportsRow = generateReportsRow;
        this.generateReportsPopupContainer = generateReportsPopupContainer;
        this.selectAFriendPopupContainer = selectAFriendPopupContainer;
        this.numberOfNotificationsLabel = numberOfNotificationsLabel;

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        setGUIForUser();
        this.setEventListeners();
        this.eventDispatcher.subscribeTo(this, Channel.onEventsChanged);
    }

    public void setGUIForUser() {
        usernameLabel.setText(userService.getCurrentUser().getDisplayUsername());
        lettersLabel.setCursor(Cursor.HAND);
        nameLabel.setText(userService.getCurrentUser().getFirstName() + " " + userService.getCurrentUser().getLastName());
        lettersLabel.setText(userService.getChatHeadPreviewLetters(userService.getCurrentUser()));
        this.setNumberOfNotificationsLabel();
    }

    private void setEventListeners() {
        lettersLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.eventDispatcher.dispatch(Channel.guiVisibleProfilePage, "id:" + this.userService.getCurrentUser().getId());
        });
        allUsersRow.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventDispatcher.dispatch(Channel.guiVisibleAllUsersController, null);
                allUsersPopupContainer.setVisible(true);
                parent.setOpacity(0.12);

                TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
                closeNav.setToX(-(sliderMenu.getWidth()));
                closeNav.play();
                Async.setTimeout(() -> {
                    sliderMenu.setVisible(false);
//                    parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
                }, 150);
            }
        });

        yourFriendsRow.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventDispatcher.dispatch(Channel.guiVisibleFriendsController, null);
                friendsPopupContainer.setVisible(true);
                parent.setOpacity(0.12);

                TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
                closeNav.setToX(-(sliderMenu.getWidth()));
                closeNav.play();
                Async.setTimeout(() -> {
                    sliderMenu.setVisible(false);
//                    parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
                }, 150);
            }
        });

        friendRequestsRow.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventDispatcher.dispatch(Channel.guiVisibleRequestsController, null);
                requestsPopupContainer.setVisible(true);
                parent.setOpacity(0.12);

                TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
                closeNav.setToX(-(sliderMenu.getWidth()));
                closeNav.play();
                Async.setTimeout(() -> {
                    sliderMenu.setVisible(false);
//                    parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
                }, 150);
            }
        });

        createGCRow.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventDispatcher.dispatch(Channel.guiVisibleGCController, null);
                createGCPopupContainer.setVisible(true);
                parent.setOpacity(0.12);

                TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
                closeNav.setToX(-(sliderMenu.getWidth()));
                closeNav.play();
                Async.setTimeout(() -> {
                    sliderMenu.setVisible(false);
//                    parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
                }, 150);
            }
        });

        generateReportsRow.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            eventDispatcher.dispatch(Channel.guiVisibleGenerateReports, null);
            generateReportsPopupContainer.setVisible(true);
            parent.setOpacity(0.12);

            TranslateTransition closeNav = new TranslateTransition(new Duration(150), sliderMenu);
            closeNav.setToX(-(sliderMenu.getWidth()));
            closeNav.play();
            Async.setTimeout(() -> {
                sliderMenu.setVisible(false);
//                    parent.setOpacity(1);
//                        coveringBorderInjectable.setPickOnBounds(false);
            }, 150);
        });

    }

    @Override
    public void newContent(String info) {
        this.setNumberOfNotificationsLabel();
    }

    private void setNumberOfNotificationsLabel() {
        numberOfNotificationsLabel.setText(String.valueOf(eventsService.findUpcomingForUser(userService.getCurrentUser()).size()));
    }
}
