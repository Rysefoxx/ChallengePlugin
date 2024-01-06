package io.github.rysefoxx.core.service;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public interface IMessageService {

    void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey);

    void sendTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements);

    @NotNull String getTranslatedMessageLegacy(@NotNull Player player, @NotNull String messageKey);

    @NotNull Component getTranslatedMessage(@NotNull Player player, @NotNull String messageKey);

    @NotNull Component getTranslatedMessage(@NotNull Player player, @NotNull String messageKey, String @NotNull ... replacements);

}
