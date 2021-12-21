package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestRepository implements RepoInterface<FriendRequest, Integer> {
    private Connection dbInstance;

    /**
     * Default constructor for the Friend requests' repository class
     * It saves the instantiated db connection
     */
    public FriendRequestRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public FriendRequest findForTwoUsers(int uidOne, int uidTwo) {
        String cmd = "SELECT * FROM friend_requests WHERE " +
                "(sender_id = ? AND receiver_id = ?)" +
                "OR (sender_id = ? AND receiver_id = ?)" +
                "ORDER BY friend_request_id DESC LIMIT 1";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, uidOne);
            ps.setInt(2, uidTwo);
            ps.setInt(3, uidTwo);
            ps.setInt(4, uidOne);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("friend_request_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String status = resultSet.getString("status");
                return new FriendRequest(id, senderId, receiverId, timestamp, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FriendRequest> findAllForUser(int uid) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        String cmd = "SELECT * FROM friend_requests WHERE receiver_id = ? OR sender_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, uid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("friend_request_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String status = resultSet.getString("status");

                friendRequests.add(new FriendRequest(id, senderId, receiverId, timestamp, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendRequests;
    }

    @Override
    public FriendRequest findById(Integer id) {
        String cmd = "SELECT * FROM friend_requests WHERE friend_request_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String status = resultSet.getString("status");
                return new FriendRequest(id, senderId, receiverId, timestamp, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addOne(FriendRequest entity) {
        String cmd = "INSERT INTO friend_requests(friend_request_id, sender_id, receiver_id) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setInt(2, entity.getSenderId());
            preparedStatement.setInt(3, entity.getReceiverId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(Integer id) {
        String cmd = "DELETE FROM friend_requests WHERE friend_request_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FriendRequest> findAll() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        String cmd = "SELECT * FROM friend_requests";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("friend_request_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String status = resultSet.getString("status");

                friendRequests.add(new FriendRequest(id, senderId, receiverId, timestamp, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendRequests;
    }

    @Override
    public void updateOne(FriendRequest original, FriendRequest changed) {
        int originalId = original.getId();
        if (this.findById(originalId) != null) {

            // All good, proceed with update
            String cmd = "UPDATE friend_requests SET status = ? WHERE friend_request_id = ?";
            try {
                PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
                preparedStatement.setString(1, changed.getFriendRequestStatusAsString());
                preparedStatement.setInt(2, original.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer getNextAvailableId() {
        String cmd = "SELECT friend_request_id FROM friend_requests ORDER BY friend_request_id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("friend_request_id") + 1;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
