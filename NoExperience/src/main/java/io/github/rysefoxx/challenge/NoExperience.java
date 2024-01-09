package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 09.01.2024
 */
public class NoExperience extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoExperience() {
        super("no_experience", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onExpChange(@NotNull PlayerExpChangeEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        end(player);
    }

}