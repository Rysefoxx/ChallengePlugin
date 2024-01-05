package io.github.rysefoxx.language.listener;

import io.github.rysefoxx.language.TranslationLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final TranslationLoader translationLoader;

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        this.translationLoader.cachePlayerLanguage(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        this.translationLoader.deletePlayerLanguage(event.getPlayer().getUniqueId());
    }
}