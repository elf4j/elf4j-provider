package elf4j.impl;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GroupPattern implements LogPattern {
    @Singular List<LogPattern> patterns;

    @Override
    public void render(LogEntry logEntry, StringBuilder stringBuilder) {
        for (LogPattern pattern : patterns) {
            pattern.render(logEntry, stringBuilder);
        }
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return patterns.stream().anyMatch(LogPattern::isCallerThreadInfoRequired);
    }

    @Override
    public boolean isCallerFrameRequired() {
        return patterns.stream().anyMatch(LogPattern::isCallerFrameRequired);
    }
}
