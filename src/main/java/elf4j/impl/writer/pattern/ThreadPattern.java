package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.util.Objects;

@Value
public class ThreadPattern implements LogPattern {
    @NonNull ThreadPattern.DisplayOption threadDisplayOption;

    public static ThreadPattern from(String pattern) {
        if (!pattern.startsWith("thread")) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        return new ThreadPattern(LogPattern.getPatternOption(pattern)
                .map(displayOption -> DisplayOption.valueOf(displayOption.toUpperCase()))
                .orElse(DisplayOption.NAME));
    }

    @Override
    public boolean includeCallerDetail() {
        return false;
    }

    @Override
    public boolean includeCallerThread() {
        return true;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logText) {
        LogEntry.ThreadInformation callerThread = Objects.requireNonNull(logEntry.getCallerThread());
        logText.append(threadDisplayOption == DisplayOption.ID ? callerThread.getId() : callerThread.getName());
    }

    enum DisplayOption {
        ID,
        NAME
    }
}
