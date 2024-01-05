package io.github.rysefoxx.core.service;

import io.github.rysefoxx.core.ChallengePlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.annotation.Nonnegative;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

public class ServiceLoader {

    private static final String CONFIG_FILE_NAME = "services.json";

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

                services.put(serviceName, new ServiceInfo(serviceName, initMethodName, priority, classList));
            }

        } catch (Exception ex) {
            ChallengePlugin.logger().log(Level.SEVERE, "Error reading " + CONFIG_FILE_NAME, ex);
        }
        return services.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().priority())).collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
    }

    public record ServiceInfo(@NotNull String name, @NotNull String initializationMethod, @Nonnegative int priority,
                              @NotNull List<Class<?>> implementedClasses) {

        @Override
        public String toString() {
            return "ServiceInfo{" + "name='" + name + '\'' + ", initializationMethod='" + initializationMethod + '\'' + ", priority=" + priority + ", implementedClasses=" + implementedClasses + '}';
        }
    }
}
