package elf4j.impl.configuration;

import elf4j.impl.writer.GroupLogWriter;
import elf4j.impl.writer.LogWriter;

import java.util.Properties;

public class WriterRepository {
    private final LogWriter logServiceWriter;

    public WriterRepository(Properties properties) {
        this.logServiceWriter = GroupLogWriter.from(properties);
    }

    LogWriter getLogServiceWriter() {
        return logServiceWriter;
    }
}
