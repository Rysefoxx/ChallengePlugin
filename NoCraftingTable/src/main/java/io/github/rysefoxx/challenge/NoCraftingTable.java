package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 04.01.2024
 */
public class NoCraftingTable extends AbstractChallengeModule implements Listener {

    public NoCraftingTable() {
        super(ChallengeType.CHALLENGE);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) return;
        if(clickedBlock.getType() != Material.CRAFTING_TABLE) return;

        event.setCancelled(true);
        end();
    }
}