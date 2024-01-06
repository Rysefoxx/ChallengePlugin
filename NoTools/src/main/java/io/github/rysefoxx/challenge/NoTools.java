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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
public class NoTools extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoTools() {
        super("no_tools", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        String key = itemStack.getType().getKey().toString().toLowerCase();
        if (!key.endsWith("_shovel")
                && !key.endsWith("_pickaxe")
                && !key.endsWith("_axe")
                && !key.endsWith("_hoe")
                && itemStack.getType() != Material.SHEARS
                && itemStack.getType() != Material.FISHING_ROD
                && itemStack.getType() != Material.FLINT_AND_STEEL) {
            return;
        }

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