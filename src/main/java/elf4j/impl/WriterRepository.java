package elf4j.impl;

import elf4j.Level;

import java.util.Arrays;
import java.util.Properties;

public class WriterRepository {
    private static final LogWriterBuilder[] LOG_WRITER_BUILDERS =
            new LogWriterBuilder[] { new ConsoleLogWriterBuilder() };
    private final LogWriter groupWriter;

    public WriterRepository(Properties properties) {
        this.groupWriter =
                new GroupLogWriter(new GroupLogWriterBuilder(Arrays.asList(LOG_WRITER_BUILDERS)).buildLogWriters(
                        properties));
    }

    LogWriter getGroupWriter() {
        return groupWriter;
    }

    Level getMinimumLevel() {
        return groupWriter.getMinimumLevel();
    }
}
