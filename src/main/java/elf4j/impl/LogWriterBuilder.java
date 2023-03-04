package elf4j.impl;

import java.util.List;
import java.util.Properties;

public interface LogWriterBuilder {
    List<LogWriter> buildLogWriters(Properties properties);
}
