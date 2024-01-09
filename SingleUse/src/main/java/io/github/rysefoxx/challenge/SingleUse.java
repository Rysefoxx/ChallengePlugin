package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 09.01.2024
 */
public class SingleUse extends AbstractChallengeModule implements Listener, IChallengeService {

    public SingleUse() {
        super("single_use", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = (Player) event.getPlayer();
        if (ignore(player)) return;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (!itemStack.getType().isAir()) continue;
            if (!itemStack.hasItemMeta()) continue;
            if (!(itemStack.getItemMeta() instanceof Damageable damageable)) continue;

            damageable.setDamage(itemStack.getType().getMaxDurability() - 1);
            itemStack.setItemMeta(damageable);
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (!itemStack.hasItemMeta()) return;
        if (!(itemStack.getItemMeta() instanceof Damageable damageable)) return;

        damageable.setDamage(itemStack.getType().getMaxDurability() - 1);
        itemStack.setItemMeta(damageable);
    }

}