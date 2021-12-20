package com.heimdallr.hmdlrapp.models;

import java.sql.Timestamp;

public class Message extends BaseEntity<Integer> {
    private int senderId;
    private int receiverId;
    private String groupId;
    private int replyTo;
    private String messageBody;
    private Timestamp timestamp;

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     *
     * Message between 2 users with timestamp
     * @param id entity id
     */
    public Message(Integer id, int senderId, int receiverId, int replyTo, String messageBody, Timestamp timestamp) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setReplyTo(replyTo);
        setMessageBody(messageBody);
        setTimestamp(timestamp);
    }

    /**
     * Message between 2 users without timestamp
     * If it's not replying to any message just pass the parameter as null
     */
    public Message(Integer id, int senderId, int receiverId, int replyTo, String messageBody) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setReplyTo(replyTo);
        setMessageBody(messageBody);
    }

    /**
     * Message from a user to a group with timestamp
     */
    public Message(Integer id, int senderId, String groupId, int replyTo, String messageBody, Timestamp timestamp) {
        super(id);
        setSenderId(senderId);
        setGroupId(groupId);
        setReplyTo(replyTo);
        setMessageBody(messageBody);
        setTimestamp(timestamp);
    }

    /**
     * Message from a user to a group without timestamp
     */
    public Message(Integer id, int senderId, String groupId, int replyTo, String messageBody) {
        super(id);
        setSenderId(senderId);
        setGroupId(groupId);
        setReplyTo(replyTo);
        setMessageBody(messageBody);
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
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

    public int getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
