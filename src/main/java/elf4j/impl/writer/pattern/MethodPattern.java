package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.Value;

import java.util.Objects;

@Value
public class MethodPattern implements LogPattern {
    public static MethodPattern from(String pattern) {
        if (!"method".equals(pattern)) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        return new MethodPattern();
    }

    @Override
    public boolean includeCallerDetail() {
        return true;
    }

    @Override
    public boolean includeCallerThread() {
        return false;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logText) {
        logText.append(Objects.requireNonNull(logEntry.getCallerFrame()).getMethodName());
    }
}
