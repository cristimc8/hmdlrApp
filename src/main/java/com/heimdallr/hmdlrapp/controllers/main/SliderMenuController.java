package com.heimdallr.hmdlrapp.controllers.main;


import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.utils.Async;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


public class SliderMenuController implements CustomController {
    private EventDispatcher eventDispatcher;

    private AnchorPane sliderMenu;
    private Label usernameLabel;
    private Label nameLabel;
    private Label lettersLabel;
    private HBox allUsersRow;
    private BorderPane allUsersPopupContainer;
    private BorderPane friendsPopupContainer;
    private BorderPane parent;
    private HBox yourFriendsRow;

    private UserService userService;

    public SliderMenuController(AnchorPane sliderMenu,
                                Label usernameLabel,
                                Label nameLabel,
                                Label lettersLabel,
                                HBox allUsersRow,
                                BorderPane allUsersPopupContainer,
                                BorderPane mainChildrenComponents,
                                HBox yourFriendsRow,
                                BorderPane friendsPopupContainer) {
        this.sliderMenu = sliderMenu;
        this.usernameLabel = usernameLabel;
        this.nameLabel = nameLabel;
        this.lettersLabel = lettersLabel;
        this.allUsersRow = allUsersRow;
        this.allUsersPopupContainer = allUsersPopupContainer;
        this.parent = mainChildrenComponents;
        this.yourFriendsRow = yourFriendsRow;
        this.friendsPopupContainer = friendsPopupContainer;

        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        setGUIForUser();
        this.setEventListeners();
    }

    public void setGUIForUser() {
        usernameLabel.setText(userService.getCurrentUser().getDisplayUsername());
        nameLabel.setText(userService.getCurrentUser().getFirstName() + " " + userService.getCurrentUser().getLastName());
        lettersLabel.setText(userService.getChatHeadPreviewLetters(userService.getCurrentUser()));
    }

    private void setEventListeners() {
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

    }
}
