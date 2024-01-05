package io.github.rysefoxx.language.service;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public interface MessageService {

    void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey);

    void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements);

    @NotNull <T> T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz);

    @NotNull <T> T getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, @NotNull Class<T> clazz, String @NotNull ... replacements);

}
