package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;

/**
 *
 */
public interface LogWriter extends PerformanceSensitive {

    /**
     * @return the minimum level above which this writer will write
     */
    Level getMinimumLevel();

    /**
     * @param logEntry the log data entry to write out
     */
    void write(LogEntry logEntry);
}
