package com.heimdallr.hmdlrapp.models;

import java.util.ArrayList;
import java.util.List;

public class GroupChat extends BaseEntity<String> {
    private int creator;
    private String alias;
    private List<Integer> participants = new ArrayList<>();

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

    public GroupChat(String id, String alias, List<Integer> participants) {
        super(id);
        setAlias(alias);
        setParticipants(participants);
        setCreator(participants.get(0));
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

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
}
