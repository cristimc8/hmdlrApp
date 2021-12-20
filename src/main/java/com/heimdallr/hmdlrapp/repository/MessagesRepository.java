package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessagesRepository implements RepoInterface<Message, Integer> {
    private Connection dbInstance;

    public MessagesRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message findById(Integer id) {
        String cmd = "SELECT * FROM messages WHERE message_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {

                int senderId = resultSet.getInt("sender_id");
                // can be null! for ints = 0
                int receiverId = resultSet.getInt("receiver_id");
                // can be null! for strings = null
                String groupId = resultSet.getString("group_id");
                int replyTo = resultSet.getInt("reply_to");
                String messageBody = resultSet.getString("message_body");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                return new Message(id, senderId, receiverId, groupId, replyTo, messageBody, timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addOne(Message entity) {
        String cmd;
        // it's either to a user or to a group
        if(entity.getReceiverId() == 0)
            cmd = "INSERT INTO messages(message_id, sender_id, group_id, reply_to, message_body) VALUES(?, ?, ?, ?, ?)";
        else cmd = "INSERT INTO messages(message_id, sender_id, receiver_id, reply_to, message_body) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setInt(2, entity.getSenderId());
            if(entity.getReceiverId() == 0) {
                preparedStatement.setString(3, entity.getGroupId());
            }
            else preparedStatement.setInt(3, entity.getReceiverId());
            preparedStatement.setInt(4, entity.getReplyTo());
            preparedStatement.setString(5, entity.getMessageBody());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(Integer id) {
        String cmd = "DELETE FROM messages WHERE message_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String cmd = "SELECT * FROM messages";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                int senderId = resultSet.getInt("sender_id");
                // can be null! for ints = 0
                int receiverId = resultSet.getInt("receiver_id");
                // can be null! for strings = null
                String groupId = resultSet.getString("group_id");
                int replyTo = resultSet.getInt("reply_to");
                String messageBody = resultSet.getString("message_body");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                messages.add(new Message(messageId, senderId, receiverId, groupId, replyTo, messageBody, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public void updateOne(Message original, Message changed) throws ValueExistsException {
        // no modifications to messages allowed, sorry!
    }

    @Override
    public Integer getNextAvailableId() {
        String cmd = "SELECT message_id FROM messages ORDER BY message_id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("message_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
