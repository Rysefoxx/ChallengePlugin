package io.github.rysefoxx.core.loader;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IChallengeDataService;
import io.github.rysefoxx.core.service.ServiceInitializer;
import io.github.rysefoxx.core.service.ServiceLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
@Getter
public class ChallengeModuleLoader {

    private final ChallengePlugin plugin;
    private final List<AbstractChallengeModule> challengeModules = new ArrayList<>();

    public void load(@NotNull ServiceLoader.ServiceInfo service) {
        for (Class<?> implementedClass : service.implementedClasses()) {
            if (!AbstractChallengeModule.class.isAssignableFrom(implementedClass)) continue;

            try {
                AbstractChallengeModule module = (AbstractChallengeModule) implementedClass.getDeclaredConstructor().newInstance();
                this.challengeModules.add(module);
                ChallengePlugin.logger().info("Loaded challenge module " + implementedClass.getSimpleName() + " successfully.");

                if (module instanceof Listener) {
                    Bukkit.getPluginManager().registerEvents((Listener) module, this.plugin);
                    ChallengePlugin.logger().info("Registered listener for challenge module " + implementedClass.getSimpleName() + " successfully.");
                }

                ServiceInitializer.initializeServices(implementedClass, module, service.initializationMethod(), this.plugin);
            } catch (Exception e) {
                ChallengePlugin.logger().log(java.util.logging.Level.SEVERE, "Failed to load challenge module " + implementedClass.getSimpleName(), e);
            }
        }
    }

    /**
     * Loads all challenge data from the database
     */
    public void loadChallengeData() {
        IChallengeDataService service = ServiceRegistry.findService(IChallengeDataService.class);
        this.challengeModules.forEach(service::load);
    }

    /**
     * Gives all challenge modules that equals the given challenge type
     *
     * @param challengeType The challenge type to check
     * @return A list of all challenge modules that equals the given challenge type
     */
    public @NotNull List<AbstractChallengeModule> findByChallengeType(@NotNull ChallengeType challengeType) {
        return this.challengeModules.stream().filter(module -> module.getChallengeType() == challengeType).toList();
    }
}