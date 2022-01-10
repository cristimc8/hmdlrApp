package com.heimdallr.hmdlrapp.services.pubSub;

import com.heimdallr.hmdlrapp.HelloApplication;
import com.heimdallr.hmdlrapp.controllers.main.sliderMenu.SliderMenuController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Event dispatcher class
 * Using in Pub/Sub architecture
 * Subscribers subscribe to an event and the eventDispatcher notifies them
 * when a change has occurred.
 * Any class may inject the event dispatcher and use it to publish changes.
 */
@Service
public class EventDispatcher {
    private HmdlrSubscribers hmdlrSubscribers;

    HashMap<Subscriber, List<Channel>> subscriberChannelHashMap;

    private EventDispatcher() {
        this.subscriberChannelHashMap = new HashMap<>();
        try {
            hmdlrSubscribers = (HmdlrSubscribers) HmdlrDI.getContainer().getService(HmdlrSubscribers.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies all the subscribers that a change was detected, and they need
     * to reload data.
     *
     * @param channel Channel that has new content
     * @param info    Info transmitted
     */
    public void dispatch(Channel channel, String info) {
        for(Method method : this.hmdlrSubscribers.getMethodsSubscribedToChannel(channel)) {
            method.setAccessible(true);
            try {
                method.invoke(null, info);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        /*for (Subscriber subscriber : this.subscriberChannelHashMap.keySet()) {
            if (isSubscribedTo(subscriber, channel)) {
                subscriber.newContent(info);
            }
        }*/
    }

    private boolean isSubscribedTo(Subscriber subscriber, Channel channel) {
        return this.subscriberChannelHashMap.get(subscriber).contains(channel);
    }

    public void subscribeTo(Subscriber subscriber, Channel channel) {
        if (subscriberChannelHashMap.containsKey(subscriber)) {
            subscriberChannelHashMap.get(subscriber).add(channel);
        } else {
            List<Channel> channels = new ArrayList<>();
            channels.add(channel);
            this.subscriberChannelHashMap.put(subscriber, channels);
        }
    }

    public void removeSubscriber(Subscriber subscriber) {
        this.subscriberChannelHashMap.remove(subscriber);
    }
}
