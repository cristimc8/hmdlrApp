package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.utils.RandomString;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupChatsRepository implements RepoInterface<GroupChat, String> {

    private Connection dbInstance;

    public GroupChatsRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GroupChat findById(String id) {
        String cmd = "SELECT * FROM group_chats WHERE group_chat_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {

                int creatorId = resultSet.getInt("creator_id");
                String alias = resultSet.getString("alias");
                String participants = resultSet.getString("participants");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                return new GroupChat(id, alias, participants, timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GroupChat> findAllForUser(int uid) {
        List<GroupChat> groupChats = new ArrayList<>();
        String cmd = "SELECT * FROM group_chats WHERE participants LIKE '%' || ? || '%'";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, "<>" + String.valueOf(uid) + "<>");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String gcId = resultSet.getString("group_chat_id");
                int creatorId = resultSet.getInt("creator_id");
                String alias = resultSet.getString("alias");
                String participants = resultSet.getString("participants");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                groupChats.add(new GroupChat(gcId, alias, participants, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupChats;
    }

    @Override
    public void addOne(GroupChat entity) {
        String cmd = "INSERT INTO group_chats(group_chat_id, creator_id, alias, participants) VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, entity.getId());
            preparedStatement.setInt(2, entity.getCreator());
            preparedStatement.setString(3, entity.getAlias());
            preparedStatement.setString(4, entity.getParticipantsAsString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(String id) {
        String cmd = "DELETE FROM group_chats WHERE group_chat_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<GroupChat> findAll() {
        List<GroupChat> groupChats = new ArrayList<>();
        String cmd = "SELECT * FROM group_chats";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("group_chat_id");
                int creatorId = resultSet.getInt("creator_id");
                String alias = resultSet.getString("alias");
                String participants = resultSet.getString("participants");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                groupChats.add(new GroupChat(id, alias, participants, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupChats;
    }

    /**
     * We can update the GC alias and the list of participants.
     */
    @Override
    public void updateOne(GroupChat original, GroupChat changed) {
        String originalId = original.getId();
        if (this.findById(originalId) != null) {

            // All good, proceed with update
            String cmd = "UPDATE group_chats SET alias = ?, participants = ? WHERE group_chat_id = ?";
            try {
                PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
                preparedStatement.setString(1, changed.getAlias());
                preparedStatement.setString(2, changed.getParticipantsAsString());
                preparedStatement.setString(3, original.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateNextId() {
        String generatedId;
        RandomString randomString = new RandomString(12);
        generatedId = randomString.nextString();
        return generatedId;
    }

    @Override
    public String getNextAvailableId() {
        String id = generateNextId();
        while(this.findById(id) != null) {
            id = generateNextId();
        }
        return id;
    }
}
