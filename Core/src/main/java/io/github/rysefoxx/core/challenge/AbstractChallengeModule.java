package io.github.rysefoxx.core.challenge;

import lombok.RequiredArgsConstructor;

/**
 * @author Rysefoxx
 * @since 03.01.2024
 */
@RequiredArgsConstructor
public abstract class AbstractChallengeModule {

    protected final ChallengeType challengeType;

    public void end() {
        //TODO: Implement end method
    }

}