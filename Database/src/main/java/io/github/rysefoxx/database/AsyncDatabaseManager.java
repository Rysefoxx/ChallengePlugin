package io.github.rysefoxx.database;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.DatabaseService;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Rysefoxx
 * @since 02.01.2024
 */
public class AsyncDatabaseManager implements DatabaseService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /***
     * Executes a runnable async. If a callback is provided, it will be called on success or failure.
     * @param runnable The runnable to execute.
     */
    public void executeAsync(@NotNull Runnable runnable) {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Shuts down the executor service.
     */
    public void shutdownExecutorService() {
        if (EXECUTOR_SERVICE.isShutdown()) return;
        EXECUTOR_SERVICE.shutdown();
    }

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        ServiceRegistry.registerService(AsyncDatabaseManager.class, this);
    }

    @Override
    public void onDisable(@NotNull ChallengePlugin plugin) {
        shutdownExecutorService();
    }
}