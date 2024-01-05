package io.github.rysefoxx.language;

import io.github.rysefoxx.core.server.ServerSoftware;
import io.github.rysefoxx.core.server.ServerSoftwareType;
import io.github.rysefoxx.language.service.MessageService;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public class MessageServiceFactory {

    /**
     * Creates a new MessageService based on the server software
     *
     * @return MessageService instance
     */
    public static @NotNull MessageService createMessageService() {
        ServerSoftwareType serverSoftwareType = ServerSoftware.getServerSoftwareType();
        String className = getFullQualifiedClassName(serverSoftwareType);
        try {
            Class<?> clazz = Class.forName(className);
            return (MessageService) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Fehler beim Erstellen der MessageService-Instanz", e);
        }
    }

    /**
     * Returns the full qualified class name of the MessageService
     *
     * @param serverSoftwareType The server software type
     * @return Full qualified class name
     */
    private static @NotNull String getFullQualifiedClassName(@NotNull ServerSoftwareType serverSoftwareType) {
        String type = (serverSoftwareType == ServerSoftwareType.PAPER) ? "Paper" : "Spigot";
        return "io.github.rysefoxx.language." + type.toLowerCase() + "." + type + "MessageService";
    }
}