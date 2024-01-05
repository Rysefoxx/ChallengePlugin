package io.github.rysefoxx.core.service;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.annotation.Nonnegative;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class ServiceLoader {

    private static final String CONFIG_FILE_NAME = "services.json";
    private static Map<String, ServiceInfo> cachedServices = new HashMap<>();

    /**
     * Terminates all services by calling the termination method on each service instance.
     */
    public static void terminateServices() {
        for (ServiceInfo service : cachedServices.values()) {
            if (service.terminateMethod() == null || service.terminateMethod().isBlank()) continue;

            for (Class<?> implementedClass : service.implementedClasses()) {
                try {
                    Object serviceInstance = ServiceRegistry.findService(implementedClass);
                    Method terminateMethod = serviceInstance.getClass().getMethod(service.terminateMethod());
                    terminateMethod.invoke(serviceInstance);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    ChallengePlugin.logger().log(Level.SEVERE, "Error terminating service " + implementedClass.getName(), e);
                }
            }
        }
    }

    /**
     * Loads all services from the services.json file.
     *
     * @param plugin The plugin instance
     * @return A map of service names to service info
     */
    public static @NotNull Map<String, ServiceInfo> loadServices(@NotNull ChallengePlugin plugin) {
        Map<String, ServiceInfo> services = new HashMap<>();

        try (InputStream input = ServiceLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (input == null) {
                ChallengePlugin.logger().severe("Could not load " + CONFIG_FILE_NAME);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return services;
            }

            JSONObject jsonObject = new JSONObject(new JSONTokener(input));
            JSONArray servicesArray = jsonObject.getJSONArray("services");

            for (int i = 0; i < servicesArray.length(); i++) {
                JSONObject serviceObject = servicesArray.getJSONObject(i);
                String serviceName = serviceObject.getString("name");
                String initMethodName = serviceObject.getString("initialization_method");
                String terminateMethodName = serviceObject.getString("terminate_method");
                int priority = serviceObject.getInt("initialization_priority");
                JSONArray implementedClasses = serviceObject.getJSONArray("implemented_classes");

                List<Class<?>> classList = new ArrayList<>();
                for (int j = 0; j < implementedClasses.length(); j++) {
                    String className = implementedClasses.getString(j);
                    try {
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        ChallengePlugin.logger().log(Level.SEVERE, "Class not found: " + className, e);
                    }
                }

                services.put(serviceName, new ServiceInfo(serviceName, initMethodName, terminateMethodName, priority, classList));
            }

        } catch (Exception ex) {
            ChallengePlugin.logger().log(Level.SEVERE, "Error reading " + CONFIG_FILE_NAME, ex);
        }
        cachedServices = services.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().priority())).collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
        return cachedServices;
    }

    public record ServiceInfo(@NotNull String name, @NotNull String initializationMethod,
                              @Nullable String terminateMethod, @Nonnegative int priority,
                              @NotNull List<Class<?>> implementedClasses) {

        @Override
        public String toString() {
            return "ServiceInfo{" + "name='" + name + '\''
                    + ", initializationMethod='" + initializationMethod
                    + ", terminateMethod='" + terminateMethod
                    + ", priority=" + priority
                    + ", implementedClasses=" + implementedClasses + '}';
        }
    }
}
