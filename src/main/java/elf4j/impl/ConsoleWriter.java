package elf4j.impl;

import elf4j.Level;

import java.util.Map;

public class ConsoleWriter implements LogWriter {
    private static final Level DEFAULT_MINIMUM_LEVEL = Level.TRACE;
    private static final String DEFAULT_PATTERN =
            "{timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level} {class}: {message}";
    private final GroupLogPattern layout;

    private final Level minimumLevel;

    ConsoleWriter(Level minimumLevel, GroupLogPattern layout) {
        this.layout = layout;
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
        layout.render(logEntry, stringBuilder);
        System.out.println(stringBuilder);
    }

    @Override
    public boolean includeCallerDetail() {
        return layout.includeCallerDetail();
    }

    @Override
    public boolean includeCallerThread() {
        return layout.includeCallerThread();
    }
}
