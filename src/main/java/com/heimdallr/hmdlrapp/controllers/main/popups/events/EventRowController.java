package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.HelloApplication;
import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class EventRowController extends AnchorPane {

    private UserService userService;
    private EventsService eventsService;

    private Event event;

    @FXML
    Label eventName;

    @FXML
    Label eventDate;

    @FXML
    Button actionButton;

    public EventRowController(Event event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/eventRow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
            this.event = event;

            eventName.setText(event.getEventName());
            Timestamp eventDateTimestamp = event.getEventDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String formatDateTime = eventDateTimestamp.toLocalDateTime().format(formatter);

            eventDate.setText(formatDateTime);

            this.setListeners();
            this.setButtonLogic();

        } catch (IOException | ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    private void setButtonLogic() {
        boolean userSubscribed = eventsService.isUserSubscribedToEvent(userService.getCurrentUser(), event);
        if(event.getEventCreator() == userService.getCurrentUser().getId()) {
            actionButton.setText("Delete event");
            actionButton.setOnAction(e -> {
                eventsService.deleteEvent(event);
            });
        }
        else {
            if (userSubscribed) {
                actionButton.setText("Unsubscribe");
                actionButton.setOnAction(e -> {
                    eventsService.unsubscribeFromEvent(userService.getCurrentUser(), event);
                });

            } else {
                actionButton.setText("Attend");
                actionButton.setOnAction(e -> {
                    eventsService.subscribeToEvent(userService.getCurrentUser(), event);
                });
            }
        }
    }

    private void setListeners() {
        eventName.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/heimdallr/hmdlrapp/eventPage.fxml"));
            try {
                Parent root = (Parent) fxmlLoader.load();
                stage.setTitle(event.getEventName());
                EventPageController eventPageController = (EventPageController) fxmlLoader.getController();
                eventPageController.setStageAndSetupListeners(stage);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException y) {
                y.printStackTrace();
            }

        });
    }
}
