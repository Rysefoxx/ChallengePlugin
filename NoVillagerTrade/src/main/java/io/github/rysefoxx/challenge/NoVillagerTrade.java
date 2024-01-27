package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 27.01.2024
 */
public class NoVillagerTrade extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoVillagerTrade() {
        super("no_villager_trade", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onPlayerTrade(@NotNull PlayerTradeEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        AbstractVillager villager = event.getVillager();
        if (villager.getType() == EntityType.WANDERING_TRADER) return;

        end(player);
    }
}