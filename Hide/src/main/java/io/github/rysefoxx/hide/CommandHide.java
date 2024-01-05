package io.github.rysefoxx.hide;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.CommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class CommandHide implements CommandExecutor, CommandService {

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
//        Objects.requireNonNull(Bukkit.getPluginCommand("hide")).setExecutor(this);
//        this.translationManager = ServiceRegistry.findService(TranslationManager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /**
         * Hide
         * Hide <Player>
         * Hide list
         */

        if (args.length == 0) {
//            togglePersonalHide(sender);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
            }
        }

        return false;
    }
}