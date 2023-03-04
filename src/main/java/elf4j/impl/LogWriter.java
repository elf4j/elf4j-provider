package elf4j.impl;

import elf4j.Level;

public interface LogWriter extends PerformanceSensitive {

    Level getMinimumLevel();

    void write(LogEntry logEntry);
}
