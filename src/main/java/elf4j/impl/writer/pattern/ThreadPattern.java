package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.util.Objects;

@Value
public class ThreadPattern implements LogPattern {
    @NonNull ThreadPattern.DisplayOption threadDisplayOption;

    public static ThreadPattern from(@NonNull String pattern) {
        if (!LogPatternType.THREAD.isTargetOf(pattern)) {
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
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        LogEntry.ThreadInformation callerThread = Objects.requireNonNull(logEntry.getCallerThread());
        logTextBuilder.append(threadDisplayOption == DisplayOption.ID ? callerThread.getId() : callerThread.getName());
    }

    enum DisplayOption {
        ID,
        NAME
    }
}
