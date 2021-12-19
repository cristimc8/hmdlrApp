package com.heimdallr.hmdlrapp.services.DI;

import com.heimdallr.hmdlrapp.exceptions.ServiceInitException;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * Dependency injector class used to instantiate services and ready them
 * to be load in any part of the code.
 * It uses the singleton architecture of the services used by this app in order to
 * provide a smooth experience without memory leaks.
 */
public class HmdlrDI {
    private static HmdlrDI _instance = null;

    private final HashMap<String, Object> instantiatedServices = new HashMap<>();

    /**
     * HMDLR Dependency injector initializer method
     * Takes a list of service classes, checks if they are all properly annotated
     * using @Service with the help of Java's reflection API and saves them,
     * so they can be later used.
     *
     * @param services List of @Service annotated classes
     */
    public void initialize(List<Class<?>> services) throws ServiceInitException {
        StringBuilder errors = new StringBuilder();
        for (Class<?> service : services) {
            if (!service.isAnnotationPresent(Service.class))
                errors.append(service.getName()).append(" cannot be used as a Service class!\n");
            else {
                try {
                    Class<?> associatedRepo = getAssociatedRepoFromService(service);
                    Constructor<?> serviceConstructor = getServiceConstructor(service, associatedRepo != void.class);
                    Object instantiatedService = getInstantiatedServiceFromConstructor(associatedRepo, serviceConstructor, associatedRepo != void.class);
                    instantiatedServices.put(service.getName(), instantiatedService);
                } catch (Exception e) {
                    errors.append("While instantiating: ").append(service.getName()).append(" -> ").append(e.toString());
                }
            }
        }
        if (!errors.isEmpty())
            throw new ServiceInitException(errors.toString());
    }

    /**
     * Private method used to get the instantiated service class, either with or
     * without the default constructor arguments.
     *
     * @param AssociatedRepoClass The class for the service's associated repo
     * @param serviceConstructor  The right constructor (with or without arguments) for service
     * @param hasRepo             Boolean indicating whether our service wants a repo or not.
     * @return The instantiated Service object.
     */
    private Object getInstantiatedServiceFromConstructor(
            Class<?> AssociatedRepoClass,
            Constructor<?> serviceConstructor,
            boolean hasRepo) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (hasRepo) {
            return serviceConstructor.newInstance(AssociatedRepoClass.getDeclaredConstructor().newInstance());
        }
        return serviceConstructor.newInstance();
    }

    /**
     * Private method that returns a service's constructor, either
     * the default empty one or the one that accepts an associated repo
     * as a parameter.
     *
     * @param service Service class object
     * @param hasRepo Boolean indicating whether the service accepts an associated repo.
     * @return Service's constructor
     * @throws NoSuchMethodException If there is no good constructor present
     */
    private Constructor<?> getServiceConstructor(Class<?> service, boolean hasRepo) throws NoSuchMethodException {
        Constructor<?> constructor;
        if (hasRepo) {
            constructor = service.getDeclaredConstructor(Object.class);
        } else constructor = service.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor;
    }

    /**
     * Private method that returns an associated repo present in a Service's
     * annotation, if any.
     * Returns 'void' if none is present.
     *
     * @param service Service class
     * @return Associated repo class or Void
     */
    private Class<?> getAssociatedRepoFromService(Class<?> service) {
        Service mService = service.getAnnotation(Service.class);
        return mService.AssociatedRepo();
    }


    /**
     * Test method to see all instantiated services registered.
     */
    public void getInstantiatedServices() {
        for (String key : instantiatedServices.keySet()) {
            System.out.println(key);
        }
    }

    /**
     * Method that returns an initialized instance of a singleton service.
     *
     * @return the initialized service class
     */
    public <T> Object getService(Class<T> serviceName) throws ServiceNotRegisteredException {
        if (instantiatedServices.containsKey(serviceName.getName())) {
            return instantiatedServices.get(serviceName.getName());
        }
        throw new ServiceNotRegisteredException(serviceName.getName() + " is not a registered service!");
    }

    private HmdlrDI() {
    }

    /**
     * Method that returns the instantiated Hmdlr Dependency Injector object that holds
     * the instantiated services states.
     * @return HmdlrDI Instantiated object
     */
    public static HmdlrDI getContainer() {
        if (_instance == null) _instance = new HmdlrDI();
        return _instance;
    }
}
