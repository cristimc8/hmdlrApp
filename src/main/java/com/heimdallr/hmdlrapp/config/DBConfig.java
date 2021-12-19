package com.heimdallr.hmdlrapp.config;

import com.heimdallr.hmdlrapp.services.DI.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Service singleton class for holding all DB config and instance variables.
 */
@Service
public class DBConfig {
    private Connection dbInstance = null;

    /**
     * Default private constructor
     */
    private DBConfig() {
        try {
            dbInstance = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "weasel", "rockweasel");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that returns the usable and initialised DB connection object.
     * @return Connection instance
     */
    public Connection getDbInstance() {
        return dbInstance;
    }
}
