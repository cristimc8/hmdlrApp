package com.heimdallr.hmdlrapp.services.DI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to acknowledge a class of type Service, which is
 * a singleton whose state is managed by HmdlrDI class.
 *
 * It can have an AssociatedRepo class, then it must declare a constructor
 * that accepts only an Object argument which is the instantiated class
 * of its AssociatedRepo.
 */
@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
    Class AssociatedRepo() default void.class;
}
