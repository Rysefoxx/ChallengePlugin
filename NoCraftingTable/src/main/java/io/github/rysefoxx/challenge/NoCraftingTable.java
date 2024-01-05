package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.core.service.IChallengeService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 04.01.2024
 */
public class NoCraftingTable extends AbstractChallengeModule implements Listener, IChallengeService {

    public NoCraftingTable() {
        super(ChallengeType.CHALLENGE, List.of(ServerSoftwareType.SPIGOT, ServerSoftwareType.PAPER));
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        //TODO: TIMER
        Player player = event.getPlayer();
        if (ignore(player)) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getType() != Material.CRAFTING_TABLE) return;

        event.setCancelled(true);
        end(player);
    }
}