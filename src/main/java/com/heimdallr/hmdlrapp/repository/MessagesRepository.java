package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.repository.pagination.Page;
import com.heimdallr.hmdlrapp.repository.pagination.Pageable;
import com.heimdallr.hmdlrapp.repository.pagination.Paginator;
import com.heimdallr.hmdlrapp.repository.pagination.PagingRepository;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesRepository implements PagingRepository<Message, Integer> {
    private Connection dbInstance;

    public MessagesRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public List<Message> findAllForUsersForRange(int uidOne, int uidTwo, Timestamp t1, Timestamp t2) {
        List<Message> messages = new ArrayList<>();
        String cmd = "SELECT * FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) " +
                "AND timestamp >= ? AND timestamp <= ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uidOne);
            preparedStatement.setInt(2, uidTwo);
            preparedStatement.setInt(3, uidTwo);
            preparedStatement.setInt(4, uidOne);
            preparedStatement.setTimestamp(5, t1);
            preparedStatement.setTimestamp(6, t2);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
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

    public int countForRangeForUser(Timestamp t1, Timestamp t2, int uid, List<String> userGroups) {
        int number = 0;
        String cmd = "SELECT COUNT(*) AS numeroMesagios FROM messages " +
                "WHERE sender_id = ? OR receiver_id = ? " +
                "AND timestamp >= ? AND timestamp <= ? ";

        if (!userGroups.isEmpty()) {
            cmd = String.format(
                    "SELECT COUNT(*) AS numeroMesagios FROM messages " +
                            "WHERE sender_id = ? OR receiver_id = ? " +
                            "OR group_id IN (%s) " +
                            "AND timestamp >= ? AND timestamp <= ? ",
                    userGroups.stream()
                            .map(v -> "?").collect(Collectors.joining(", ")));
        }

        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, uid);
            if (!userGroups.isEmpty()) {
                for (int i = 0; i < userGroups.size(); i++) {
                    preparedStatement.setString(i + 3, userGroups.get(i));
                }
                preparedStatement.setTimestamp(3 + userGroups.size(), t1);
                preparedStatement.setTimestamp(4 + userGroups.size(), t2);
            }
            else {
                preparedStatement.setTimestamp(3, t1);
                preparedStatement.setTimestamp(4, t2);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                number = resultSet.getInt("numeroMesagios");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return number;
    }

    public List<Message> getAllBetweenUsers(int uidOne, int uidTwo) {
        List<Message> messages = new ArrayList<>();
        String cmd = "SELECT * FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?)" +
                "ORDER BY timestamp DESC";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uidOne);
            preparedStatement.setInt(2, uidTwo);
            preparedStatement.setInt(3, uidTwo);
            preparedStatement.setInt(4, uidOne);
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

    public List<Message> getAllForGroup(String gid) {
        List<Message> messages = new ArrayList<>();
        String cmd = "SELECT * FROM messages " +
                "WHERE group_id = ?" +
                "ORDER BY timestamp DESC";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, gid);
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

    /**
     * Deletes all messages between two users.
     *
     * @param uidOne User id one
     * @param uidTwo User id two
     */
    public void deleteAllBetweenTwoUsers(int uidOne, int uidTwo) {
        String cmd = "DELETE FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?)" +
                "OR (sender_id = ? AND receiver_id = ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uidOne);
            preparedStatement.setInt(2, uidTwo);
            preparedStatement.setInt(3, uidTwo);
            preparedStatement.setInt(4, uidOne);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getAllPreviewsForUser(int uid, List<String> userGroups) {
        List<Message> messages = new ArrayList<>();
        List<Integer> candidates = new ArrayList<>();
        List<String> groupIdsCandidates = new ArrayList<>();

        String cmd = "SELECT * FROM messages " +
                "WHERE sender_id = ? OR receiver_id = ? " +
                "ORDER BY message_id DESC";

        if (!userGroups.isEmpty()) {
            cmd = String.format(
                    "SELECT * FROM messages " +
                            "WHERE sender_id = ? OR receiver_id = ? " +
                            "OR group_id IN (%s) " +
                            "ORDER BY message_id DESC",
                    userGroups.stream()
                            .map(v -> "?").collect(Collectors.joining(", ")));
        }


//        System.out.println(cmd);
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, uid);
            if (!userGroups.isEmpty()) {
                for (int i = 0; i < userGroups.size(); i++) {
                    preparedStatement.setString(i + 3, userGroups.get(i));
                }
            }
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

                int candidateId = senderId != uid ? senderId : receiverId;

                // We only want the last message with each user
                // But the system is special case
                if (groupId == null) {
                    // its a convo
                    if (!candidates.contains(candidateId)) {
                        // If from this convo we don't already have a preview
                        // with this other person
                        messages.add(new Message(messageId, senderId, receiverId, null, replyTo, messageBody, timestamp));
                        candidates.add(candidateId);
                    }
                } else {
                    // its a gc
                    if (!groupIdsCandidates.contains(groupId)) {
                        messages.add(new Message(messageId, senderId, receiverId, groupId, replyTo, messageBody, timestamp));
                        groupIdsCandidates.add(groupId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public List<Message> getAllMessagesForUser(int uid) {
        List<Message> messages = new ArrayList<>();
        String cmd = "SELECT * FROM messages WHERE sender_id = ? OR receiver_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, uid);
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
        if (entity.getReceiverId() == 0)
            cmd = "INSERT INTO messages(message_id, sender_id, group_id, reply_to, message_body) VALUES(?, ?, ?, ?, ?)";
        else
            cmd = "INSERT INTO messages(message_id, sender_id, receiver_id, reply_to, message_body) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setInt(2, entity.getSenderId());
            if (entity.getReceiverId() == 0) {
                preparedStatement.setString(3, entity.getGroupId());
            } else preparedStatement.setInt(3, entity.getReceiverId());
            preparedStatement.setInt(4, entity.getReplyTo());
            preparedStatement.setString(5, entity.getMessageBody());
            preparedStatement.executeUpdate();

            notifySubs();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies subscribers that a new message arrived(or was sent)
     */
    private void notifySubs() {
        try {
            ((EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class))
                    .dispatch(Channel.onNewMessage, null);
        } catch (ServiceNotRegisteredException e) {
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

    @Override
    public Page<Message> findAll(Pageable pageable) {
        Paginator<Message> paginator = new Paginator<Message>(pageable, this.findAll());
        return paginator.paginate();
    }

    public Page<Message> getAllBetweenUsers(Pageable pageable, int uidOne, int uidTwo) {
        Paginator<Message> paginator = new Paginator<>(pageable, this.getAllBetweenUsers(uidOne, uidTwo));
        return paginator.paginate();
    }

    public Page<Message> getAllForGroup(Pageable pageable, String gid) {
        Paginator<Message> paginator = new Paginator<>(pageable, this.getAllForGroup(gid));
        return paginator.paginate();
    }
}
