package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Rysefoxx
 * @since 09.01.2024
 */
public class SkeletonFollow extends AbstractChallengeModule implements Listener, IChallengeService {

    private final HashMap<UUID, Skeleton> playerSkeleton = new HashMap<>();
    private ChallengePlugin plugin;
    private BukkitTask task;

    public SkeletonFollow() {
        super("skeleton_follow", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onTimerStart() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (ignore(player)) return;
            spawnSkeleton(player);
        });

        enableSkeletons();
        this.task = scheduler();
    }

    @Override
    public void onTimerStop() {
        disableSkeletons();

        if (this.task == null) return;
        this.task.cancel();
        this.task = null;
    }

    @Override
    protected @Nullable BukkitTask scheduler() {
        return Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!isEnabled()) return;
            if (!isTimerEnabled()) return;

            for (Map.Entry<UUID, Skeleton> entry : this.playerSkeleton.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null) continue;

                Skeleton skeleton = entry.getValue();
                if (!isTooFarAway(skeleton, player)) continue;

                Location location = player.getLocation().clone();
                skeleton.teleport(location.add(location.getDirection().multiply(-2)));
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;
        Player player = (Player) event.getPlayer();
        if (ignore(player)) return;

        spawnSkeleton(player);
    }

    /**
     * Spawns a skeleton for a player and adds it to the hashmap. The skeleton has a leather helmet.
     *
     * @param player the player for which the skeleton is spawned
     */
    private void spawnSkeleton(@NotNull Player player) {
        if (this.playerSkeleton.containsKey(player.getUniqueId())) return;

        Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);

        entity.setInvulnerable(true);
        entity.setGlowing(true);

        Skeleton skeleton = (Skeleton) entity;
        skeleton.setShouldBurnInDay(false);
        skeleton.setConversionTime(-1);

        EntityEquipment equipment = skeleton.getEquipment();

        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET), true);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        skeleton.setTarget(player);
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setAI(false);

        this.playerSkeleton.put(player.getUniqueId(), skeleton);
    }

    /**
     * Enables all skeletons ai
     */
    private void enableSkeletons() {
        this.playerSkeleton.values().forEach(skeleton -> skeleton.setAI(true));
    }

    /**
     * Disables all skeletons ai
     */
    private void disableSkeletons() {
        this.playerSkeleton.values().forEach(skeleton -> skeleton.setAI(false));
    }

    /**
     * Checks if the skeleton is too far away from the player
     *
     * @param skeleton the skeleton
     * @param player   the player
     * @return true if the skeleton is too far away from the player or the worlds are not the same
     */
    private boolean isTooFarAway(@NotNull Skeleton skeleton, @NotNull Player player) {
        if (skeleton.getWorld().getUID() != player.getWorld().getUID()) return true;
        return skeleton.getLocation().distance(player.getLocation()) > 20;
    }

}