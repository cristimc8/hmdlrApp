package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class CreateEventController extends AnchorPane implements CustomController, Subscriber {

    BorderPane parent;

    @FXML
    Button createEventButton;

    @FXML
    DatePicker datePicker;

    @FXML
    TextField eventName;

    @FXML
    ImageView closeCreateEventBtn;

    @FXML
    AnchorPane createEventContainer;

    public CreateEventController(BorderPane mainChildrenComponents) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/createEventContainer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.parent = mainChildrenComponents;

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
        closeCreateEventBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            createEventContainer.setVisible(false);
            parent.setOpacity(1);
        });
    }
}
