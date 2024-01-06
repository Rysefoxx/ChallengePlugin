package io.github.rysefoxx.core.util;

import io.github.rysefoxx.core.ChallengePlugin;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@UtilityClass
public class StringUtil {

    /**
     * Formats a string to a readable string
     * @param key The string to format
     * @return The formatted string
     */
    public @NotNull String formatString(@NotNull String key) {
        String[] split = key.split("[\\s_]+");
        StringBuilder formattedString = new StringBuilder();
        for (String word : split) {
            formattedString.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return formattedString.toString().trim();
    }

    public @NotNull Component[] splitStringAsComponent(@NotNull String value) {
        String[] split = value.split("\\n");
        Component[] components = new Component[split.length];
        for (int i = 0; i < split.length; i++) {
            components[i] = ChallengePlugin.getMiniMessage().deserialize(split[i]);
        }

        return components;
    }
}