package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class AllEventsContainer extends AnchorPane implements Subscriber, CustomController {

    BorderPane parent;

    @FXML
    AnchorPane allEventsContainer;

    @FXML
    ImageView closeEventsPopupButton;

    @FXML
    VBox injectableVboxEvents;

    public AllEventsContainer(BorderPane parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/listEventsContainer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.parent = parent;

        try {
            fxmlLoader.load();
            this.setListeners();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void newContent(String info) {

    }

    private void setListeners() {
        closeEventsPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            allEventsContainer.setVisible(false);
            parent.setOpacity(1);
        });
    }
}
