package elf4j.impl;

import lombok.Value;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Value
public class LayoutPattern implements LogPattern {
    List<LogPattern> logPatternEntries;

    public static LayoutPattern from(String pattern) {
        StringBuilder p = new StringBuilder(pattern);
        List<LogPattern> logPatternEntries = new ArrayList<>();
        while (p.length() > 0) {
            int before = p.length();
            EnumSet.allOf(LogPatternElementType.class)
                    .stream()
                    .map(patternType -> patternType.parseAndTrimLeadingPattern(p))
                    .forEach(patternEntry -> patternEntry.ifPresent(logPatternEntries::add));
            if (p.length() == before) {
                throw new IllegalArgumentException(String.format("pattern: %s", p));
            }
        }
        assert p.length() == 0;
        return new LayoutPattern(logPatternEntries);
    }

    @Override
    public boolean isCallerFrameRequired() {
        return logPatternEntries.stream().anyMatch(LogPattern::isCallerFrameRequired);
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return logPatternEntries.stream().anyMatch(LogPattern::isCallerThreadInfoRequired);
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        for (LogPattern pattern : logPatternEntries) {
            pattern.render(logEntry, logTextBuilder);
        }
    }
}
