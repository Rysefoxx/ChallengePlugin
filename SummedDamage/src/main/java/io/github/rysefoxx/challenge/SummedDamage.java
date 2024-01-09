package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 09.01.2024
 */
public class SummedDamage extends AbstractChallengeModule implements Listener, IChallengeService {

    public SummedDamage() {
        super("summed_damage", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        Chunk chunk = damager.getChunk();
        double damage = event.getDamage();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            if (ignore(player)) return;

            int multiplier = 0;
            for (Entity chunkEntity : chunk.getEntities()) {
                if (chunkEntity.getType() != damager.getType()) continue;
                multiplier++;
            }
            event.setDamage(damage * multiplier);
            return;
        }

        event.setCancelled(true);
        for (Entity chunkEntity : chunk.getEntities()) {
            if (event.getEntityType() != chunkEntity.getType()) continue;
            if (!(chunkEntity instanceof LivingEntity livingEntity)) continue;
            livingEntity.damage(damage);
        }
    }

}