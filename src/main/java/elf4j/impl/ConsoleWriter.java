package elf4j.impl;

import elf4j.Level;

public class ConsoleWriter implements LogWriter {
    private static final Level DEFAULT_MINIMUM_SEVERITY_LEVEL = Level.TRACE;
    private final LogPattern layout;

    private final Level minimumLevel;

    public ConsoleWriter(String level, String pattern) {
        this(level == null ? DEFAULT_MINIMUM_SEVERITY_LEVEL : Level.valueOf(level.toUpperCase()),
                LayoutPattern.from(pattern));
    }

    public ConsoleWriter(Level minimumLevel, LogPattern layout) {
        this.layout = layout;
        this.minimumLevel = minimumLevel;
    }

    @Override
    public Level getMinimumLevel() {
        return minimumLevel;
    }

    @Override
    public void write(LogEntry logEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        layout.render(logEntry, stringBuilder);
        System.out.println(stringBuilder);
    }

    @Override
    public boolean isCallerFrameRequired() {
        return layout.isCallerFrameRequired();
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return layout.isCallerThreadInfoRequired();
    }
}
