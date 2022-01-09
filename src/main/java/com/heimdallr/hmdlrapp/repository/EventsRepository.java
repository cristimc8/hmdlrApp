package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.utils.RandomString;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsRepository implements RepoInterface<Event, String> {

    private Connection dbInstance;

    public EventsRepository() {
        try {
            this.dbInstance = ((DBConfig) HmdlrDI.getContainer().getService(DBConfig.class)).getDbInstance();
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public Event findByName(String name) {
        String cmd = "SELECT * FROM events WHERE event_name = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setString(1, name);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {

                String id = resultSet.getString("event_id");
                int creatorId = resultSet.getInt("event_creator");
                String eventName = resultSet.getString("event_name");
                String registered = resultSet.getString("registered");
                Timestamp eventDate = resultSet.getTimestamp("event_date");

                return new Event(id, eventName, registered, eventDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event findById(String id) {
        String cmd = "SELECT * FROM events WHERE event_id = ?";
        try {
            PreparedStatement ps = dbInstance.prepareStatement(cmd);
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {

                int creatorId = resultSet.getInt("event_creator");
                String eventName = resultSet.getString("event_name");
                String registered = resultSet.getString("registered");
                Timestamp eventDate = resultSet.getTimestamp("event_date");

                return new Event(id, eventName, registered, eventDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> findUpcomingForUserId(int uid, Timestamp currentDate, int acceptableNumberOfDaysForNotification) {
        List<Event> events = new ArrayList<>();
        String cmd = "SELECT * FROM events WHERE EXTRACT(DAY FROM events.event_date- ?) < ?" +
                " AND events.registered LIKE '%' || ? || '%'";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setTimestamp(1, currentDate);
            preparedStatement.setInt(2, acceptableNumberOfDaysForNotification);
            preparedStatement.setString(3, "," + String.valueOf(uid) + ",");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("event_id");
                int creatorId = resultSet.getInt("event_creator");
                String eventName = resultSet.getString("event_name");
                String registered = resultSet.getString("registered");
                Timestamp eventDate = resultSet.getTimestamp("event_date");

                events.add(new Event(id, eventName, registered, eventDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public void addOne(Event entity) {
        String cmd = "INSERT INTO events(event_id, event_creator, event_name, registered, event_date) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, entity.getId());
            preparedStatement.setInt(2, entity.getEventCreator());
            preparedStatement.setString(3, entity.getEventName());
            preparedStatement.setString(4, entity.getRegisteredUsersAsString());
            preparedStatement.setTimestamp(5, entity.getEventDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOne(String id) {
        String cmd = "DELETE FROM events WHERE event_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String cmd = "SELECT * FROM events";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("event_id");
                int creatorId = resultSet.getInt("event_creator");
                String eventName = resultSet.getString("event_name");
                String registered = resultSet.getString("registered");
                Timestamp eventDate = resultSet.getTimestamp("event_date");

                events.add(new Event(id, eventName, registered, eventDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public void updateOne(Event original, Event changed) throws ValueExistsException {
        String cmd = "UPDATE events SET registered = ? WHERE event_id = ?";
        try {
            PreparedStatement preparedStatement = dbInstance.prepareStatement(cmd);
            preparedStatement.setString(1, original.getRegisteredUsersAsString());
            preparedStatement.setString(2, original.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        while (this.findById(id) != null) {
            id = generateNextId();
        }
        return id;
    }
}
