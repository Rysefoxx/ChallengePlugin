package io.github.rysefoxx.language.service;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.ITranslationService;
import io.github.rysefoxx.language.Language;
import io.github.rysefoxx.language.TranslationLoader;
import io.github.rysefoxx.language.command.CommandLanguage;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@SuppressWarnings("unchecked")
public class MessageService implements ITranslationService, IMessageService {

    private TranslationLoader translationLoader;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        ServiceRegistry.registerService(IMessageService.class, this);
        this.translationLoader = ServiceRegistry.findService(TranslationLoader.class);
    }

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey) {
        String translation = getTranslatedMessage(player, messageKey);
        Audience audience = ChallengePlugin.getAdventure().player(player);
        audience.sendMessage(ChallengePlugin.getMiniMessage().deserialize(translation));
    }

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements) {
        Audience audience = ChallengePlugin.getAdventure().player(player);
        audience.sendMessage(getTranslatedMessage(player, messageKey, replacements));
    }

    @Override
    public @NotNull String getTranslatedMessage(@NotNull Player player, @NotNull String messageKey) {
        Language language = this.translationLoader.getLanguageSync(player.getUniqueId());
        return this.translationLoader.getTranslations().get(language.getCode()).getOrDefault(messageKey, messageKey);
    }

    @Override
    public <T> @NotNull T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements) {
        Language language = this.translationLoader.getLanguageSync(player.getUniqueId());
        String message = getTranslatedMessage(player, messageKey);
        if (message.equals(messageKey)) return (T) ChallengePlugin.getMiniMessage().deserialize(message);

        for (int i = 0; i < replacements.length; i++) {
            String replacement = replacements[i];

            if (this.translationLoader.getTranslations().get(language.getCode()).containsKey(replacement)) {
                replacement = this.translationLoader.getTranslations().get(language.getCode()).get(replacement);
            }

            message = message.replace("{" + i + "}", replacement);
        }

        return (T) ChallengePlugin.getMiniMessage().deserialize(message);
    }
}