package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

public class GroupLogWriter implements LogWriter {
    private final Set<LogWriter> writers;
    private Level minimumLevel;
    private Boolean includeCallerDetail;
    private Boolean includeCallerThread;

    private GroupLogWriter(Set<LogWriter> writers) {
        this.writers = writers;
    }

    public static GroupLogWriter from(Properties properties) {
        return new GroupLogWriter(LogWriterType.getLogWriters(properties));
    }

    @Override
    public Level getMinimumLevel() {
        if (minimumLevel == null) {
            minimumLevel = Level.values()[writers.stream()
                    .mapToInt(writer -> writer.getMinimumLevel().ordinal())
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

    public boolean isEmpty() {
        return writers.isEmpty();
    }
}
