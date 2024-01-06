package io.github.rysefoxx.core.command;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.ChallengeType;
import io.github.rysefoxx.core.loader.ChallengeModuleLoader;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IChallengeDataService;
import io.github.rysefoxx.core.service.ICommandService;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.core.util.InventoryDefaults;
import io.github.rysefoxx.core.util.ItemBuilder;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
public class CommandChallenge implements CommandExecutor, ICommandService {

    private ChallengePlugin plugin;
    private ChallengeModuleLoader challengeModuleLoader;
    private IChallengeDataService challengeDataService;
    private IMessageService messageService;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        this.plugin = plugin;
        this.challengeModuleLoader = plugin.getChallengeModuleLoader();
        this.challengeDataService = ServiceRegistry.findService(IChallengeDataService.class);
        this.messageService = ServiceRegistry.findService(IMessageService.class);
        Objects.requireNonNull(Bukkit.getPluginCommand("challenge")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        RyseInventory.builder()
                .title(messageService.getTranslatedMessage(player, "overview_title"))
                .rows(3)
                .disableUpdateTask()
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fill(InventoryDefaults.BORDER_ITEM);

                        for (ChallengeType challengeType : ChallengeType.values()) {
                            contents.set(challengeType.getInventorySlot(), IntelligentItem.of(ItemBuilder.of(challengeType.getDisplayMateriaL())
                                    .displayName(messageService.getTranslatedMessage(player, challengeType.toString().toLowerCase() + "_displayname"))
                                    .build(), clickEvent -> openInventory(player, challengeType)));
                        }
                    }
                })
                .build(this.plugin)
                .open(player);

        return false;
    }

    private void openInventory(@NotNull Player player, @NotNull ChallengeType challengeType) {
        List<AbstractChallengeModule> challengeModuleList = this.challengeModuleLoader.findByChallengeType(challengeType);
        if (challengeModuleList.isEmpty()) {
            this.messageService.sendTranslatedMessage(player, "no_challenge_found", "prefix");
            return;
        }

        RyseInventory.builder()
                .title(messageService.getTranslatedMessage(player, challengeType.toString().toLowerCase() + "_inventory_title"))
                .rows(6)
                .disableUpdateTask()
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fillBorders(InventoryDefaults.BORDER_ITEM);
                        Pagination pagination = contents.pagination();
                        pagination.setItemsPerPage(28);
                        pagination.iterator(SlotIterator.builder()
                                .type(SlotIterator.SlotIteratorType.HORIZONTAL)
                                .startPosition(1, 1)
                                .build());

                        InventoryDefaults.previous(contents, pagination, player, messageService, 5, 3);

                        for (AbstractChallengeModule challengeModule : challengeModuleList) {
                            pagination.addItem(IntelligentItem.of(challengeModule.displayItem(player, messageService), clickEvent -> {
                                RyseInventory settingsInventory = challengeModule.settingsInventory(player, messageService);
                                if (settingsInventory == null || clickEvent.isLeftClick()) {
                                    challengeModule.setEnabled(!challengeModule.isEnabled());
                                    challengeDataService.saveChallenge(challengeModule);
                                    contents.update(clickEvent.getSlot(), challengeModule.displayItem(player, messageService));
                                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                                    return;
                                }

                                if (clickEvent.isRightClick()) {
                                    settingsInventory.open(player);
                                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                                }
                            }));
                        }

                        InventoryDefaults.next(contents, pagination, player, messageService, 5, 5);
                        InventoryDefaults.back(contents, player, messageService, 5, 0);
                    }
                })
                .build(this.plugin)
                .open(player);

    }
}