package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.EventsRepository;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service(AssociatedRepo = EventsRepository.class)
public class EventsService {
    EventsRepository eventsRepository;
    EventDispatcher eventDispatcher;
    private int acceptableNumberOfDaysForNotification = 30;

    private EventsService(Object eventsRepo) {
        this.eventsRepository = (EventsRepository) eventsRepo;
        try {
            this.eventDispatcher = (EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Event event) {
        this.eventsRepository.deleteOne(event.getId());
        eventDispatcher.dispatch(Channel.onEventDeleted, event.getEventName());
    }

    public void subscribeToEvent(User user, Event event) {
        List<Integer> registered = event.getRegisteredUsers();
        registered.add(user.getId());
        event.setRegisteredUsers(registered);
        try {
            eventsRepository.updateOne(event, event);
        } catch (ValueExistsException e) {
            e.printStackTrace();
        }
        eventDispatcher.dispatch(Channel.onEventsChanged, null);
    }

    public void unsubscribeFromEvent(User user, Event event) {
        List<Integer> registered = event.getRegisteredUsers();
        registered.remove(user.getId());
        event.setRegisteredUsers(registered);
        try {
            eventsRepository.updateOne(event, event);
        } catch (ValueExistsException e) {
            e.printStackTrace();
        }
        eventDispatcher.dispatch(Channel.onEventsChanged, null);
    }

    public boolean isUserSubscribedToEvent(User user, Event event) {
        return isUserSubscribedToEvent(user.getId(), event.getId());
    }

    public boolean isUserSubscribedToEvent(int uid, String eid) {
        return findById(eid).getRegisteredUsers().contains(uid);
    }

    public Event findByName(String name) {
        return eventsRepository.findByName(name);
    }

    public void createEvent(String eventName, int creatorId, Timestamp eventDate) {
        Event event = new Event(
                eventsRepository.getNextAvailableId(),
                eventName,
                List.of(creatorId),
                eventDate
        );
        eventsRepository.addOne(event);
        eventDispatcher.dispatch(Channel.onEventsChanged, null);
    }

    public Event findById(String id) {
        return this.eventsRepository.findById(id);
    }

    public List<Event> findAllForUser(User user) {
        return this.eventsRepository.findAll().stream().filter(e -> {
                    return e.getRegisteredUsers().contains(user.getId());
                })
                .collect(Collectors.toList());
    }

    public List<Event> findAll() {
        return this.eventsRepository.findAll();
    }

    public List<Event> findUpcomingForUser(User user) {
        return this.findUpcomingForUser(user.getId());
    }

    public List<Event> findUpcomingForUser(int uid) {
        Timestamp current = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        return eventsRepository.findUpcomingForUserId(uid, current, acceptableNumberOfDaysForNotification);
    }
}
