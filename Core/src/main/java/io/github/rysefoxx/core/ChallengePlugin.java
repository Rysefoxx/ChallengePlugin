package io.github.rysefoxx.core;

import io.github.rysefoxx.core.loader.ChallengeModuleLoader;
import io.github.rysefoxx.core.service.ServiceInitializer;
import io.github.rysefoxx.core.service.ServiceLoader;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
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
    private static MiniMessage miniMessage;
    @Getter
    private static InventoryManager inventoryManager;

    private ChallengeModuleLoader challengeModuleLoader;

    @Override
    public void onEnable() {
        miniMessage = MiniMessage.miniMessage();
        inventoryManager = new InventoryManager(this);
        inventoryManager.invoke();
        logger = getLogger();
        logger.setLevel(Level.ALL);
        initializeServices();
    }

    @Override
    public void onDisable() {
        ServiceLoader.terminateServices();
    }

    private void initializeServices() {
        this.challengeModuleLoader = new ChallengeModuleLoader(this);

        Map<String, ServiceLoader.ServiceInfo> services = ServiceLoader.loadServices(this);
        services.forEach((name, serviceInfo) -> {
            getLogger().info("Initializing service " + serviceInfo.toString());
            ServiceInitializer.initializeService(serviceInfo, this);

            if (name.equals("IChallengeService")) {
                this.challengeModuleLoader.load(serviceInfo);
            }
        });

        this.challengeModuleLoader.loadChallengeData();
    }

    public static Logger logger() {
        return logger;
    }
}