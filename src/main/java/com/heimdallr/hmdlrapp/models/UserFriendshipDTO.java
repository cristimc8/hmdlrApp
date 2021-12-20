package com.heimdallr.hmdlrapp.models;

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
}
