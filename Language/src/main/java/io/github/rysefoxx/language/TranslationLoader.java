package io.github.rysefoxx.language;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.TranslationService;
import io.github.rysefoxx.database.AsyncDatabaseManager;
import io.github.rysefoxx.database.ConnectionManager;
import io.github.rysefoxx.language.command.CommandLanguage;
import io.github.rysefoxx.language.listener.ConnectionListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class TranslationLoader implements TranslationService {

    @Getter
    private final HashMap<String, HashMap<String, String>> translations = new HashMap<>();
    private final HashMap<UUID, Language> playerLanguage = new HashMap<>();
    @Getter
    private ChallengePlugin plugin;
    private ConnectionManager connectionManager;
    private AsyncDatabaseManager asyncDatabaseManager;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.connectionManager = ServiceRegistry.findService(ConnectionManager.class);
        this.asyncDatabaseManager = ServiceRegistry.findService(AsyncDatabaseManager.class);
        ServiceRegistry.registerService(TranslationLoader.class, this);
        onLoad();
        registerCommand();
        registerListener();
    }

    /**
     * Registers all commands.
     */
    private void registerCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("language")).setExecutor(new CommandLanguage());
    }

    /**
     * Registers all listeners.
     */
    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this.plugin);
    }

    /**
     * Loads all messages from the plugin to the plugin folder.
     */
    public void onLoad() {
        for (Language language : Language.values()) {
            try {
                this.plugin.saveResource("messages_" + language.getCode() + ".properties", false);
            } catch (IllegalArgumentException exception) {
                this.plugin.getLogger().severe("Failed to save messages for language " + language + "!");
                continue;
            }
            cacheTranslations(plugin, language);
        }
    }

    /**
     * Reload all translations.
     */
    public void reload() {
        this.translations.clear();
        for (Language value : Language.values()) {
            cacheTranslations(plugin, value);
        }
    }

    /**
     * Caches all translations from the file to the {@link HashMap}.
     *
     * @param plugin   The {@link Plugin} to cache the translations from.
     * @param language The {@link Language} to cache the translations from.
     */
    private void cacheTranslations(@NotNull Plugin plugin, @NotNull Language language) {
        HashMap<String, String> translationsMap = new HashMap<>();
        File file = new File(plugin.getDataFolder(), "messages_" + language.getCode() + ".properties");

        if (!file.exists()) {
            plugin.getLogger().severe("Die Übersetzungsdatei für die Sprache " + language + " wurde nicht gefunden!");
            return;
        }

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException exception) {
            plugin.getLogger().severe("Die Übersetzungsdatei für die Sprache " + language + " ist invalide! " + exception.getMessage());
            return;
        }

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            translationsMap.put(key, value);
        }

        this.translations.put(language.getCode(), translationsMap);
    }

    /**
     * Get the player language from cache.
     *
     * @param uuid The uuid of the player.
     * @return The language of the player.
     */
    public @NotNull Language getLanguageSync(@NotNull UUID uuid) {
        if (!this.playerLanguage.containsKey(uuid)) {
            ChallengePlugin.logger().severe("Failed to get language for " + uuid + " because it is not cached!");
            return Language.ENGLISH;
        }

        return this.playerLanguage.getOrDefault(uuid, Language.ENGLISH);
    }

    /**
     * Cache the language of a player.
     *
     * @param uuid The uuid of the player.
     */
    public void cachePlayerLanguage(UUID uuid) {
        getOrSetLanguage(uuid).thenAccept(language -> this.playerLanguage.put(uuid, language));
    }

    /**
     * Deletes the cached language of a player.
     *
     * @param uuid The uuid of the player.
     */
    public void deletePlayerLanguage(UUID uuid) {
        this.playerLanguage.remove(uuid);
    }

    /**
     * Gets the language of a player from the database. If the player does not exist, it will be saved with the default language.
     *
     * @param uuid The uuid of the player.
     * @return The language of the player.
     */
    public CompletableFuture<Language> getOrSetLanguage(@NotNull UUID uuid) {
        CompletableFuture<Language> future = new CompletableFuture<>();

        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection()) {
                try (PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection, "SELECT * FROM challenge.language WHERE uuid = ?")) {
                    if (preparedStatement == null) {
                        this.plugin.getLogger().severe("Failed to load language from database, because the prepared statement is null!");
                        save(uuid, Language.ENGLISH);
                        future.complete(Language.ENGLISH);
                        return;
                    }

                    preparedStatement.setString(1, uuid.toString());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            save(uuid, Language.ENGLISH);
                            future.complete(Language.ENGLISH);
                            return;
                        }

                        Language language = Language.fromName(resultSet.getString("language"));
                        if (language == null) {
                            save(uuid, Language.ENGLISH);
                            future.complete(Language.ENGLISH);
                            return;
                        }

                        this.playerLanguage.put(uuid, language);
                        future.complete(language);
                    }
                }
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to establish database connection!", e);
                save(uuid, Language.ENGLISH);
                future.complete(Language.ENGLISH);
            }
        });

        return future;
    }

    /**
     * Saves player language to database.
     *
     * @param uuid     The uuid of the player.
     * @param language The language of the player.
     */
    public void save(@NotNull UUID uuid, @NotNull Language language) {
        this.playerLanguage.put(uuid, language);
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection();
                 PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection,
                         "INSERT INTO challenge.language (uuid, language) VALUES (?, ?)" +
                                 "ON DUPLICATE KEY UPDATE language = VALUES(language)")) {

                if (preparedStatement == null) {
                    this.plugin.getLogger().severe("Failed to save language for " + uuid + " to database, because the prepared statement is null!");
                    return;
                }

                preparedStatement.setObject(1, uuid);
                preparedStatement.setString(2, language.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to save language for " + uuid + " to database!", e);
            }
        });
    }
}