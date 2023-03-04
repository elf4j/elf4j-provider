package elf4j.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GroupLogWriterBuilder implements LogWriterBuilder {
    private final List<LogWriterBuilder> logWriterBuilders;

    public GroupLogWriterBuilder(List<LogWriterBuilder> logWriterBuilders) {
        this.logWriterBuilders = logWriterBuilders;
    }

    @Override
    public List<LogWriter> buildLogWriters(Properties properties) {
        List<LogWriter> logWriters = new ArrayList<>();
        logWriterBuilders.forEach(logWriterBuilder -> logWriters.addAll(logWriterBuilder.buildLogWriters(properties)));
        return logWriters;
    }
}
