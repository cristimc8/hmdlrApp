package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Users repository class
 * Holding and querying persistent data related to the user.
 */
public class UsersRepository implements RepoInterface<User, Integer> {
    private Connection dbInstance;

    /**
     * Default constructor for the Users' repository class
     * It saves the instantiated db connection
     */
    public UsersRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findById(Integer id) {
        String cmd = "SELECT * FROM users WHERE id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String hash = resultSet.getString("hash");
                return new User(id, email, username, firstName, lastName, hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addOne(User entity) {
        String cmd = "INSERT INTO users(id, first_name, last_name, email, hash, username) VALUES(?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setString(2, entity.getFirstName());
            preparedStatement.setString(3, entity.getLastName());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getHash());
            preparedStatement.setString(6, entity.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(Integer id) {
        String cmd = "DELETE FROM users WHERE id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String cmd = "SELECT * FROM users";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String hash = resultSet.getString("hash");

                users.add(new User(id, email, username, firstName, lastName, hash));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Find a user that has a certain email
     *
     * @param email searched user's email
     * @return User if found, null else
     */
    public User findByEmail(String email) {
        String cmd = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String hash = resultSet.getString("hash");

                return new User(id, email, firstName, lastName, username, hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find a user that has a certain username
     *
     * @param username searched user's username
     * @return User if found, null else
     */
    public User findByUsername(String username) {
        String cmd = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String hash = resultSet.getString("hash");

                return new User(id, email, username, firstName, lastName, hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param original Original user we want to update
     * @param changed  User with updated properties we want to save
     */
    @Override
    public void updateOne(User original, User changed) throws ValueExistsException {
        int originalId = original.getId();
        if (this.findById(originalId) != null) {
            // we found a user with that ID

            //We check so no email or username already exists
            if (!Objects.equals(changed.getEmail(), original.getEmail())
                    && this.findByEmail(changed.getEmail()) != null) {
                throw new ValueExistsException("Email " + changed.getEmail() + " already exists!");
            }

            if (!Objects.equals(changed.getUsername(), original.getUsername())
                    && this.findByUsername(changed.getUsername()) != null) {
                throw new ValueExistsException("Username " + changed.getUsername() + " already exists!");
            }

            // All good, proceed with update
            String cmd = "UPDATE users SET firstName = ?, lastName = ?, email = ?, hash = ?, username = ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
                preparedStatement.setString(1, changed.getFirstName());
                preparedStatement.setString(2, changed.getLastName());
                preparedStatement.setString(3, changed.getEmail());
                preparedStatement.setString(4, changed.getHash());
                preparedStatement.setString(5, changed.getUsername());
                preparedStatement.setInt(6, original.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer getNextAvailableId() {
        String cmd = "SELECT id FROM users ORDER BY id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("id") + 1;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
