package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.repository.EventsRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

@Service(AssociatedRepo = EventsRepository.class)
public class EventsService {
    EventsRepository eventsRepository;

    private EventsService(Object eventsRepo) {
        this.eventsRepository = (EventsRepository) eventsRepo;
    }
}
