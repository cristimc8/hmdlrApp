package com.heimdallr.hmdlrapp.models;

import com.heimdallr.hmdlrapp.utils.Constants;

import java.sql.Timestamp;

public class FriendRequest extends BaseEntity<Integer> {
    private int senderId;
    private int receiverId;
    private Timestamp timestamp;
    private Constants.FriendshipRequestStatus friendshipRequestStatus;
    private boolean accepted;

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     *
     * @param id entity id
     */
    public FriendRequest(Integer id, int senderId, int receiverId,
                         Timestamp timestamp, String friendRequestStatus, boolean accepted) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setTimestamp(timestamp);
        setAccepted(accepted);
        switch (friendRequestStatus) {
            case "pending" -> setFriendshipRequestStatus(Constants.FriendshipRequestStatus.PENDING);
            case "approved" -> setFriendshipRequestStatus(Constants.FriendshipRequestStatus.APPROVED);
            case "rejected" -> setFriendshipRequestStatus(Constants.FriendshipRequestStatus.REJECTED);
            case "canceled" -> setFriendshipRequestStatus(Constants.FriendshipRequestStatus.CANCELED);
        }
    }

    public FriendRequest(Integer id, int senderId, int receiverId,
                         Timestamp timestamp, Constants.FriendshipRequestStatus friendRequestStatus) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setTimestamp(timestamp);
        setFriendshipRequestStatus(friendRequestStatus);
    }

    public FriendRequest(Integer id, int senderId, int receiverId) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setAccepted(false);
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Constants.FriendshipRequestStatus getFriendshipRequestStatus() {
        return friendshipRequestStatus;
    }

    public String getFriendRequestStatusAsString() {
        String status = "pending";
        switch (friendshipRequestStatus) {
            case APPROVED -> {
                status = "approved";
            }
            case REJECTED -> {
                status = "rejected";
            }
            case PENDING -> {
                status = "pending";
            }
            case CANCELED -> {
                status = "canceled";
            }
        }
        return status;
    }

    public void setFriendshipRequestStatus(Constants.FriendshipRequestStatus friendshipRequestStatus) {
        this.friendshipRequestStatus = friendshipRequestStatus;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
