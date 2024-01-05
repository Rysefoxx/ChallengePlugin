package io.github.rysefoxx.core.service;

import io.github.rysefoxx.core.ChallengePlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class ServiceInitializer {

    /**
     * Initializes a service by creating a new instance of the implementation class and calling the initialization method.
     *
     * @param service The service to initialize
     * @param plugin  The plugin to pass to the initialization method
     */
    public static void initializeService(@NotNull ServiceLoader.ServiceInfo service, @NotNull ChallengePlugin plugin) {
        for (Class<?> implClass : service.implementedClasses()) {
            try {
                Object serviceInstance = implClass.getDeclaredConstructor().newInstance();
                Method initMethod = implClass.getMethod(service.initializationMethod(), ChallengePlugin.class);
                initMethod.invoke(serviceInstance, plugin);
            } catch (Exception e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Error initializing service " + implClass.getName(), e);
            }
        }
    }
}