package io.github.rysefoxx.core;

import io.github.rysefoxx.core.loader.ChallengeModuleLoader;
import io.github.rysefoxx.core.server.ServerSoftware;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.core.service.DatabaseService;
import io.github.rysefoxx.core.service.ServiceInitializer;
import io.github.rysefoxx.core.service.ServiceLoader;
import lombok.Getter;
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

    private ChallengeModuleLoader challengeModuleLoader;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.setLevel(Level.ALL);
        initialize();

        initializeServices();
    }

    @Override
    public void onDisable() {
//        ServiceLoader<DatabaseService> databaseServices = ServiceLoader.load(DatabaseService.class);
//        databaseServices.forEach(service -> service.onDisable(this));
    }

    private void initialize() {
        ServerSoftwareType softwareType = ServerSoftware.getServerSoftwareType();
        this.challengeModuleLoader = new ChallengeModuleLoader(this);
        this.challengeModuleLoader.load(softwareType);
    }

    private void initializeServices() {
        Map<String, ServiceLoader.ServiceInfo> services = ServiceLoader.loadServices(this);
        services.forEach((name, serviceInfo) -> {
            getLogger().info("Initializing service " + serviceInfo.toString());
            ServiceInitializer.initializeService(serviceInfo, this);
        });


//        ServiceLoader<DatabaseService> databaseServices = ServiceLoader.load(DatabaseService.class);
//        getLogger().warning("Found " + databaseServices.stream().count() + " DatabaseServices");
//        databaseServices.forEach(service -> service.onEnable(this));
//
//        ServiceLoader<TranslationService> translationServices = ServiceLoader.load(TranslationService.class);
//        getLogger().warning("Found " + translationServices.stream().count() + " TranslationServices");
//        translationServices.forEach(service -> service.onEnable(this));
//
//        ServiceLoader<CommandService> commandServices = ServiceLoader.load(CommandService.class);
//        getLogger().warning("Found " + commandServices.stream().count() + " CommandServices");
//        commandServices.forEach(service -> service.onEnable(this));
    }

    public static Logger logger() {
        return logger;
    }
}