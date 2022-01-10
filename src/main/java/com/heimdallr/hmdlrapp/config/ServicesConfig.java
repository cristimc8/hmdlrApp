package com.heimdallr.hmdlrapp.config;

import com.heimdallr.hmdlrapp.exceptions.ServiceInitException;
import com.heimdallr.hmdlrapp.services.*;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.services.pubSub.HmdlrSubscribers;

import java.util.List;

public class ServicesConfig {
    HmdlrDI hmdlrDI = HmdlrDI.getContainer();

    /**
     * method that instantiates the application's services(logical controllers)
     *
     * @throws ServiceInitException Throws if any error occurs while instantiating
     */
    public void initServices() throws ServiceInitException {
        List<Class<?>> servicesToInit = List.of(
                DBConfig.class,
                HmdlrSubscribers.class,
                EventDispatcher.class,
                MessagesService.class,
                UserService.class,
                FriendshipsService.class,
                FriendRequestService.class,
                GroupChatsService.class,
                ReportsService.class,
                EventsService.class
        );
        hmdlrDI.initialize(servicesToInit);
    }
}
