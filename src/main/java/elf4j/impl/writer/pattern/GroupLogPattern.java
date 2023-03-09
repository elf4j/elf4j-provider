package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class GroupLogPattern implements LogPattern {
    List<LogPattern> logPatternEntries;

    public static GroupLogPattern from(@NonNull String pattern) {
        return new GroupLogPattern(LogPatternType.parseAllPatternsOrThrow(pattern));
    }

    @Override
    public boolean includeCallerDetail() {
        return logPatternEntries.stream().anyMatch(LogPattern::includeCallerDetail);
    }

    @Override
    public boolean includeCallerThread() {
        return logPatternEntries.stream().anyMatch(LogPattern::includeCallerThread);
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        for (LogPattern pattern : logPatternEntries) {
            pattern.render(logEntry, logTextBuilder);
        }
    }
}
