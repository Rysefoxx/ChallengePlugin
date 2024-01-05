package io.github.rysefoxx.language.command;

import io.github.rysefoxx.language.Language;
import io.github.rysefoxx.language.MessageServiceFactory;
import io.github.rysefoxx.language.TranslationKeyDefaults;
import io.github.rysefoxx.language.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class CommandLanguage implements CommandExecutor, TabCompleter {

    private final MessageService messageService;

    public CommandLanguage() {
        this.messageService = MessageServiceFactory.createMessageService();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        /**
         * Language <language>
         * Language config reload
         */

        if (args.length == 1) {
            Language language = Language.fromName(args[0]);
            if (language == null) {
                this.messageService.sendTranslatedMessage(player, "invalid_language");
                this.messageService.sendTranslatedMessage(player, "invalid_language", TranslationKeyDefaults.PREFIX, args[0]);
                return true;
            }

            return true;
        }

        if (args.length == 2) {
            return true;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}