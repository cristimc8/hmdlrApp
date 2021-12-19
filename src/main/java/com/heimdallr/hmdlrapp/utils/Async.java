package com.heimdallr.hmdlrapp.utils;

import java.util.Timer;
import java.util.TimerTask;

import static javafx.application.Platform.runLater;

/**
 * Class holding static methods that performs various operations either on other threads, or
 * in a non-blocking method.
 */
public class Async {
    /**
     * Method that creates a new thread in order to simulate the
     * javascript setTimeout function.
     * @param runnable Lambda function to execute after given delay.
     * @param delay ms delay
     */
    public static void setTimeout(Runnable runnable, int delay){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runLater(new Runnable() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer.purge();
                        runnable.run();
                    }
                });
            }
        }, delay, 2000);
    }
}
