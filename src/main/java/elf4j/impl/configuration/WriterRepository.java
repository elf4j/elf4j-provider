package elf4j.impl.configuration;

import elf4j.impl.writer.ConsoleWriter;
import elf4j.impl.writer.GroupLogWriter;
import elf4j.impl.writer.LogWriter;

import java.util.Properties;

/**
 *
 */
public class WriterRepository {
    private static final LogWriter DEFAULT_WRITER = ConsoleWriter.defaultWriter();
    private final LogWriter logServiceWriter;

    /**
     * @param properties configuration from which to build the writer repo
     */
    public WriterRepository(Properties properties) {
        GroupLogWriter fromProperties = GroupLogWriter.from(properties);
        this.logServiceWriter = fromProperties.isEmpty() ? DEFAULT_WRITER : fromProperties;
    }

    LogWriter getLogServiceWriter() {
        return logServiceWriter;
    }
}
