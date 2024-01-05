package io.github.rysefoxx.core.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rysefoxx
 * @since 05.01.2024
 */
@UtilityClass
public class TimeUtil {

    private static final String REGEX_PATTERN = "(\\d+d)|(\\d+h)|(\\d+m)|(\\d+s)";

    /**
     * Parses a time string to seconds
     *
     * @param timeString The time string to parse
     * @return The time in seconds
     */
    public long parseTimeToSeconds(@NotNull String timeString) {
        long totalSeconds = -1;

        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(timeString);

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null) {
                    int value = Integer.parseInt(match.substring(0, match.length() - 1));
                    if (match.endsWith("d")) {
                        totalSeconds += value * 24L * 60 * 60;
                    } else if (match.endsWith("h")) {
                        totalSeconds += value * 60L * 60;
                    } else if (match.endsWith("m")) {
                        totalSeconds += value * 60L;
                    } else if (match.endsWith("s")) {
                        totalSeconds += value;
                    }
                }
            }
        }
        return totalSeconds;
    }

    /**
     * Formats seconds to a time string
     *
     * @param totalSeconds The seconds to format
     * @return The formatted time string
     */
    public @NotNull String formatSecondsToTimeString(@Nonnegative long totalSeconds) {
        final long SECONDS_PER_MINUTE = 60;
        final long SECONDS_PER_HOUR = 3600;
        final long SECONDS_PER_DAY = 86400;

        long days = totalSeconds / SECONDS_PER_DAY;
        totalSeconds %= SECONDS_PER_DAY;
        long hours = totalSeconds / SECONDS_PER_HOUR;
        totalSeconds %= SECONDS_PER_HOUR;
        long minutes = totalSeconds / SECONDS_PER_MINUTE;
        long seconds = totalSeconds % SECONDS_PER_MINUTE;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
            sb.append(hours).append("h ");
        } else if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (days > 0 || hours > 0) {
            sb.append(minutes).append("m ");
        } else if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append("s");
        }

        return sb.toString().trim();
    }

}