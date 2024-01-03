package io.github.rysefoxx.core.loader;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
public class ChallengeModuleLoader {

    private final ChallengePlugin plugin;

    public void load(@NotNull ServerSoftwareType softwareType) {
        if (softwareType == ServerSoftwareType.UNSUPPORTED) {
            this.plugin.getLogger().severe("The server runs on software that is not supported. Please use one of the following software: " + ServerSoftwareType.getSupportedSoftware());
            return;
        }
    }
}