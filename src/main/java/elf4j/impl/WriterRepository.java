package elf4j.impl;

import elf4j.Level;

import java.util.Properties;

public class WriterRepository {
    private final LogWriter groupWriter;

    public WriterRepository(Properties properties) {
        this.groupWriter = GroupLogWriter.from(properties);
    }

    LogWriter getGroupWriter() {
        return groupWriter;
    }

    Level getMinimumLevel() {
        return groupWriter.getMinimumLevel();
    }
}
