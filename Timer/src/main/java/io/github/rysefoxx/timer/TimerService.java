package io.github.rysefoxx.timer;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.service.ITimerService;
import io.github.rysefoxx.core.util.TimeUtil;
import io.github.rysefoxx.database.AsyncDatabaseManager;
import io.github.rysefoxx.database.ConnectionManager;
import io.github.rysefoxx.language.TranslationKeyDefaults;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class TimerService implements ITimerService {

    private ChallengePlugin plugin;
    private Timer timer;
    private IMessageService messageService;
    private ConnectionManager connectionManager;
    private AsyncDatabaseManager asyncDatabaseManager;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.timer = new Timer();
        ServiceRegistry.registerService(ITimerService.class, this);
        this.messageService = ServiceRegistry.findService(IMessageService.class);
        this.connectionManager = ServiceRegistry.findService(ConnectionManager.class);
        this.asyncDatabaseManager = ServiceRegistry.findService(AsyncDatabaseManager.class);
        load();
        displayScheduler();
    }

    @Override
    public boolean isTimerEnabled() {
        return this.timer.isEnabled();
    }

    @Override
    public void resume(@NotNull Player player) {
        if (this.timer.isEnabled()) {
            this.messageService.sendTranslatedMessage(player, "timer_already_enabled", TranslationKeyDefaults.PREFIX);
            return;
        }

        this.timer.setEnabled(true);
        save();
        this.messageService.sendTranslatedMessage(player, "timer_enabled", TranslationKeyDefaults.PREFIX);
    }

    @Override
    public void pause(@Nullable Player player) {
        if (!this.timer.isEnabled()) {
            if (player != null)
                this.messageService.sendTranslatedMessage(player, "timer_already_disabled", TranslationKeyDefaults.PREFIX);
            return;
        }

        this.timer.setEnabled(false);
        save();
        if (player != null)
            this.messageService.sendTranslatedMessage(player, "timer_disabled", TranslationKeyDefaults.PREFIX);
    }

    @Override
    public void set(@NotNull Player player, @NotNull String input) {
        long seconds = TimeUtil.parseTimeToSeconds(input);
        if (seconds == -1) {
            this.messageService.sendTranslatedMessage(player, "timer_invalid_time", TranslationKeyDefaults.PREFIX, input);
            return;
        }
        timer.setTime(seconds);
        save();
        this.messageService.sendTranslatedMessage(player, "timer_set", TranslationKeyDefaults.PREFIX, input);
    }

    @Override
    public void reset(@NotNull Player player) {
        this.timer.setTime(0);
        save();
        this.messageService.sendTranslatedMessage(player, "timer_reset", TranslationKeyDefaults.PREFIX);
    }

    @Override
    public void reverse(@NotNull Player player) {
        if (this.timer.isReverse()) {
            this.timer.setReverse(false);
            this.messageService.sendTranslatedMessage(player, "timer_reverse_disabled", TranslationKeyDefaults.PREFIX);
        } else {
            this.timer.setReverse(true);
            this.messageService.sendTranslatedMessage(player, "timer_reverse_enabled", TranslationKeyDefaults.PREFIX);
        }
        save();
    }

    /**
     * Displays the scheduler for all players
     */
    private void displayScheduler() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!this.timer.isEnabled()) {
                    onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§o" + this.messageService.getTranslatedMessage(onlinePlayer, "timer_paused")));
                    continue;
                }
                onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6" + TimeUtil.formatSecondsToTimeString(this.timer.getTime())));
            }

            if (!this.timer.isEnabled()) return;
            if (this.timer.isReverse()) {
                this.timer.setTime(this.timer.getTime() - 1);
                if (this.timer.getTime() < 0) timerOver();

                save();
                return;
            }

            this.timer.setTime(this.timer.getTime() + 1);
            save();
        }, 0L, 20L);
    }

    /**
     * Called when the timer is over
     */
    private void timerOver() {
        this.timer.setEnabled(false);
        this.timer.setTime(0);
        this.timer.setReverse(false);
        save();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            this.messageService.sendTranslatedMessage(player, "timer_over", TranslationKeyDefaults.PREFIX);
        });
    }

    /**
     * Saves the timer to the database
     */
    private void save() {
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection();
                 PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection,
                         "INSERT INTO challenge.timer (id, reverse, time) VALUES (0, ?, ?)" +
                                 "ON DUPLICATE KEY UPDATE reverse = VALUES(reverse), time = VALUES(time)")) {

                if (preparedStatement == null) {
                    ChallengePlugin.logger().severe("Failed to save timer to database, because the prepared statement is null!");
                    return;
                }

                preparedStatement.setBoolean(1, this.timer.isReverse());
                preparedStatement.setLong(2, this.timer.getTime());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Failed to save timer to database!", e);
            }
        });
    }

    /**
     * Loads the timer from the database
     */
    private void load() {
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection()) {
                try (PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection, "SELECT * FROM challenge.timer")) {
                    if (preparedStatement == null) {
                        ChallengePlugin.logger().severe("Failed to load language from database, because the prepared statement is null!");
                        return;
                    }

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) return;

                        this.timer.setReverse(resultSet.getBoolean("reverse"));
                        this.timer.setTime(resultSet.getInt("time"));
                    }
                }
            } catch (SQLException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Failed to establish database connection!", e);
            }
        });
    }
}