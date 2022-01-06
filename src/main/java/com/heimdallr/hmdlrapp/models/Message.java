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
     * Message from a user to a group or person with timestamp
     */
    public Message(Integer id, int senderId, int receiverId, String groupId, int replyTo, String messageBody, Timestamp timestamp) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
        setGroupId(groupId);
        setReplyTo(replyTo);
        setMessageBody(messageBody);
        setTimestamp(timestamp);
    }

    /**
     * Message from a user to a group or user without timestamp
     */
    public Message(Integer id, int senderId, int receiverId, String groupId, int replyTo, String messageBody) {
        super(id);
        setSenderId(senderId);
        setReceiverId(receiverId);
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

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", groupId='" + groupId + '\'' +
                ", replyTo=" + replyTo +
                ", messageBody='" + messageBody + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
