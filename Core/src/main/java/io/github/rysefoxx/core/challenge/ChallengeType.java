package io.github.rysefoxx.core.challenge;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@Getter
public enum ChallengeType {

    CHALLENGE(11, Material.SCULK_SENSOR),
    RANDOMIZER(12, Material.SOUL_LANTERN),
    LONG_TERM_CHALLENGE(14, Material.CLOCK),
    SPECIAL_CHALLENGE(15, Material.TOTEM_OF_UNDYING);

    private final int inventorySlot;
    private final Material displayMateriaL;

    ChallengeType(@Nonnegative int inventorySlot,@NotNull Material displayMateriaL) {
        this.inventorySlot = inventorySlot;
        this.displayMateriaL = displayMateriaL;
    }
}
