package io.github.rysefoxx.language.command;

import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.util.StringUtil;
import io.github.rysefoxx.language.Language;
import io.github.rysefoxx.language.TranslationKeyDefaults;
import io.github.rysefoxx.language.TranslationLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class CommandLanguage implements CommandExecutor, TabCompleter {

    private final IMessageService messageService;
    private final TranslationLoader translationLoader;

    public CommandLanguage() {
        this.messageService = ServiceRegistry.findService(IMessageService.class);
        this.translationLoader = ServiceRegistry.findService(TranslationLoader.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 1) {
            Language language = Language.fromName(args[0]);
            if (language == null) {
                this.messageService.sendTranslatedMessage(player, "invalid_language", TranslationKeyDefaults.PREFIX, args[0]);
                return true;
            }

            if (this.translationLoader.getLanguageSync(player.getUniqueId()) == language) {
                this.messageService.sendTranslatedMessage(player, "language_already_set", TranslationKeyDefaults.PREFIX, StringUtil.formatString(language.name()));
                return true;
            }

            this.translationLoader.save(player.getUniqueId(), language);
            this.messageService.sendTranslatedMessage(player, "language_set", TranslationKeyDefaults.PREFIX, StringUtil.formatString(language.name()));
            return true;
        }

        if (!sender.hasPermission("challenge.language")) return true;

        if (args.length == 2 && args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("reload")) {
            this.translationLoader.reload();
            this.messageService.sendTranslatedMessage(player, "config_reload", TranslationKeyDefaults.PREFIX);
            return true;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> toReturn = new ArrayList<>();
            if (sender.hasPermission("challenge.language"))
                toReturn.add("config");

            toReturn.addAll(Arrays.stream(Language.values()).map(Language::name).map(StringUtil::formatString).toList());
            return toReturn;
        }

        if (!sender.hasPermission("challenge.language")) return List.of();
        if (args.length == 2) return List.of("reload");

        return null;
    }
}