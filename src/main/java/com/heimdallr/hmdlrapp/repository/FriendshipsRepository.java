package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipsRepository implements RepoInterface<Friendship, Integer> {

    private Connection dbInstance;

    /**
     * Default constructor for the Friendships' repository class
     * It saves the instantiated db connection
     */
    public FriendshipsRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public Friendship findForTwoUsers(int uidOne, int uidTwo) {
        String cmd = "SELECT * FROM friendships WHERE " +
                "(user_one_id = ? AND user_two_id = ?) OR " +
                "(user_one_id = ? AND user_two_id = ?)";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, uidOne);
            ps.setInt(2, uidTwo);
            ps.setInt(3, uidTwo);
            ps.setInt(4, uidOne);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int friendshipId = resultSet.getInt("friendship_id");
                int userOneId = resultSet.getInt("user_one_id");
                int userTwoId = resultSet.getInt("user_two_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                return new Friendship(friendshipId, userOneId, userTwoId, timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a friendship in which one of the friends is the provided UID.
     *
     * @param userId User id we want to search in friendships.
     * @return
     */
    public List<Friendship> findAllWithUser(int userId) {
        List<Friendship> friendships = new ArrayList<>();
        String cmd = "SELECT * FROM friendships WHERE user_one_id = ? OR user_two_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int friendshipId = resultSet.getInt("friendship_id");
                int userOneId = resultSet.getInt("user_one_id");
                int userTwoId = resultSet.getInt("user_two_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                friendships.add(new Friendship(friendshipId, userOneId, userTwoId, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship findById(Integer id) {
        String cmd = "SELECT * FROM friendships WHERE friendship_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int userOneId = resultSet.getInt("user_one_id");
                int userTwoId = resultSet.getInt("user_two_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                return new Friendship(id, userOneId, userTwoId, timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addOne(Friendship entity) {
        String cmd = "INSERT INTO friendships(friendship_id, user_one_id, user_two_id) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setInt(2, entity.getUserOne());
            preparedStatement.setInt(3, entity.getUserTwo());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(Integer id) {
        String cmd = "DELETE FROM friendships WHERE friendship_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String cmd = "SELECT * FROM friendships";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("friendship_id");
                int userOneId = resultSet.getInt("user_one_id");
                int userTwoId = resultSet.getInt("user_two_id");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                friendships.add(new Friendship(id, userOneId, userTwoId, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendships;
    }

    @Override
    public void updateOne(Friendship original, Friendship changed) throws ValueExistsException {
        // we don't update friendships
    }

    @Override
    public Integer getNextAvailableId() {
        String cmd = "SELECT friendship_id FROM friendships ORDER BY friendship_id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("friendship_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
