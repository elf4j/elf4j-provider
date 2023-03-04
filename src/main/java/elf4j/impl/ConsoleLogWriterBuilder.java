package elf4j.impl;

import elf4j.impl.util.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsoleLogWriterBuilder implements LogWriterBuilder {
    @Override
    public List<LogWriter> buildLogWriters(Properties properties) {
        List<Map<String, String>> consolePropertiesGroup =
                PropertiesUtils.getPropertiesGroupOfType("console", properties);
        List<LogWriter> consoleWriters = new ArrayList<>();
        consolePropertiesGroup.forEach(consoleProperties -> {
            consoleWriters.add(new ConsoleWriter(consoleProperties.get("level"), consoleProperties.get("pattern")));
        });
        return consoleWriters;
    }
}
