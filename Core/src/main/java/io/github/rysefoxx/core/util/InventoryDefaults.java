/*
 *      Copyright (c) 2023 Rysefoxx
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.rysefoxx.core.util;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.service.IMessageService;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.Optional;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
@UtilityClass
public class InventoryDefaults {

    public ItemStack BORDER_ITEM = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE, Component.text("§r")).build();

    public void previous(@NotNull InventoryContents contents,
                         @NotNull Pagination pagination,
                         @NotNull Player player,
                         @NotNull IMessageService messageService,
                         @Nonnegative int row,
                         @Nonnegative int column) {

        int previousPage = pagination.page() - 1;
        contents.set(row, column, IntelligentItem.of(ItemBuilder.of(Material.PAPER)
                .customModelData(1)
                .amount(pagination.isFirst()
                        ? 1
                        : pagination.page() - 1)
                .displayName(pagination.isFirst()
                        ? messageService.getTranslatedMessage(player, "already_on_first_page_displayname")
                        : messageService.getTranslatedMessage(player, "next_page", String.valueOf(previousPage))).build(), event -> {
            if (pagination.isFirst()) {
                messageService.sendTranslatedMessage(player, "already_on_first_page_message", "prefix");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                return;
            }

            RyseInventory currentInventory = pagination.inventory();
            currentInventory.open(player, pagination.previous().page());
        }));
    }

    public void next(@NotNull InventoryContents contents,
                     @NotNull Pagination pagination,
                     @NotNull Player player,
                     @NotNull IMessageService messageService,
                     @Nonnegative int row,
                     @Nonnegative int column) {

        int page = pagination.page() + 1;
        contents.set(row, column, IntelligentItem.of(ItemBuilder.of(Material.PAPER)
                .customModelData(2)
                .amount((pagination.isLast() ? 1 : page))
                .displayName(!pagination.isLast()
                        ? messageService.getTranslatedMessage(player, "next_page", String.valueOf(page))
                        : messageService.getTranslatedMessage(player, "already_on_last_page_displayname")).build(), event -> {
            if (pagination.isLast()) {
                messageService.sendTranslatedMessage(player, "already_on_last_page_message", "prefix");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                return;
            }

            RyseInventory currentInventory = pagination.inventory();
            currentInventory.open(player, pagination.next().page());
        }));
    }

    public void back(@NotNull InventoryContents contents,
                     @NotNull Player player,
                     @NotNull IMessageService messageService,
                     @Nonnegative int row,
                     @Nonnegative int column) {
        contents.set(row, column, IntelligentItem.of(ItemBuilder.of(Material.OAK_DOOR).displayName(messageService.getTranslatedMessage(player, "back_page")).build(), event -> {
            Optional<RyseInventory> optional = ChallengePlugin.getInventoryManager().getLastInventory(player.getUniqueId());
            optional.ifPresent(inventory -> inventory.open(player));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
        }));
    }
}
