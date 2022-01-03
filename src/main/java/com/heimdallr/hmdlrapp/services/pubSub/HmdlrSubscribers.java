package com.heimdallr.hmdlrapp.services.pubSub;

import com.heimdallr.hmdlrapp.services.DI.Service;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.Set;

@Service
public class HmdlrSubscribers {
    private Set<Method> methodsAnnotatedWith = null;
    private Set<Method> getSubscribedMethods() {
        if(methodsAnnotatedWith == null) {
            methodsAnnotatedWith = new Reflections("com.heimdallr", new MethodAnnotationsScanner()).getMethodsAnnotatedWith(On.class);
        }
        return methodsAnnotatedWith;
    }

    private HmdlrSubscribers() {
        this.methodsAnnotatedWith = this.getSubscribedMethods();
    }


}
