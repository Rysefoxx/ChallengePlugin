package io.github.rysefoxx.language.spigot;

import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.language.Language;
import io.github.rysefoxx.language.TranslationLoader;
import io.github.rysefoxx.language.service.MessageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@SuppressWarnings("unchecked")
public class SpigotMessageService implements MessageService {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final BukkitAudiences adventure;
    private final TranslationLoader translationLoader;

    public SpigotMessageService() {
        this.translationLoader = ServiceRegistry.findService(TranslationLoader.class);
        this.adventure = BukkitAudiences.create(this.translationLoader.getPlugin());
    }

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey) {
        String translation = getTranslatedMessage(player, messageKey, String.class);
        Audience audience = this.adventure.player(player);
        audience.sendMessage(MINI_MESSAGE.deserialize(translation));
    }

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements) {
        Audience audience = this.adventure.player(player);
        audience.sendMessage(getTranslatedMessage(player, messageKey, Component.class, replacements));
    }

    @Override
    public <T> @NotNull T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz) {
        Language language = this.translationLoader.getLanguageSync(player.getUniqueId());
        String translation = this.translationLoader.getTranslations().get(language.getCode()).getOrDefault(messageKey, messageKey);
        return (T) translation;
    }

    @Override
    public <T> @NotNull T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz, String @NotNull ... replacements) {
        Language language = this.translationLoader.getLanguageSync(player.getUniqueId());
        String message = getTranslatedMessage(player, messageKey, String.class);
        if (message.equals(messageKey)) return (T) MINI_MESSAGE.deserialize(message);

        for (int i = 0; i < replacements.length; i++) {
            String replacement = replacements[i];

            if (this.translationLoader.getTranslations().get(language.getCode()).containsKey(replacement)) {
                replacement = this.translationLoader.getTranslations().get(language.getCode()).get(replacement);
            }

            message = message.replace("{" + i + "}", replacement);
        }

        return (T) MINI_MESSAGE.deserialize(message);
    }
}