package io.github.rysefoxx.database;

import io.github.rysefoxx.core.ChallengePlugin;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 02.01.2024
 */
@RequiredArgsConstructor
public class DatabaseTableManager {

    private final ConnectionManager connectionManager;

    /**
     * Executes the queries from data.sql. If data.sql is not found, the server is shut down. <br>
     * The creation takes place synchronously, as there are no users on the server when the plugin is started and the users should only join when the tables have been created.
     */
    public void createDefaultTables() {
        String[] data;

        try (InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("tables.sql"))) {
            data = IOUtils.toString(inputStream, StandardCharsets.UTF_8).split(";");
        } catch (IOException e) {
            ChallengePlugin.logger().log(Level.SEVERE, "Could not load tables.sql", e);

            // Without a database, nothing works, so we shut down the server.
            Bukkit.shutdown();
            return;
        }

        for (String query : data) {
            if (query == null || query.isEmpty() || query.isBlank()) continue;

            try (Connection connection = this.connectionManager.getConnection()) {
                if (connection == null) {
                    ChallengePlugin.logger().severe("Failed to get connection from datasource!");
                    break;
                }

                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
            } catch (SQLException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Failed to execute query: " + query, e);
            }
        }
    }
}