package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Rysefoxx
 * @since 27.01.2024
 */
public class OnlyDirt extends AbstractChallengeModule implements Listener, IChallengeService {

    private ChallengePlugin plugin;
    private BukkitTask task;

    public OnlyDirt() {
        super("only_dirt", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onTimerStart() {
        this.task = scheduler();
    }

    @Override
    public void onTimerStop() {
        if (this.task == null) return;
        this.task.cancel();
        this.task = null;
    }

    @Override
    protected @Nullable BukkitTask scheduler() {
        return Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!isEnabled()) return;
            if (!isTimerEnabled()) return;

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (ignore(player)) return;

                Location location = player.getLocation().clone().subtract(0, 0.06, 0);
                Block block = location.getBlock();
                if (block.getType().isAir()) return;
                if (block.getType() == Material.DIRT) return;

                end(player);
            });
        }, 0L, 20L);
    }
}