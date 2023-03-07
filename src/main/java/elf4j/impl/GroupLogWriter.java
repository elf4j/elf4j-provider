package elf4j.impl;

import elf4j.Level;

import java.util.*;

public class GroupLogWriter implements LogWriter {
    private final Set<LogWriter> writers;
    private Level minimumLevel;
    private Boolean includeCallerDetail;
    private Boolean includeCallerThread;

    private GroupLogWriter(Set<LogWriter> writers) {
        this.writers = writers;
    }

    public static GroupLogWriter from(Properties properties) {
        Set<LogWriter> logWriters = new HashSet<>();
        EnumSet.allOf(LogWriterType.class)
                .forEach(writerType -> logWriters.addAll(writerType.parseLogWriters(properties)));
        return new GroupLogWriter(logWriters);
    }

    @Override
    public Level getMinimumLevel() {
        if (minimumLevel == null) {
            minimumLevel = Level.values()[writers.stream()
                    .mapToInt(w -> w.getMinimumLevel().ordinal())
                    .min()
                    .orElseThrow(NoSuchElementException::new)];
        }
        return minimumLevel;
    }

    @Override
    public void write(LogEntry logEntry) {
        writers.parallelStream().forEach(writer -> writer.write(logEntry));
    }

    @Override
    public boolean includeCallerDetail() {
        if (includeCallerDetail == null) {
            includeCallerDetail = writers.stream().anyMatch(LogWriter::includeCallerDetail);
        }
        return includeCallerDetail;
    }

    @Override
    public boolean includeCallerThread() {
        if (includeCallerThread == null) {
            includeCallerThread = writers.stream().anyMatch(LogWriter::includeCallerThread);
        }
        return includeCallerThread;
    }
}
