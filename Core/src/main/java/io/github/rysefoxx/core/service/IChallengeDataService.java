package io.github.rysefoxx.core.service;

import io.github.rysefoxx.core.challenge.AbstractChallengeModule;
import io.github.rysefoxx.core.challenge.SettingModule;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
public interface IChallengeDataService {

    /**
     * Load the challenge data from the database.
     *
     * @param challengeModule The {@link AbstractChallengeModule} to load the data from.
     */
    void load(@NotNull AbstractChallengeModule challengeModule);

    /**
     * Save a setting to the database.
     *
     * @param challengeModule The {@link AbstractChallengeModule} to save the data from.
     * @param settingModule   The {@link SettingModule} to save the data from.
     */
    void saveSettings(@NotNull AbstractChallengeModule challengeModule, @NotNull SettingModule<?> settingModule);

    /**
     * Save the challenge to the database.
     *
     * @param challengeModule The {@link AbstractChallengeModule} to save the data from.
     */
    void saveChallenge(@NotNull AbstractChallengeModule challengeModule);
}
