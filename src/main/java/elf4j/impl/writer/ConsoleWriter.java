package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.pattern.GroupLogPattern;
import elf4j.impl.writer.pattern.LogPattern;

import java.util.Map;

public class ConsoleWriter implements LogWriter {
    public static final OutStreamType DEFAULT_OUT_STREAM = OutStreamType.AUTO;
    private static final Level DEFAULT_MINIMUM_LEVEL = Level.TRACE;
    private static final String DEFAULT_PATTERN =
            "{timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level} {class} - {message}";
    private final LogPattern logPattern;

    private final Level minimumLevel;

    private final OutStreamType outStreamType;

    private ConsoleWriter(Level minimumLevel, GroupLogPattern logPattern, OutStreamType outStreamType) {
        this.logPattern = logPattern;
        this.minimumLevel = minimumLevel;
        this.outStreamType = outStreamType;
    }

    public static ConsoleWriter defaultWriter() {
        return new ConsoleWriter(DEFAULT_MINIMUM_LEVEL, GroupLogPattern.from(DEFAULT_PATTERN), DEFAULT_OUT_STREAM);
    }

    public static ConsoleWriter from(Map<String, String> configuration) {
        String level = configuration.get("level");
        String pattern = configuration.get("pattern");
        String stream = configuration.get("stream");
        return new ConsoleWriter(level == null ? DEFAULT_MINIMUM_LEVEL : Level.valueOf(level.toUpperCase()),
                GroupLogPattern.from(pattern == null ? DEFAULT_PATTERN : pattern),
                stream == null ? DEFAULT_OUT_STREAM : OutStreamType.valueOf(stream.toUpperCase()));
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
        switch (this.outStreamType) {
            case STDOUT:
                System.out.println(stringBuilder);
                return;
            case STDERR:
                System.err.println(stringBuilder);
                return;
            case AUTO:
                if (logEntry.getNativeLogger().getLevel().ordinal() < Level.WARN.ordinal()) {
                    System.out.println(stringBuilder);
                } else {
                    System.err.println(stringBuilder);
                }
                return;
            default:
                throw new IllegalStateException("Unknown out stream type: " + this.outStreamType);
        }
    }

    @Override
    public boolean includeCallerDetail() {
        return logPattern.includeCallerDetail();
    }

    @Override
    public boolean includeCallerThread() {
        return logPattern.includeCallerThread();
    }

    enum OutStreamType {
        STDOUT,
        AUTO,
        STDERR
    }
}
