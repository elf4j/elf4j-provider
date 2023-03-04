package elf4j.impl;

import elf4j.Level;

import java.util.List;
import java.util.NoSuchElementException;

public class GroupLogWriter implements LogWriter {
    private final List<LogWriter> writers;

    public GroupLogWriter(List<LogWriter> writers) {
        this.writers = writers;
    }

    @Override
    public Level getMinimumLevel() {
        return Level.values()[writers.stream()
                .mapToInt(w -> w.getMinimumLevel().ordinal())
                .min()
                .orElseThrow(NoSuchElementException::new)];
    }

    @Override
    public void write(LogEntry logEntry) {
        writers.parallelStream().forEach(writer -> writer.write(logEntry));
    }

    @Override
    public boolean isCallerFrameRequired() {
        return writers.stream().anyMatch(PerformanceSensitive::isCallerFrameRequired);
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return writers.stream().anyMatch(PerformanceSensitive::isCallerThreadInfoRequired);
    }
}
