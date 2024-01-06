package io.github.rysefoxx.core;

import io.github.rysefoxx.core.loader.ChallengeModuleLoader;
import io.github.rysefoxx.core.server.ServerSoftware;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.core.service.ServiceInitializer;
import io.github.rysefoxx.core.service.ServiceLoader;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rysefoxx
 * @since 11.12.2023
 */
@Getter
public class ChallengePlugin extends JavaPlugin {

    private static Logger logger;
    @Getter
    private static BukkitAudiences adventure;
    @Getter
    private static MiniMessage miniMessage;

    private ChallengeModuleLoader challengeModuleLoader;

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        miniMessage = MiniMessage.miniMessage();
        logger = getLogger();
        logger.setLevel(Level.ALL);
        initializeServices();
    }

    @Override
    public void onDisable() {
        adventure.close();
        ServiceLoader.terminateServices();
    }

    private void initializeServices() {
        ServerSoftwareType softwareType = ServerSoftware.getServerSoftwareType();
        this.challengeModuleLoader = new ChallengeModuleLoader(this);

        Map<String, ServiceLoader.ServiceInfo> services = ServiceLoader.loadServices(this);
        services.forEach((name, serviceInfo) -> {
            getLogger().info("Initializing service " + serviceInfo.toString());
            ServiceInitializer.initializeService(serviceInfo, this);

            if (name.equals("IChallengeService")) {
                this.challengeModuleLoader.load(softwareType, serviceInfo);
            }
        });

        this.challengeModuleLoader.loadChallengeData();
    }

    public static Logger logger() {
        return logger;
    }
}