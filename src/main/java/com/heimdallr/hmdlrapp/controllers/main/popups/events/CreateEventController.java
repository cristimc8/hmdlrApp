package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
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

import java.sql.Timestamp;


public class CreateEventController extends AnchorPane implements Subscriber {

    private EventsService eventsService;
    private EventDispatcher eventDispatcher;
    private UserService userService;

    private boolean ok = false;

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

            try {
                this.eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
                this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }

            this.setListeners();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void newContent(String info) {

    }

    private void setListeners() {
        closeCreateEventBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            closePopup();
        });

        eventName.textProperty().addListener((observable, oldValue, newValue) -> {
            this.ok = !eventName.getText().isEmpty() && datePicker.getValue() != null;
            createEventButton.setDisable(!this.ok);
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.ok = !eventName.getText().isEmpty() && datePicker.getValue() != null;
            createEventButton.setDisable(!this.ok);
        });

        createEventButton.setOnAction(e -> {
            String eventNameVal = eventName.getText();
            Timestamp eventDateVal = Timestamp.valueOf(datePicker.getValue().atStartOfDay());

            this.eventsService.createEvent(eventNameVal, userService.getCurrentUser().getId(), eventDateVal);
            closePopup();
            this.eventDispatcher.dispatch(Channel.onEventSuccessfullyCreated, "success_image");
        });
    }

    private void closePopup() {
        createEventContainer.setVisible(false);
        parent.setOpacity(1);
    }
}
