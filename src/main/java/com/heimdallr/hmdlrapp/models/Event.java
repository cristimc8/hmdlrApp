package com.heimdallr.hmdlrapp.models;

import java.sql.Timestamp;
import java.util.List;

public class Event extends BaseEntity<String> {

    private String eventName;
    private int eventCreator;
    private List<Integer> registeredUsers;
    private Timestamp eventDate;

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     *
     * @param id entity id
     */
    public Event(String id) {
        super(id);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(int eventCreator) {
        this.eventCreator = eventCreator;
    }

    public List<Integer> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<Integer> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }
}
