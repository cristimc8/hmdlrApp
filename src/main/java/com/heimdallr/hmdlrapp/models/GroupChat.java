package com.heimdallr.hmdlrapp.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GroupChat entity
 * Holds info about a GC such as its creator, the list of participants, its alias
 * and the date it was created.
 */
public class GroupChat extends BaseEntity<String> {
    private int creator;
    private String alias;
    private List<Integer> participants = new ArrayList<>();
    private Timestamp timestamp;

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     *
     * @param id entity id
     */
    public GroupChat(String id, String alias, int creatorId) {
        super(id);
        setAlias(alias);
        participants.add(creatorId);
        setCreator(creatorId);
    }

    /**
     * Constructor with only creator and timestamp
     */
    public GroupChat(String id, String alias, int creatorId, Timestamp timestamp) {
        super(id);
        setAlias(alias);
        participants.add(creatorId);
        setCreator(creatorId);
        setTimestamp(timestamp);
    }

    /**
     * Constructor with participants list
     */
    public GroupChat(String id, String alias, String participants) {
        super(id);
        setAlias(alias);
        setParticipants(participants);
        setCreator(this.participants.get(0));
    }

    /**
     * Constructor with participants and timestamp
     */
    public GroupChat(String id, String alias, String participants, Timestamp timestamp) {
        super(id);
        setAlias(alias);
        setParticipants(participants);
        setCreator(this.participants.get(0));
        setTimestamp(timestamp);
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    /**
     * Returns the list of participants as a '<>' delimited String.
     * @return list of participants ids delimited with <> separator
     */
    public String getParticipantsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer elem : this.getParticipants()) {
            stringBuilder.append(elem).append("<>");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    public void setParticipants(String participants) {
        this.participants = Stream.of(participants.split("<>"))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

    /**
     * Gets the id of the user who created the GC
     *
     * @return The creator's id
     */
    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
