package com.heimdallr.hmdlrapp.models.dtos;

import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;

public class UserFriendRequestDTO {
    private User senderUser;
    private FriendRequest friendRequest;

    /**
     * DTO holding the friend request and the user that sent it.
     */
    public UserFriendRequestDTO(FriendRequest friendRequest, User senderUser) {
        this.senderUser = senderUser;
        this.friendRequest = friendRequest;
    }

    public User getSenderUser() {
        return this.senderUser;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }
}
