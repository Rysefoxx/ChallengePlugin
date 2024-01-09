package io.github.rysefoxx.challenge;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.util.ItemBuilder;
import io.github.rysefoxx.core.util.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
public class NoJump extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoJump() {
        super("no_jump", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onPlayerJump(@NotNull PlayerJumpEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        end(player);
    }

}