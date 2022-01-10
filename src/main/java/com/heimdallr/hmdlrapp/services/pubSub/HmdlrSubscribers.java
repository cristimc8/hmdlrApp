package com.heimdallr.hmdlrapp.services.pubSub;

import com.heimdallr.hmdlrapp.services.DI.Service;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Service class that loads all subscribers
 * We search for @On annotated methods and capture their Channel, so we can trigger them
 * from the event dispatcher when needed.
 */
@Service
public class HmdlrSubscribers {
    private Set<Method> methodsAnnotatedWith = null;
    private HashMap<Channel, List<Method>> subscribersMap = new HashMap<>();
    private Set<Method> getSubscribedMethods() {
        if(methodsAnnotatedWith == null) {
            methodsAnnotatedWith = new Reflections("com.heimdallr", new MethodAnnotationsScanner()).getMethodsAnnotatedWith(On.class);
        }
        return methodsAnnotatedWith;
    }

    private HmdlrSubscribers() {
        this.methodsAnnotatedWith = this.getSubscribedMethods();
        this.methodsAnnotatedWith.forEach(m -> {
            // Force the holding classes to be annotated with the @HmdlrSubscriber
            if (!m.getDeclaringClass().isAnnotationPresent(HmdlrSubscriber.class)) return;
            On onAnnotation = m.getAnnotation(On.class);
            List<Method> alreadySubscribedOnChannel = this.subscribersMap.get(onAnnotation.CapturedChannel());
            if (alreadySubscribedOnChannel == null) {
                this.subscribersMap.put(onAnnotation.CapturedChannel(), List.of(m));
            } else {
                alreadySubscribedOnChannel.add(m);
                this.subscribersMap.put(onAnnotation.CapturedChannel(), alreadySubscribedOnChannel);
            }
        });
    }

    /**
     * Returns the list of methods that are capturing a specific channel.
     * @param channel Chanel that is captured
     * @return the list of methods to be executed
     */
    public List<Method> getMethodsSubscribedToChannel(Channel channel) {
        return this.subscribersMap.get(channel);
    }
}
