package com.heimdallr.hmdlrapp.config;

import java.sql.Connection;

/**
 * Class initializing all services config classes in a single convenient place,
 * so we don't initialize them one by one in main
 */
public class ConfigHelper {
    /**
     * Method that takes all config classes we have and executes them
     * one by one.
     */
    public void runAll() {
        ServicesConfig servicesConfig = new ServicesConfig();

        try {
            servicesConfig.initServices();
        }
        // Catching all config exceptions here
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
