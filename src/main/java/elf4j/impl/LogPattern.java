package elf4j.impl;

import java.util.Optional;

public interface LogPattern extends PerformanceSensitive {
    static Optional<String> getPatternOption(String patternContent) {
        String[] contentEntries = patternContent.split(":", 2);
        return contentEntries.length == 1 ? Optional.empty() : Optional.of(contentEntries[1].trim());
    }

    void render(LogEntry logEntry, StringBuilder logText);
}
