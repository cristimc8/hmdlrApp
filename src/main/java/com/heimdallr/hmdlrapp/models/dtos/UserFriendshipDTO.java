package com.heimdallr.hmdlrapp.models.dtos;

import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UserFriendshipDTO {
    private User userOne;
    private User userTwo;
    private Friendship friendship;

    /**
     * DTO holding the friendship and the users that are part of it.
     * @param friendship
     * @param userOne
     * @param userTwo
     */
    public UserFriendshipDTO(Friendship friendship, User userOne, User userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        this.friendship = friendship;
    }

    public User getUserOne() {
        return userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formatDateTime = friendship.getTimestamp().toLocalDateTime().format(formatter);
        return "Friendship between: " +
                "" + userOne.getDisplayUsername() + " and " + userTwo.getDisplayUsername() + "\n" +
                "With friendship id: " + friendship.getId() + "\n" +
                "Started from: " + formatDateTime + "\n" +
                "=================================== \n";
    }
}
