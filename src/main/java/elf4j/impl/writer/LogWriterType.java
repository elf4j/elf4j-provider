package elf4j.impl.writer;

import elf4j.impl.util.PropertiesUtils;

import java.util.*;

public enum LogWriterType {
    CONSOLE {
        @Override
        Set<LogWriter> parseLogWriters(Properties properties) {
            List<Map<String, String>> configurationGroup =
                    PropertiesUtils.getPropertiesGroupOfType("console", properties);
            Set<LogWriter> consoleWriters = new HashSet<>();
            configurationGroup.forEach(configuration -> consoleWriters.add(ConsoleWriter.from(configuration)));
            return consoleWriters;
        }
    };

    public static Set<LogWriter> parseAllLogWriters(Properties properties) {
        Set<LogWriter> logWriters = new HashSet<>();
        EnumSet.allOf(LogWriterType.class).forEach(type -> logWriters.addAll(type.parseLogWriters(properties)));
        return logWriters;
    }

    abstract Set<LogWriter> parseLogWriters(Properties properties);
}
