package io.github.rysefoxx.challenge;

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
 * @since 04.01.2024
 */
public class NoFallDamage extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoFallDamage() {
        super("no_fall_damage", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (ignore(player)) return;

        end(player);
    }

    @Override
    public @NotNull ItemStack displayItem(@NotNull Player player, @NotNull IMessageService messageService) {
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_displayname").append(Component.text(" ")).append(messageService.getTranslatedMessage(player, "enabled_" + isEnabled())))
                .lore(StringUtil.splitStringAsComponent(messageService.getTranslatedMessageLegacy(player, this.id + "_lore")))
                .build();
    }
}