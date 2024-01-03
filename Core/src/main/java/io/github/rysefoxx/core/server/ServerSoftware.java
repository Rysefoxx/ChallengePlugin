package io.github.rysefoxx.core.server;

import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
public class ServerSoftware {

    /**
     * Checks the server software type. This is important for the compatibility of the individual challenges because, for example, some events only work on paper.
     *
     * @return The {@link ServerSoftwareType} of the server.
     */
    public static @NotNull ServerSoftwareType getServerSoftwareType() {
        if (classExists("com.destroystokyo.paper.ParticleBuilder")) {
            return ServerSoftwareType.PAPER;
        } else if (classExists("org.spigotmc.event.entity.EntityDismountEvent")) {
            return ServerSoftwareType.SPIGOT;
        } else {
            return ServerSoftwareType.UNSUPPORTED;
        }
    }

    /**
     * Checks if the class is available in the current classpath.
     *
     * @param className The class name to check.
     * @return True if the class is available, false otherwise.
     */
    private static boolean classExists(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}