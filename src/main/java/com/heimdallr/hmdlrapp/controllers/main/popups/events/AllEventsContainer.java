package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.controllers.CustomController;
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;


public class AllEventsContainer extends AnchorPane implements Subscriber {

    EventsService eventsService;
    EventDispatcher eventDispatcher;
    UserService userService;

    BorderPane parent;
    boolean onlyUpcoming;

    @FXML
    Label popupTitle;

    @FXML
    AnchorPane allEventsContainer;

    @FXML
    ImageView closeEventsPopupButton;

    @FXML
    VBox injectableVboxEvents;

    public AllEventsContainer(BorderPane parent, boolean onlyUpcoming) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/listEventsContainer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.parent = parent;
        this.onlyUpcoming = onlyUpcoming;

        try {
            fxmlLoader.load();

            try {
                userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
                eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
                this.eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }

            eventDispatcher.subscribeTo(this, Channel.onEventDeleted);
            eventDispatcher.subscribeTo(this, Channel.onEventSuccessfullyCreated);
            eventDispatcher.subscribeTo(this, Channel.onEventsChanged);
            // Populate with all events
            this.populateWithEvents();

            this.setListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateWithEvents() {
        if(this.onlyUpcoming) {
            this.popupTitle.setText("Upcoming events");
            this.populateWithOnlyUpcomingEvents();
        }
        else {
            this.popupTitle.setText("All events");
            this.populateWithAllEvents();
        }
    }

    private void populateWithAllEvents() {
        injectableVboxEvents.getChildren().clear();
        List<Event> events = eventsService.findAll();
        for (Event event :
                events) {
            EventRowController eventRowController = new EventRowController(event);
            injectableVboxEvents.getChildren().add(eventRowController);
        }
    }

    private void populateWithOnlyUpcomingEvents() {
        injectableVboxEvents.getChildren().clear();
        List<Event> events = eventsService.findUpcomingForUser(userService.getCurrentUser());
        for (Event event :
                events) {
            EventRowController eventRowController = new EventRowController(event);
            injectableVboxEvents.getChildren().add(eventRowController);
        }
    }

    @Override
    public void newContent(String info) {
        populateWithEvents();
    }

    private void setListeners() {
        closeEventsPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            allEventsContainer.setVisible(false);
            parent.setOpacity(1);
        });
    }
}
