package elf4j.impl;

public interface LogWriter extends PerformanceSensitive {

    void write(LogEntry logEntry);
}
