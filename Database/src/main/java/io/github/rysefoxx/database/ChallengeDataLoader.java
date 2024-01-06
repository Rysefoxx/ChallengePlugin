package io.github.rysefoxx.database;

import io.github.rysefoxx.core.ChallengePlugin;
import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.SettingModule;
import io.github.rysefoxx.core.registry.ServiceRegistry;
import io.github.rysefoxx.core.service.IChallengeDataService;
import io.github.rysefoxx.core.service.IDatabaseService;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
public class ChallengeDataLoader implements IDatabaseService, IChallengeDataService {

    private AsyncDatabaseManager asyncDatabaseManager;
    private ConnectionManager connectionManager;

    @Override
    public void onEnable(@NotNull ChallengePlugin plugin) {
        ServiceRegistry.registerService(IChallengeDataService.class, this);
        this.asyncDatabaseManager = ServiceRegistry.findService(AsyncDatabaseManager.class);
        this.connectionManager = ServiceRegistry.findService(ConnectionManager.class);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void load(@NotNull AbstractChallengeModule challengeModule) {
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection()) {
                if (connection == null) {
                    ChallengePlugin.logger().severe("Failed to load challenge data from database, because the connection is null!");
                    return;
                }

                if (!checkAndCreateChallenge(connection, challengeModule)) {
                    initializeDefaultSettings(challengeModule);
                } else {
                    loadChallengeSettings(connection, challengeModule);
                }
            } catch (SQLException | ClassNotFoundException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Error during loading challenge settings", e);
            }
        });
    }

    /**
     * Checks if the challenge exists in the database. If not, it will be created. Make sure to call this method async.
     *
     * @param connection      The connection to use.
     * @param challengeModule The challenge to check.
     * @return true if the challenge already exists, false if not.
     * @throws SQLException If an error occurs while executing the query.
     */
    private boolean checkAndCreateChallenge(@NotNull Connection connection, @NotNull AbstractChallengeModule challengeModule) throws SQLException {
        if (!challengeExists(connection, challengeModule.getId())) {
            createChallenge(connection, challengeModule);
            return false;
        }
        return true;
    }

    /**
     * Checks if the challenge exists in the database. Make sure to call this method async.
     *
     * @param connection  The connection to use.
     * @param challengeId The challenge to check.
     * @return true if the challenge already exists, false if not.
     * @throws SQLException If an error occurs while executing the query.
     */
    private boolean challengeExists(@NotNull Connection connection, @NotNull String challengeId) throws SQLException {
        String checkQuery = "SELECT EXISTS (SELECT 1 FROM challenge.challenge_data WHERE name = ?)";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setString(1, challengeId);
            try (ResultSet checkResultSet = checkStatement.executeQuery()) {
                return checkResultSet.next() && checkResultSet.getBoolean(1);
            }
        }
    }

    /**
     * Creates a new challenge in the database. Make sure to call this method async.
     *
     * @param connection      The connection to use.
     * @param challengeModule The challenge to create.
     * @throws SQLException If an error occurs while executing the query.
     */
    private void createChallenge(Connection connection, AbstractChallengeModule challengeModule) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO challenge.challenge_data (name, enabled) VALUES (?, ?)")) {
            insertStatement.setString(1, challengeModule.getId());
            insertStatement.setBoolean(2, challengeModule.isEnabled());
            insertStatement.executeUpdate();
        }
    }

    /**
     * Loads the challenge settings from the database. Make sure to call this method async.
     *
     * @param connection      The connection to use.
     * @param challengeModule The challenge to load the settings for.
     * @throws SQLException           If an error occurs while executing the query.
     * @throws ClassNotFoundException If the setting class cannot be found.
     */
    private void loadChallengeSettings(Connection connection, AbstractChallengeModule challengeModule) throws SQLException, ClassNotFoundException {
        String query = "SELECT cd.enabled, cs.setting FROM challenge.challenge_data cd " +
                "LEFT JOIN challenge.challenge_settings cs ON cd.name = cs.name " +
                "WHERE cd.name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, challengeModule.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasSettings = false;
                boolean isFirstRow = true;
                while (resultSet.next()) {
                    if (isFirstRow) {
                        challengeModule.setEnabled(resultSet.getBoolean("enabled"));
                        isFirstRow = false;
                    }

                    String settingJson = resultSet.getString("setting");
                    if (settingJson != null) {
                        SettingModule<?> setting = SettingModule.fromJson(settingJson);
                        challengeModule.addSetting(setting);
                        hasSettings = true;
                    }
                }

                if (!hasSettings) {
                    initializeDefaultSettings(challengeModule);
                }
            }
        }
    }

    /**
     * Initializes the default settings for the challenge.
     *
     * @param challengeModule The challenge to initialize the settings for.
     */
    private void initializeDefaultSettings(@NotNull AbstractChallengeModule challengeModule) {
        List<SettingModule<?>> defaultSettings = challengeModule.defaultSettings();
        for (SettingModule<?> setting : defaultSettings) {
            challengeModule.addSetting(setting);
            saveSetting(challengeModule, setting);
        }
    }

    @Override
    public void saveSetting(@NotNull AbstractChallengeModule challengeModule, @NotNull SettingModule<?> settingModule) {
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection()) {
                String query = "INSERT INTO challenge.challenge_settings (name, setting_key, setting) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE setting = VALUES(setting)";
                try (PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection, query)) {
                    if (preparedStatement == null) {
                        ChallengePlugin.logger().severe("Failed to save challenge data to database, because the prepared statement is null!");
                        return;
                    }

                    preparedStatement.setString(1, challengeModule.getId());
                    preparedStatement.setString(2, settingModule.getKey());
                    preparedStatement.setString(3, settingModule.toJson());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Error during saving challenge setting", e);
            }
        });
    }

    @Override
    public void saveChallenge(@NotNull AbstractChallengeModule challengeModule) {
        this.asyncDatabaseManager.executeAsync(() -> {
            try (Connection connection = this.connectionManager.getConnection()) {
                String query = "INSERT INTO challenge.challenge_data (name, enabled) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE enabled = VALUES(enabled)";
                try (PreparedStatement preparedStatement = this.connectionManager.prepareStatement(connection, query)) {
                    if (preparedStatement == null) {
                        ChallengePlugin.logger().severe("Failed to save challenge data to database, because the prepared statement is null!");
                        return;
                    }

                    preparedStatement.setString(1, challengeModule.getId());
                    preparedStatement.setBoolean(2, challengeModule.isEnabled());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                ChallengePlugin.logger().log(Level.SEVERE, "Error during saving challenge data", e);
            }
        });
    }
}