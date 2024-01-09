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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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

        Arrays.stream(player.getInventory().getContents()).forEach(this::oneDurability);
    }


    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = event.getPlayer();
        if (ignore(player)) return;

        ItemStack itemStack = event.getItem();
        oneDurability(itemStack);
    }

    /**
     * Sets the durability of an item to 1
     *
     * @param itemStack the itemstack to set the durability
     */
    private void oneDurability(@Nullable ItemStack itemStack) {
        if (itemStack == null) return;
        if (!itemStack.getType().isAir()) return;
        if (!itemStack.hasItemMeta()) return;
        if (!(itemStack.getItemMeta() instanceof Damageable damageable)) return;

        damageable.setDamage(itemStack.getType().getMaxDurability() - 1);
        itemStack.setItemMeta(damageable);
    }
}