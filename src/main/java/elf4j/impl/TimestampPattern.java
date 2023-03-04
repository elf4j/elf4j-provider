package elf4j.impl;

import lombok.Value;

import java.time.format.DateTimeFormatter;

@Value
public class TimestampPattern implements LogPattern {
    DateTimeFormatter dateTimeFormatter;

    @Override
    public boolean isCallerFrameRequired() {
        return false;
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return false;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        logTextBuilder.append(dateTimeFormatter.format(logEntry.getTimestamp()));
    }
}
