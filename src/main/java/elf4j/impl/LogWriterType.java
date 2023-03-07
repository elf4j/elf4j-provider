package elf4j.impl;

import elf4j.impl.util.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public enum LogWriterType {
    CONSOLE {
        @Override
        public List<LogWriter> parseLogWriters(Properties properties) {
            List<Map<String, String>> configurationGroup =
                    PropertiesUtils.getPropertiesGroupOfType("console", properties);
            List<LogWriter> consoleWriters = new ArrayList<>();
            configurationGroup.forEach(configuration -> consoleWriters.add(ConsoleWriter.from(configuration)));
            return consoleWriters;
        }
    };

    public abstract List<LogWriter> parseLogWriters(Properties properties);
}
