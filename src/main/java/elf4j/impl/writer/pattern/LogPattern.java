package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.PerformanceSensitive;

import java.util.Optional;

public interface LogPattern extends PerformanceSensitive {
    static Optional<String> getPatternOption(String pattern) {
        String[] elements = pattern.split(":", 2);
        return elements.length == 1 ? Optional.empty() : Optional.of(elements[1].trim());
    }

    void render(LogEntry logEntry, StringBuilder logTextBuilder);
}
