package io.github.rysefoxx.core.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@UtilityClass
public class StringUtil {

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

}