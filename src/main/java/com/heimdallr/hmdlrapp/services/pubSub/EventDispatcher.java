package com.heimdallr.hmdlrapp.services.pubSub;

import com.heimdallr.hmdlrapp.services.DI.Service;

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
    HashMap<Subscriber, List<Channel>> subscriberChannelHashMap;

    private EventDispatcher() {
        this.subscriberChannelHashMap = new HashMap<>();
    }

    /**
     * Notifies all the subscribers that a change was detected, and they need
     * to reload data.
     *
     * @param channel Channel that has new content
     * @param info    Info transmitted
     */
    public void dispatch(Channel channel, String info) {
        for (Subscriber subscriber : this.subscriberChannelHashMap.keySet()) {
            if (isSubscribedTo(subscriber, channel)) {
                subscriber.newContent();
            }
        }
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
