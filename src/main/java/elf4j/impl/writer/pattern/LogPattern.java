package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.PerformanceSensitive;

import java.util.Optional;

public interface LogPattern extends PerformanceSensitive {
    static Optional<String> getPatternOption(String patternContent) {
        String[] contentEntries = patternContent.split(":", 2);
        return contentEntries.length == 1 ? Optional.empty() : Optional.of(contentEntries[1].trim());
    }

    void render(LogEntry logEntry, StringBuilder logText);
}
