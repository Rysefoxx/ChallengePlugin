package io.github.rysefoxx.core;

import io.github.rysefoxx.core.loader.ChallengeModuleLoader;
import io.github.rysefoxx.core.server.ServerSoftware;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Rysefoxx
 * @since 11.12.2023
 */
@Getter
public class ChallengePlugin extends JavaPlugin {

    private ChallengeModuleLoader challengeModuleLoader;

    @Override
    public void onEnable() {
        initialize();
    }

    @Override
    public void onDisable() {
    }

    private void initialize() {
        ServerSoftwareType softwareType = ServerSoftware.getServerSoftwareType();
        this.challengeModuleLoader = new ChallengeModuleLoader(this);
        this.challengeModuleLoader.load(softwareType);
    }
}