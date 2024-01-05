package io.github.rysefoxx.core.service;

import io.github.rysefoxx.core.ChallengePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public interface CommandService {

    /**
     * Will be called when the plugin is enabled.
     *
     * @param plugin The {@link ChallengePlugin} to load the translations from.
     */
    void onEnable(@NotNull ChallengePlugin plugin);

}
