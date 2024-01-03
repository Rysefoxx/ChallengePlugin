package io.github.rysefoxx.core.server;

import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
public enum ServerSoftwareType {

    PAPER,
    SPIGOT,
    UNSUPPORTED;

    /**
     * @return a formatted string with all supported software
     */
    public static @NotNull String getSupportedSoftware() {
        StringBuilder builder = new StringBuilder();
        for (ServerSoftwareType value : values()) {
            if (value == UNSUPPORTED) continue;
            builder.append(value.name()).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

}
