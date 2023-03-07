package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;

public interface LogWriter extends PerformanceSensitive {

    Level getMinimumLevel();

    void write(LogEntry logEntry);
}
