package com.heimdallr.hmdlrapp.models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

public class Friendship extends BaseEntity<Integer> {
    private int userOne;
    private int userTwo;
    private Timestamp timestamp;

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     *
     * @param id entity id
     */
    public Friendship(Integer id, int userOne, int userTwo, Timestamp timestamp) {
        super(id);
        setUserOne(userOne);
        setUserTwo(userTwo);
        setTimestamp(timestamp);
    }

    public Friendship(Integer id, int userOne, int userTwo) {
        super(id);
        setUserOne(userOne);
        setUserTwo(userTwo);
    }

    public int getUserOne() {
        return userOne;
    }

    public void setUserOne(int userOne) {
        this.userOne = userOne;
    }

    public int getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(int userTwo) {
        this.userTwo = userTwo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return userOne == that.userOne && userTwo == that.userTwo && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userOne, userTwo, timestamp);
    }
}
