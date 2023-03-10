package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Value
public class TimestampPattern implements LogPattern {
    private static final DateTimeFormatter DEFAULT_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
    private static final ZoneId DEFAULT_TIMESTAMP_ZONE = ZoneId.systemDefault();
    DateTimeFormatter dateTimeFormatter;

    public static TimestampPattern from(@NonNull String pattern) {
        if (!LogPatternType.TIMESTAMP.isTargetOf(pattern)) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        DateTimeFormatter dateTimeFormatter = LogPattern.getPatternOption(pattern)
                .map(DateTimeFormatter::ofPattern)
                .orElse(DEFAULT_TIMESTAMP_FORMATTER);
        if (dateTimeFormatter.getZone() == null) {
            dateTimeFormatter = dateTimeFormatter.withZone(DEFAULT_TIMESTAMP_ZONE);
        }
        return new TimestampPattern(dateTimeFormatter);
    }

    @Override
    public boolean includeCallerDetail() {
        return false;
    }

    @Override
    public boolean includeCallerThread() {
        return false;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        logTextBuilder.append(dateTimeFormatter.format(logEntry.getTimestamp()));
    }
}