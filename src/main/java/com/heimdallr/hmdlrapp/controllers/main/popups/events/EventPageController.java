package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.UserService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class EventPageController extends AnchorPane implements Subscriber {

    UserService userService;
    EventsService eventsService;
    private EventDispatcher eventDispatcher;

    private Event event;
    private Stage stage;

    @FXML
    Label eventName;

    @FXML
    Label creatorName;

    @FXML
    Label eventDate;

    @FXML
    Button actionButton;

    public void setStageAndSetupListeners(Stage stage) {
        try {
            userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
            eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
        this.stage = stage;

        this.event = eventsService.findByName(stage.getTitle());

        creatorName.setText("Created by " + userService.findById(event.getEventCreator()).getDisplayUsername());
        eventName.setText("Public event " + event.getEventName());
        Timestamp eventDateTimestamp = event.getEventDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formatDateTime = eventDateTimestamp.toLocalDateTime().format(formatter);

        eventDate.setText("Taking place @ " + formatDateTime);

        setButtonLogic();

        eventDispatcher.subscribeTo(this, Channel.onEventsChanged);
        eventDispatcher.subscribeTo(this, Channel.onEventDeleted);

    }

    @Override
    public void newContent(String info) {
        if(event == null) return;
        if(info == null)
            setButtonLogic();
        else {
            if(info.equals(event.getEventName())) {
                stage.close();
            }
        }
    }

    private void setButtonLogic() {
        if(eventsService.findById(event.getId()) == null) {
            this.event = null;
            return;
        }
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
}
