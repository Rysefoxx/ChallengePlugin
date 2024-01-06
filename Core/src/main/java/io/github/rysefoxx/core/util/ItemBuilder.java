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

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.rysefoxx.core.ChallengePlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnegative;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 30.10.2023
 */
@NoArgsConstructor
@Setter
@Getter
public class ItemBuilder {

    private static final UUID PROFILE_UUID = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");

    private ItemStack itemStack;

    private Component displayName;
    private String displayNameOld;
    private Material material;
    private int amount = 1;
    private ItemMeta itemMeta;

    private OfflinePlayer skullOwner;
    private String skullUrl;

    private List<ItemFlag> flags = new ArrayList<>();
    private List<Component> lore = new ArrayList<>();
    private List<String> loreOld = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<Container<?>> containers = new HashSet<>();
    private int customModelData = -1;
    private boolean unbreakable;

    private ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta == null)
            this.itemMeta = Bukkit.getItemFactory().getItemMeta(this.material);

        if (this.itemMeta != null && this.itemMeta.hasLore()) {
            this.lore = this.itemMeta.lore();
            this.loreOld = this.itemMeta.getLore();
        }

        if (this.itemMeta != null && this.itemMeta.hasEnchants())
            this.enchantments = this.itemMeta.getEnchants();

        if (this.itemMeta != null && this.itemMeta.hasDisplayName())
            this.displayName = this.itemMeta.displayName();

        if (this.itemMeta != null && !this.itemMeta.getItemFlags().isEmpty())
            this.flags = Arrays.asList(this.itemMeta.getItemFlags().toArray(new ItemFlag[0]));

        if (this.itemMeta instanceof SkullMeta)
            this.skullOwner = ((SkullMeta) this.itemMeta).getOwningPlayer();
    }

    private ItemBuilder(@NotNull Material material) {
        this.material = material;
    }

    private ItemBuilder(@NotNull Material material, @NotNull Component displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    @Contract("_ -> new")
    public static @NotNull ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    public static @NotNull ItemBuilder of(@NotNull Material material, @NotNull Component component) {
        return new ItemBuilder(material, component);
    }

    @Contract("_ -> new")
    public static @NotNull ItemBuilder of(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder unbreakable() {
        this.unbreakable = true;
        return this;
    }

    public ItemBuilder material(@NotNull Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder skullUrl(@NotNull String url) {
        this.skullUrl = url;
        return this;
    }


    public ItemBuilder customModelData(@Nonnegative int data) {
        this.customModelData = data;
        return this;
    }

    public ItemBuilder amount(@NotNull Number number) {
        int amount = number.intValue();
        if (amount > this.material.getMaxStackSize() || amount > 64) {
            amount = this.material.getMaxStackSize();
        }
        if (amount < 0) {
            amount = 1;
        }

        this.amount = amount;
        return this;
    }

    public ItemBuilder displayName(@NotNull Component displayName) {
        this.displayName = displayName.decoration(TextDecoration.ITALIC, false);
        return this;
    }

    public ItemBuilder displayName(@NotNull String displayName) {
        this.displayNameOld = displayName;
        return this;
    }

    public ItemBuilder appendDisplayName(@NotNull Component displayName) {
        if (this.displayName == null)
            this.displayName = Component.empty();

        this.displayName = this.displayName.append(displayName.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder clearLore() {
        if (this.lore == null || this.lore.isEmpty()) return this;

        this.lore.clear();
        this.itemMeta.lore(this.lore);
        return this;
    }

    public ItemBuilder itemFlag(@NotNull ItemFlag itemFlag) {
        this.flags.add(itemFlag);
        return this;
    }


    public ItemBuilder itemFlags(ItemFlag @NotNull ... itemFlag) {
        this.flags.addAll(Arrays.asList(itemFlag));
        return this;
    }

    public ItemBuilder allItemFlags() {
        this.flags.addAll(Arrays.asList(ItemFlag.values()));
        return this;
    }

    public ItemBuilder enchantment(@NotNull Enchantment enchantment, @Nonnegative int value) {
        this.enchantments.put(enchantment, value);
        return this;
    }

    public ItemBuilder lore(String line) {
        Component component = ChallengePlugin.getMiniMessage().deserialize(line);
        this.lore.add(component);
        return this;
    }

    public ItemBuilder lore(Component @NotNull ... lines) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].decoration(TextDecoration.ITALIC, false);
        }

        this.lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder lore(@Nullable List<Component> list) {
        if (list == null) return this;

        try {
            list.replaceAll(component -> component.decoration(TextDecoration.ITALIC, false));
        } catch (UnsupportedOperationException e) {
            list = new ArrayList<>(list);
            list.replaceAll(component -> component.decoration(TextDecoration.ITALIC, false));
        }
        this.lore.addAll(list);
        return this;
    }

    public ItemBuilder loreOld(@Nullable List<String> list) {
        if (list == null) return this;

        this.loreOld.addAll(list);
        return this;
    }

    public ItemBuilder skullOwner(@NotNull String string) {
        this.skullOwner = Bukkit.getOfflinePlayer(string);
        return this;
    }

    public ItemBuilder skullOwner(@NotNull UUID uuid) {
        this.skullOwner = Bukkit.getOfflinePlayer(uuid);
        return this;
    }

    public <T, Z> ItemBuilder dataContainer(@NotNull NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.containers.add(new Container<>(key, value, type));
        return this;
    }

    public @NotNull ItemStack build() {
        if (this.itemStack == null)
            this.itemStack = new ItemStack(this.material, this.amount);

        if (this.itemMeta == null)
            this.itemMeta = this.itemStack.getItemMeta();

        this.itemMeta.setUnbreakable(this.unbreakable);

        if (this.customModelData != -1)
            this.itemMeta.setCustomModelData(this.customModelData);

        if (this.displayNameOld != null && this.itemMeta != null)
            this.itemMeta.setDisplayName(this.displayNameOld);

        if (this.displayName != null && this.itemMeta != null)
            this.itemMeta.displayName(this.displayName);

        if (!this.loreOld.isEmpty() && this.itemMeta != null)
            this.itemMeta.setLore(this.loreOld);

        if (!this.lore.isEmpty() && this.itemMeta != null)
            this.itemMeta.lore(this.lore);

        if (!this.flags.isEmpty())
            this.flags.forEach(itemFlag -> this.itemMeta.addItemFlags(itemFlag));

        if (!this.enchantments.isEmpty())
            this.enchantments.forEach((enchantment, level) -> this.itemMeta.addEnchant(enchantment, level, true));

        if (this.skullOwner != null && this.itemMeta instanceof SkullMeta) {
            this.itemStack.setType(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) this.itemMeta;
            skullMeta.setOwningPlayer(this.skullOwner);
            this.itemMeta = skullMeta;
        }

        if (!this.containers.isEmpty() && this.itemMeta != null) {
            for (Container<?> container : this.containers)
                this.itemMeta.getPersistentDataContainer().set(container.key(), container.type(), container.value());
        }

        this.itemStack.setAmount(this.amount);

        if (this.skullUrl != null && this.itemMeta instanceof SkullMeta) {
            this.itemStack.setType(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) this.itemMeta;
            PlayerProfile profile = Bukkit.createProfile(PROFILE_UUID);
            PlayerTextures textures = profile.getTextures();
            URL urlObject;
            try {
                urlObject = new URL(this.skullUrl);
            } catch (MalformedURLException exception) {
                Bukkit.getLogger().log(Level.SEVERE, "Invalid URL", exception);
                this.itemStack.setItemMeta(this.itemMeta);
                return this.itemStack;
            }
            textures.setSkin(urlObject);
            profile.setTextures(textures);
            skullMeta.setPlayerProfile(profile);

            this.itemMeta = skullMeta;
        }

        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    private record Container<Z>(NamespacedKey key, Z value, @SuppressWarnings("rawtypes") PersistentDataType type) {
    }
}
