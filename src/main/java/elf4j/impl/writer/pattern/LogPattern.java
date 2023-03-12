package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.PerformanceSensitive;
import lombok.NonNull;

import java.util.Optional;

/**
 *
 */
public interface LogPattern extends PerformanceSensitive {
    /**
     * @param pattern entire pattern text of an individual pattern segment, including pattern name and possibly options
     * @return the option portion of the pattern text if present; otherwise, empty Optional
     */
    static Optional<String> getPatternOption(@NonNull String pattern) {
        String[] elements = pattern.split(":", 2);
        return elements.length == 1 ? Optional.empty() : Optional.of(elements[1].trim());
    }

    /**
     * From the log entry, renders the data of interest particular to this log pattern instance, and appends the result
     * to the text builder
     *
     * @param logEntry       the overall log data entry to render
     * @param logTextBuilder the overall logging text aggregator
     */
    void render(LogEntry logEntry, StringBuilder logTextBuilder);
}
