package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.pattern.GroupLogPattern;
import elf4j.impl.writer.pattern.LogPattern;

import java.util.Map;

public class ConsoleWriter implements LogWriter {
    private static final Level DEFAULT_MINIMUM_LEVEL = Level.TRACE;
    private static final String DEFAULT_PATTERN =
            "{timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level} {class}: {message}";
    private final LogPattern logPattern;

    private final Level minimumLevel;

    ConsoleWriter(Level minimumLevel, GroupLogPattern logPattern) {
        this.logPattern = logPattern;
        this.minimumLevel = minimumLevel;
    }

    public static ConsoleWriter from(Map<String, String> configuration) {
        String level = configuration.get("level");
        String pattern = configuration.get("pattern");
        return new ConsoleWriter(level == null ? DEFAULT_MINIMUM_LEVEL : Level.valueOf(level.toUpperCase()),
                GroupLogPattern.from(pattern == null ? DEFAULT_PATTERN : pattern));
    }

    @Override
    public Level getMinimumLevel() {
        return minimumLevel;
    }

    @Override
    public void write(LogEntry logEntry) {
        if (this.minimumLevel.ordinal() > logEntry.getNativeLogger().getLevel().ordinal()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        logPattern.render(logEntry, stringBuilder);
        System.out.println(stringBuilder);
    }

    @Override
    public boolean includeCallerDetail() {
        return logPattern.includeCallerDetail();
    }

    @Override
    public boolean includeCallerThread() {
        return logPattern.includeCallerThread();
    }
}
