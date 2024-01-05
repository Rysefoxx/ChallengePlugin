package io.github.rysefoxx.core.registry;

import io.github.rysefoxx.core.ChallengePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class ServiceRegistry {

    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    public static <T> void registerService(@NotNull Class<T> serviceClass, @NotNull T serviceInstance) {
        ChallengePlugin.logger().info("Registering service " + serviceClass.getSimpleName());
        SERVICES.put(serviceClass, serviceInstance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T findService(@NotNull Class<T> serviceClass) {
        return (T) SERVICES.get(serviceClass);
    }
}