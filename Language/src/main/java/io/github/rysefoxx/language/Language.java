package io.github.rysefoxx.language;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Rysefoxx
 * @since 02.01.2024
 */
@Getter
public enum Language {

    GERMAN("de"),
    ENGLISH("en");

    private final String code;

    Language(@NotNull String code) {
        this.code = code;
    }

    /**
     * Checks if the language is supported
     *
     * @param code Language code
     * @return true if supported, false if not
     */
    public static boolean isLanguageSupported(@NotNull String code) {
        return Arrays.stream(values()).anyMatch(language -> language.getCode().equalsIgnoreCase(code));
    }

    /**
     * Returns the language from the name
     * @param name Language name
     * @return Language or null if not found
     */
    public static @Nullable Language fromName(@NotNull String name) {
        return Arrays.stream(values()).filter(language -> language.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
