package io.github.rysefoxx.core.loader;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.core.service.ServiceLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
public class ChallengeModuleLoader {

    private final ChallengePlugin plugin;
    private final List<AbstractChallengeModule> challengeModules = new ArrayList<>();

    public void load(@NotNull ServerSoftwareType softwareType, @NotNull ServiceLoader.ServiceInfo service) {
        if (softwareType == ServerSoftwareType.UNSUPPORTED) {
            this.plugin.getLogger().severe("The server runs on software that is not supported. Please use one of the following software: " + ServerSoftwareType.getSupportedSoftware());
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }

        for (Class<?> implementedClass : service.implementedClasses()) {
            if (!AbstractChallengeModule.class.isAssignableFrom(implementedClass)) continue;

            try {
                AbstractChallengeModule module = (AbstractChallengeModule) implementedClass.getDeclaredConstructor().newInstance();

                if (module.isSupported(softwareType)) {
                    this.challengeModules.add(module);
                    ChallengePlugin.logger().info("Loaded challenge module " + implementedClass.getSimpleName() + " successfully.");

                    if (module instanceof Listener) {
                        Bukkit.getPluginManager().registerEvents((Listener) module, this.plugin);
                        ChallengePlugin.logger().info("Registered listener for challenge module " + implementedClass.getSimpleName() + " successfully.");
                    }
                    continue;
                }

                ChallengePlugin.logger().warning("The challenge module " + implementedClass.getSimpleName() + " is not supported on this server software.");
            } catch (Exception e) {
                ChallengePlugin.logger().log(java.util.logging.Level.SEVERE, "Failed to load challenge module " + implementedClass.getSimpleName(), e);
            }
        }
    }

}