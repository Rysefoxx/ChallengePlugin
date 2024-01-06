package io.github.rysefoxx.challenge;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.challenge.SettingModule;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IChallengeDataService;
import io.github.rysefoxx.core.service.IChallengeService;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.util.InventoryDefaults;
import io.github.rysefoxx.core.util.ItemBuilder;
import io.github.rysefoxx.core.util.StringUtil;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
public class SpeedTen extends AbstractChallengeModule implements Listener, IChallengeService {

    private static final String AMPLIFIER_KEY = "amplifier";
    private ChallengePlugin plugin;
    private IChallengeDataService challengeDataService;

    public SpeedTen() {
        super("speed_ten", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.challengeDataService = ServiceRegistry.findService(IChallengeDataService.class);
    }

    @EventHandler
    public void onCreatureSpawn(@NotNull CreatureSpawnEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;

        SettingModule<Integer> setting = getSetting(AMPLIFIER_KEY, Integer.class);
        event.getEntity().addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, setting.getValue() - 1));
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;

        Player player = event.getPlayer();
        if (ignore(player)) return;

        SettingModule<Integer> setting = getSetting(AMPLIFIER_KEY, Integer.class);
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, setting.getValue() - 1));
    }

    @Override
    public void onTimerStart() {
        SettingModule<Integer> setting = getSetting(AMPLIFIER_KEY, Integer.class);
        Bukkit.getWorlds().forEach(world -> world.getLivingEntities()
                .forEach(livingEntity -> livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, setting.getValue() - 1))));

    }

    @Override
    public void onTimerStop() {
        Bukkit.getWorlds().forEach(world -> world.getLivingEntities()
                .forEach(livingEntity -> livingEntity.removePotionEffect(PotionEffectType.SPEED)));
    }

    @Override
    public @NotNull ItemStack displayItem(@NotNull Player player, @NotNull IMessageService messageService) {
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_displayname").append(Component.text(" ")).append(messageService.getTranslatedMessage(player, "enabled_" + isEnabled())))
                .lore(StringUtil.splitStringAsComponent(messageService.getTranslatedMessageLegacy(player, this.id + "_lore")))
                .build();
    }

    @Override
    public @Nullable RyseInventory settingsInventory(@NotNull Player player, @NotNull IMessageService messageService) {
        return RyseInventory.builder()
                .title(messageService.getTranslatedMessage(player, this.id + "_settings_title"))
                .rows(1)
                .disableUpdateTask()
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fill(InventoryDefaults.BORDER_ITEM);

                        SettingModule<Integer> setting = getSetting(AMPLIFIER_KEY, Integer.class);
                        contents.set(4, IntelligentItem.of(getSpeedSetting(player, messageService), clickEvent -> {
                            int change = clickEvent.isRightClick() ? -1 : 1;
                            int newSettingValue = setting.getValue() + change;
                            updateSetting(AMPLIFIER_KEY, newSettingValue);
                            challengeDataService.saveSetting(SpeedTen.this, setting);
                            contents.update(clickEvent.getSlot(), getSpeedSetting(player, messageService));
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        }));

                    }
                })
                .build(this.plugin);
    }

    @Override
    public @NotNull List<SettingModule<?>> defaultSettings() {
        return List.of(
                new SettingModule<>(AMPLIFIER_KEY, 10)
        );
    }

    /**
     * Setting item for {@link SpeedTen#AMPLIFIER_KEY}
     *
     * @param player         to get the language
     * @param messageService to get the language translation
     * @return the setting item for {@link SpeedTen#AMPLIFIER_KEY}
     */
    private @NotNull ItemStack getSpeedSetting(@NotNull Player player, @NotNull IMessageService messageService) {
        SettingModule<Integer> setting = getSetting(AMPLIFIER_KEY, Integer.class);
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_potion_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_potion_displayname", setting.getValue().toString()))
                .lore(StringUtil.splitStringAsComponent(messageService.getTranslatedMessageLegacy(player, this.id + "_potion_lore")))
                .build();
    }
}