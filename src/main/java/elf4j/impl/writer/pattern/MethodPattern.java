package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.Value;

import javax.annotation.Nonnull;
import java.util.Objects;

@Value
public class MethodPattern implements LogPattern {
    @Nonnull
    public static MethodPattern from(String pattern) {
        if (!LogPatternType.METHOD.isTargetOf(pattern)) {
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
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        logTextBuilder.append(Objects.requireNonNull(logEntry.getCallerFrame()).getMethodName());
    }
}
