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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 10.01.2024
 */
public class WaterDamage extends AbstractChallengeModule implements Listener, IChallengeService {

    private static final String DAMAGE_KEY = "damage";
    private static final String PERMANENT_KEY = "permanent";
    private ChallengePlugin plugin;
    private IChallengeDataService challengeDataService;
    private BukkitTask task;

    public WaterDamage() {
        super("water_damage", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.challengeDataService = ServiceRegistry.findService(IChallengeDataService.class);
    }

    @Override
    public void onTimerStart() {
        this.task = scheduler();
    }

    @Override
    public void onTimerStop() {
        if (this.task == null) return;
        this.task.cancel();
        this.task = null;
    }

    @Override
    protected @Nullable BukkitTask scheduler() {
        return Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!isEnabled()) return;
            if (!isTimerEnabled()) return;

            double damage = getSetting(DAMAGE_KEY, Double.class).getValue() * 2;
            boolean permanent = getSetting(PERMANENT_KEY, Boolean.class).getValue();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.isInWater()) continue;
                if (ignore(onlinePlayer)) continue;

                onlinePlayer.setNoDamageTicks(0);

                if (permanent) {
                    AttributeInstance maxHealth = onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (maxHealth == null) continue;

                    maxHealth.setBaseValue(maxHealth.getValue() - damage);
                    continue;
                }

                onlinePlayer.damage(damage);
            }

        }, 0L, 20L);
    }

    @Override
    public @Nullable RyseInventory settingsInventory(@NotNull Player player, @NotNull IMessageService messageService) {
        return RyseInventory.builder()
                .title(messageService.getTranslatedMessage(player, this.id + "_settings_title"))
                .disableUpdateTask()
                .rows(1)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fill(InventoryDefaults.BORDER_ITEM);
                        InventoryDefaults.back(contents, player, messageService, 0, 0);

                        contents.set(3, IntelligentItem.of(getDamageSetting(player, messageService), clickEvent -> {
                            SettingModule<Double> setting = getSetting(DAMAGE_KEY, Double.class);

                            if (setting.getValue() <= 0.5 && !clickEvent.isLeftClick()) return;

                            updateSetting(DAMAGE_KEY, clickEvent.isLeftClick() ? setting.getValue() + 0.5 : setting.getValue() - 0.5);
                            challengeDataService.saveSetting(WaterDamage.this, setting);
                            contents.update(clickEvent.getSlot(), getDamageSetting(player, messageService));
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        }));

                        contents.set(5, IntelligentItem.of(getPermanentSetting(player, messageService), clickEvent -> {
                            SettingModule<Boolean> setting = getSetting(PERMANENT_KEY, Boolean.class);
                            updateSetting(PERMANENT_KEY, !setting.getValue());
                            challengeDataService.saveSetting(WaterDamage.this, setting);
                            contents.update(clickEvent.getSlot(), getPermanentSetting(player, messageService));
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        }));
                    }
                })
                .build(this.plugin);
    }

    @Override
    public @NotNull List<SettingModule<?>> defaultSettings() {
        return List.of(
                new SettingModule<>(DAMAGE_KEY, 1.0),
                new SettingModule<>(PERMANENT_KEY, false)
        );
    }

    /**
     * Setting item for {@link WaterDamage#DAMAGE_KEY}
     *
     * @param player         to get the language
     * @param messageService to get the language translation
     * @return the setting item for {@link WaterDamage#DAMAGE_KEY}
     */
    private @NotNull ItemStack getDamageSetting(@NotNull Player player, @NotNull IMessageService messageService) {
        SettingModule<Double> setting = getSetting(DAMAGE_KEY, Double.class);
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_damage_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_damage_displayname", setting.getValue().toString()))
                .lore(StringUtil.splitStringAsComponent(messageService.getTranslatedMessageLegacy(player, this.id + "_damage_lore")))
                .build();
    }

    /**
     * Setting item for {@link WaterDamage#PERMANENT_KEY}
     *
     * @param player         to get the language
     * @param messageService to get the language translation
     * @return the setting item for {@link WaterDamage#PERMANENT_KEY}
     */
    private @NotNull ItemStack getPermanentSetting(@NotNull Player player, @NotNull IMessageService messageService) {
        SettingModule<Boolean> setting = getSetting(PERMANENT_KEY, Boolean.class);
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_permanent_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_permanent_displayname", messageService.getTranslatedMessageLegacy(player, "enabled_" + setting.getValue())))
                .build();
    }
}