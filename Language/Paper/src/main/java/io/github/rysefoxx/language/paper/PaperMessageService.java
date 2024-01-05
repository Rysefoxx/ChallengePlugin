package io.github.rysefoxx.language.paper;

import io.github.rysefoxx.language.service.MessageService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class PaperMessageService implements MessageService {

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey) {
    }

    @Override
    public void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements) {
        System.out.println("PaperMessageService.sendTranslatedMessage");
    }

    @Override
    public <T> @NotNull T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz) {
        return null;
    }

    @Override
    public <T> @NotNull T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz, String @NotNull ... replacements) {
        return null;
    }
}