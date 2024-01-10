package io.github.rysefoxx.challenge;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
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
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
public class NoArmor extends AbstractChallengeModule implements Listener, IChallengeService {

    private static final String ALLOW_PUMPKIN_KEY = "allow_pumpkin";
    private ChallengePlugin plugin;
    private IChallengeDataService challengeDataService;

    public NoArmor() {
        super("no_armor", ChallengeType.CHALLENGE);
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.challengeDataService = ServiceRegistry.findService(IChallengeDataService.class);
    }

    @EventHandler
    public void onPlayerArmorChange(@NotNull PlayerArmorChangeEvent event) {
        if (!isEnabled()) return;
        if (!isTimerEnabled()) return;

        Player player = event.getPlayer();
        if (ignore(player)) return;

        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();
        if ((newItem.getType() == Material.CARVED_PUMPKIN || oldItem.getType() == Material.CARVED_PUMPKIN) && getSetting(ALLOW_PUMPKIN_KEY, Boolean.class).getValue())
            return;

        end(player);
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
                        contents.set(4, IntelligentItem.of(getPumpkinSetting(player, messageService), clickEvent -> {
                            SettingModule<Boolean> setting = getSetting(ALLOW_PUMPKIN_KEY, Boolean.class);
                            updateSetting(ALLOW_PUMPKIN_KEY, !setting.getValue());
                            challengeDataService.saveSetting(NoArmor.this, setting);
                            contents.update(clickEvent.getSlot(), getPumpkinSetting(player, messageService));
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        }));
                    }
                })
                .build(this.plugin);
    }

    @Override
    public @NotNull List<SettingModule<?>> defaultSettings() {
        return List.of(
                new SettingModule<>(ALLOW_PUMPKIN_KEY, true)
        );
    }

    /**
     * Setting item for {@link NoArmor#ALLOW_PUMPKIN_KEY}
     *
     * @param player         to get the language
     * @param messageService to get the language translation
     * @return the setting item for {@link NoArmor#ALLOW_PUMPKIN_KEY}
     */
    private @NotNull ItemStack getPumpkinSetting(@NotNull Player player, @NotNull IMessageService messageService) {
        SettingModule<Boolean> setting = getSetting(ALLOW_PUMPKIN_KEY, Boolean.class);
        return ItemBuilder.of(Material.valueOf(messageService.getTranslatedMessageLegacy(player, this.id + "_pumpkin_material")))
                .displayName(messageService.getTranslatedMessage(player, this.id + "_pumpkin_displayname", messageService.getTranslatedMessageLegacy(player, "enabled_" + setting.getValue())))
                .build();
    }
}