package elf4j.impl;

import lombok.NonNull;
import lombok.Value;

@Value
public class VerbatimPattern implements LogPattern {
    @NonNull String text;

    public static VerbatimPattern from(String pattern) {
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
    public void render(LogEntry logEntry, StringBuilder logText) {
        logText.append(text);
    }
}
