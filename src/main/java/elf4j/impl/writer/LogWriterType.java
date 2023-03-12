package elf4j.impl.writer;

import elf4j.impl.util.PropertiesUtils;

import java.util.*;

/**
 *
 */
public enum LogWriterType {
    /**
     *
     */
    CONSOLE {
        @Override
        List<LogWriter> parseLogWriters(Properties properties) {
            List<Map<String, String>> configurationGroup =
                    PropertiesUtils.getPropertiesGroupOfType("console", properties);
            List<LogWriter> consoleWriters = new ArrayList<>();
            configurationGroup.forEach(configuration -> consoleWriters.add(ConsoleWriter.from(configuration,
                    properties.getProperty("console.out.stream"))));
            return consoleWriters;
        }
    };

    /**
     * @param properties configuration source
     * @return all writers parsed from the specified properties
     */
    public static List<LogWriter> parseAllLogWriters(Properties properties) {
        List<LogWriter> logWriters = new ArrayList<>();
        EnumSet.allOf(LogWriterType.class).forEach(type -> logWriters.addAll(type.parseLogWriters(properties)));
        return logWriters;
    }

    abstract List<LogWriter> parseLogWriters(Properties properties);
}
