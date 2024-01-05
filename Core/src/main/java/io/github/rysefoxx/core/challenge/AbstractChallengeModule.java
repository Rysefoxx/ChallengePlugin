package io.github.rysefoxx.core.challenge;

import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.service.ITimerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
public abstract class AbstractChallengeModule {

    protected final ChallengeType challengeType;
    protected final List<ServerSoftwareType> supportedSoftware;

    /**
     * Checks if the player should be ignored
     *
     * @param player The player to check
     * @return true if ignored, false if not
     */
    protected boolean ignore(@NotNull Player player) {
        return player.getGameMode() == GameMode.SPECTATOR || player.isDead();
    }

    /**
     * Checks if the challenge is supported by the server software
     *
     * @param serverSoftwareType The server software to check
     * @return true if supported, false if not
     */
    public boolean isSupported(@NotNull ServerSoftwareType serverSoftwareType) {
        return this.supportedSoftware.contains(serverSoftwareType);
    }

    /**
     * Ends the challenge and sends a message to all players
     *
     * @param trigger The player who triggered the end
     */
    public void end(@NotNull Player trigger) {
        IMessageService service = ServiceRegistry.findService(IMessageService.class);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            service.sendTranslatedMessage(player, "challenge_end", "prefix", trigger.getName());
        });
    }

    /**
     * Checks if the timer is enabled
     *
     * @return true if enabled, false if not
     */
    public boolean isTimerEnabled() {
        ITimerService service = ServiceRegistry.findService(ITimerService.class);
        return service.isTimerEnabled();
    }
}