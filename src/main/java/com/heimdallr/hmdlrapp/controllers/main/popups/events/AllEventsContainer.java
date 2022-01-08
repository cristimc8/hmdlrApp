package com.heimdallr.hmdlrapp.controllers.main.popups.events;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.EventsService;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;


public class AllEventsContainer extends AnchorPane implements Subscriber {

    EventsService eventsService;
    EventDispatcher eventDispatcher;

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

            try {
                eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
                this.eventsService = (EventsService) HmdlrDI.getContainer().getService(EventsService.class);
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }

            eventDispatcher.subscribeTo(this, Channel.onEventDeleted);
            eventDispatcher.subscribeTo(this, Channel.onEventSuccessfullyCreated);
            eventDispatcher.subscribeTo(this, Channel.onEventsChanged);
            // Populate with all events
            this.populateWithAllEvents();

            this.setListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateWithAllEvents() {
        injectableVboxEvents.getChildren().clear();
        List<Event> events = eventsService.findAll();
        System.out.println(events.size());
        for (Event event :
                events) {
            EventRowController eventRowController = new EventRowController(event);
            injectableVboxEvents.getChildren().add(eventRowController);
        }
    }

    @Override
    public void newContent(String info) {
        populateWithAllEvents();
    }

    private void setListeners() {
        closeEventsPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            allEventsContainer.setVisible(false);
            parent.setOpacity(1);
        });
    }
}
