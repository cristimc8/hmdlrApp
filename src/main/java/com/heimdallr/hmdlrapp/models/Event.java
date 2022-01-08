package com.heimdallr.hmdlrapp.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Event extends BaseEntity<String> {

    private String eventName;
    private int eventCreator;
    private List<Integer> registeredUsers = new ArrayList<>();
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

    public Event(String id, String eventName, String registeredUsers, Timestamp eventDate) {
        super(id);
        setEventName(eventName);
        setRegisteredUsers(registeredUsers);
        setEventDate(eventDate);
        setEventCreator(this.registeredUsers.get(0));
    }

    public Event(String id, String eventName, List<Integer> registeredUsers, Timestamp eventDate) {
        super(id);
        setEventName(eventName);
        setRegisteredUsers(registeredUsers);
        setEventDate(eventDate);
        setEventCreator(this.registeredUsers.get(0));
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

    /**
     * Returns the list of participants as a ',' delimited String.
     *
     * @return list of participants ids delimited with , separator
     */
    public String getRegisteredUsersAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer elem : this.getRegisteredUsers()) {
            stringBuilder.append(elem).append(",");
        }

        return "," + stringBuilder.toString();
    }

    public void setRegisteredUsers(String participants) {
        participants = participants.substring(1);
        participants = participants.substring(0, participants.length() - 1);
        this.registeredUsers = Stream.of(participants.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
