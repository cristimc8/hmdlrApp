package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.config.DBConfig;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;
import com.heimdallr.hmdlrapp.models.Event;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;

import java.sql.*;
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

                return new Event(id, creatorId, eventName, registered, eventDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    }

    @Override
    public List<Event> findAll() {
        return null;
    }

    @Override
    public void updateOne(Event original, Event changed) throws ValueExistsException {

    }

    @Override
    public String getNextAvailableId() {
        return null;
    }
}
