package com.heimdallr.hmdlrapp.controllers.main;


import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.utils.Async;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class SliderMenuController {

    private AnchorPane sliderMenu;
    private Label usernameLabel;
    private Label nameLabel;
    private Label lettersLabel;
    private HBox allUsersRow;
    private BorderPane allUsersPopupContainer;
    private BorderPane parent;
    private ImageView closeAllUsersPopupButton;
    private VBox scrollableUsersContainer;

    private UserService userService;
    private FriendshipsService friendshipsService;

    public SliderMenuController(AnchorPane sliderMenu,
                                Label usernameLabel,
                                Label nameLabel,
                                Label lettersLabel,
                                HBox allUsersRow,
                                BorderPane allUsersPopupContainer,
                                BorderPane mainChildrenComponents,
                                ImageView closeAllUsersPopupButton,
                                VBox scrollableUsersContainer) {
        this.sliderMenu = sliderMenu;
        this.usernameLabel = usernameLabel;
        this.nameLabel = nameLabel;
        this.lettersLabel = lettersLabel;
        this.allUsersRow = allUsersRow;
        this.allUsersPopupContainer = allUsersPopupContainer;
        this.parent = mainChildrenComponents;
        this.closeAllUsersPopupButton = closeAllUsersPopupButton;
        this.scrollableUsersContainer = scrollableUsersContainer;

        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

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
                loadAllUsersAndTheFriendships();
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

        closeAllUsersPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                allUsersPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });
    }

    private void loadAllUsersAndTheFriendships() {
        try {
            scrollableUsersContainer.getChildren().clear();
        } catch (Exception ignored) {
        }

        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            if(user.getId() == 1 || Objects.equals(user.getId(), userService.getCurrentUser().getId())) continue;
            UserRowController userRowController = new UserRowController(
                    null,
                    userService.getChatHeadPreviewLetters(user),
                    user.getUsername(),
                    user.getFirstName() + " " + user.getLastName()
            );

            try {
                scrollableUsersContainer.getChildren().add(userRowController);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
