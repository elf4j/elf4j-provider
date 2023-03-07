package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Value
public class GroupLogPattern implements LogPattern {
    List<LogPattern> logPatternEntries;

    public static GroupLogPattern from(@NonNull String pattern) {
        StringBuilder extractablePattern = new StringBuilder(pattern);
        List<LogPattern> logPatternEntries = new ArrayList<>();
        while (extractablePattern.length() > 0) {
            int before = extractablePattern.length();
            EnumSet.allOf(LogPatternType.class)
                    .forEach(patternType -> patternType.extractLeadingPattern(extractablePattern, logPatternEntries));
            if (extractablePattern.length() == before) {
                throw new IllegalArgumentException(String.format("Leading pattern text of '%s' from pattern text '%s'",
                        extractablePattern,
                        pattern));
            }
        }
        return new GroupLogPattern(logPatternEntries);
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
    public void render(LogEntry logEntry, StringBuilder logText) {
        for (LogPattern pattern : logPatternEntries) {
            pattern.render(logEntry, logText);
        }
    }
}
