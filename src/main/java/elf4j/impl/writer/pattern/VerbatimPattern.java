package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;

@Value
public class VerbatimPattern implements LogPattern {
    @NonNull String text;

    @Nonnull
    public static VerbatimPattern from(String pattern) {
        if (!LogPatternType.VERBATIM.isTargetOf(pattern)) {
            throw new IllegalArgumentException(String.format(
                    "pattern '%s' looks to be targeted at another known pattern type than %s",
                    pattern,
                    LogPatternType.VERBATIM));
        }
        return new VerbatimPattern(pattern);
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
        logTextBuilder.append(text);
    }
}
