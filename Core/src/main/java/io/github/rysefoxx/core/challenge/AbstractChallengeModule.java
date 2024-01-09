package io.github.rysefoxx.core.challenge;

import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.service.ITimerService;
import io.github.rysefoxx.core.util.ItemBuilder;
import io.github.rysefoxx.core.util.StringUtil;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractChallengeModule {

    protected final List<SettingModule<?>> settings = new ArrayList<>(defaultSettings());

    @Setter
    protected boolean enabled = false;
    protected final String id;
    protected final ChallengeType challengeType;

    /**
     * Checks if the player should be ignored
     *
     * @param player The player to check
     * @return true if ignored, false if not
     */
    protected boolean ignore(@NotNull Player player) {
        return player.getGameMode() == GameMode.SPECTATOR || player.isDead();
    }

    /**
     * Ends the challenge and sends a message to all players
     *
     * @param trigger The player who triggered the end
     */
    public void end(@NotNull Player trigger) {
        IMessageService messageService = ServiceRegistry.findService(IMessageService.class);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            messageService.sendTranslatedMessage(player, "challenge_end", "prefix", trigger.getName());
        });

        ITimerService timerService = ServiceRegistry.findService(ITimerService.class);
        timerService.pause(null);
    }

    /**
     * Checks if the timer is enabled
     *
     * @return true if enabled, false if not
     */
    public boolean isTimerEnabled() {
        ITimerService service = ServiceRegistry.findService(ITimerService.class);
        return service.isTimerEnabled();
    }

    /**
     * Get a setting by key
     *
     * @param key  The key to get the setting from
     * @param type The type of the setting
     * @param <T>  The type of the setting
     * @return The setting value
     */
    public @NotNull <T> SettingModule<T> getSetting(@NotNull String key, Class<T> type) {
        return this.settings.stream()
                .filter(setting -> setting.getKey().equals(key) && type.isInstance(setting.getValue()))
                .map(setting -> (SettingModule<T>) setting)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Setting with key " + key + " not found!"));
    }


    /**
     * Add a setting to the challenge
     *
     * @param settingModule The setting to add
     */
    public void addSetting(@NotNull SettingModule<?> settingModule) {
        this.settings.removeIf(setting -> setting.getKey().equals(settingModule.getKey()));
        this.settings.add(settingModule);
    }

    /**
     * Update a setting value
     *
     * @param key   The key of the setting
     * @param value The new value
     */
    public void updateSetting(@NotNull String key, @NotNull Object value) {
        this.settings.stream()
                .filter(setting -> setting.getKey().equals(key))
                .findFirst()
                .ifPresent(setting -> {
                    if (value.getClass().getName().equals(setting.getType())) {
                        ((SettingModule) setting).setValue(value);
                    } else {
                        throw new IllegalArgumentException("Type mismatch. Expected: " + setting.getType() + ", Provided: " + value.getClass().getName());
                    }
                });
    }


    /**
     * Gives the default settings for the challenge
     *
     * @return The default settings or an empty list if there are no default settings
     */
    public @NotNull List<SettingModule<?>> defaultSettings() {
        return new ArrayList<>();
    }

    /**
     * This itemstack is displayed in the challenge overview
     *
     * @param player         The player to display the itemstack for
     * @param messageService The message service to get the messages from
     * @return The itemstack to display
     */
    public @NotNull ItemStack displayItem(@NotNull Player player, @NotNull IMessageService messageService) {
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_displayname").append(Component.text(" ")).append(messageService.getTranslatedMessage(player, "enabled_" + isEnabled())))
                .lore(StringUtil.splitStringAsComponent(messageService.getTranslatedMessageLegacy(player, this.id + "_lore")))
                .build();
    }

    /**
     * Settings inventory for the challenge. This is called when the player right click on the challenge in the challenge overview
     *
     * @param player         The player to display the inventory for
     * @param messageService The message service to get the messages from
     * @return The settings inventory or null if there is no settings inventory
     */
    public @Nullable RyseInventory settingsInventory(@NotNull Player player, @NotNull IMessageService messageService) {
        return null;
    }

    /**
     * This method is called when the timer starts
     */
    public void onTimerStart() {
    }

    /**
     * This method is called when the timer stops
     */
    public void onTimerStop() {
    }

    /**
     * @return The scheduler for the challenge
     */
    protected @Nullable BukkitTask scheduler() {
        return null;
    }
}