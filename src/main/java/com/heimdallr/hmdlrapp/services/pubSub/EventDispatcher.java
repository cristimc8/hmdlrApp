package com.heimdallr.hmdlrapp.services.pubSub;

import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.HashMap;

/**
 * Event dispatcher class
 * Using in Pub/Sub architecture
 * Subscribers subscribe to an event and the eventDispatcher notifies them
 * when a change has occurred.
 * Any class may inject the event dispatcher and use it to publish changes.
 */
@Service
public class EventDispatcher {
    HashMap<Subscriber, Channel> subscriberChannelHashMap;

    private EventDispatcher(){
        this.subscriberChannelHashMap = new HashMap<>();
    }

    /**
     * Notifies all the subscribers that a change was detected, and they need
     * to reload data.
     * @param channel Channel that has new content
     * @param info Info transmitted
     */
    public void dispatch(Channel channel, String info) {
        for(Subscriber subscriber : this.subscriberChannelHashMap.keySet()) {
            if(isSubscribedTo(subscriber, channel)) subscriber.newContent();
        }
    }

    private boolean isSubscribedTo(Subscriber subscriber, Channel channel) {
        return this.subscriberChannelHashMap.get(subscriber) == channel;
    }

    public void subscribeTo(Subscriber subscriber, Channel channel) {
        this.subscriberChannelHashMap.put(subscriber, channel);
    }

    public void removeSubscriber(Subscriber subscriber) {
        this.subscriberChannelHashMap.remove(subscriber);
    }
}
