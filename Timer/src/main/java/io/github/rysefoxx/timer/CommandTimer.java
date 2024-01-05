package io.github.rysefoxx.timer;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.ICommandService;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.service.ITimerService;
import io.github.rysefoxx.language.TranslationKeyDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class CommandTimer implements CommandExecutor, TabCompleter, ICommandService {

    private IMessageService messageService;
    private ITimerService timerService;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.messageService = ServiceRegistry.findService(IMessageService.class);
        this.timerService = ServiceRegistry.findService(ITimerService.class);
        Objects.requireNonNull(Bukkit.getPluginCommand("timer")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (!(sender.hasPermission("challenge.timer"))) {
            this.messageService.sendTranslatedMessage(player, "no_permission", TranslationKeyDefaults.PREFIX);
            return true;
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "resume" -> this.timerService.resume(player);
                case "pause" -> this.timerService.pause(player);
                case "reset" -> this.timerService.reset(player);
                case "reverse" -> this.timerService.reverse(player);
            }
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            this.timerService.set(player, args[1]);
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("challenge.timer"))) return List.of();

        if (args.length == 1) {
            return List.of("resume", "pause", "reset", "reverse", "set");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return List.of("2d3h4m5s (Beispiel)");
        }

        return List.of();
    }
}