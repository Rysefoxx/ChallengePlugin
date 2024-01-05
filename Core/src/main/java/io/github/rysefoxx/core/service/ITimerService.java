package io.github.rysefoxx.core.service;


import io.github.rysefoxx.core.ChallengePlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public interface ITimerService {

    /**
     * Will be called when the plugin is enabled.
     *
     * @param plugin The {@link ChallengePlugin} to load the translations from.
     */
    void onEnable(@NotNull ChallengePlugin plugin);


    /**
     * Checks if the timer is enabled.
     *
     * @return true if the timer is enabled.
     */
    boolean isTimerEnabled();

    /**
     * Resumes the timer.
     *
     * @param player The player who triggered the resume.
     */
    void resume(@NotNull Player player);

    /**
     * Pauses the timer.
     *
     * @param player The player who triggered the pause. Or null if the timer should be paused by the plugin.
     */
    void pause(@Nullable Player player);

    /**
     * Sets the timer.
     *
     * @param player The player who triggered the set.
     * @param input  The input to set the timer.
     */
    void set(@NotNull Player player, @NotNull String input);

    /**
     * Resets the timer.
     *
     * @param player The player who triggered the reset.
     */
    void reset(@NotNull Player player);

    /**
     * Reverses the timer.
     *
     * @param player The player who triggered the reverse.
     */
    void reverse(@NotNull Player player);
}
