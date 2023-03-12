package elf4j.impl.writer;

import elf4j.Level;
import elf4j.impl.service.LogEntry;
import elf4j.impl.writer.pattern.GroupLogPattern;
import elf4j.impl.writer.pattern.LogPattern;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.util.Map;

/**
 *
 */
public class ConsoleWriter implements LogWriter {
    private static final Level DEFAULT_MINIMUM_LEVEL = Level.TRACE;
    private static final OutStreamType DEFAULT_OUT_STREAM = OutStreamType.STDOUT;
    private static final String DEFAULT_PATTERN =
            "{timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level} {class} - {message}";
    private static final PrintStream STREAM_ERR = new PrintStream(System.err, false);
    private static final PrintStream STREAM_OUT = new PrintStream(System.out, false);
    private final LogPattern logPattern;
    private final Level minimumLevel;
    private final OutStreamType outStreamType;

    private ConsoleWriter(Level minimumLevel, GroupLogPattern logPattern, OutStreamType outStreamType) {
        this.logPattern = logPattern;
        this.minimumLevel = minimumLevel;
        this.outStreamType = outStreamType;
    }

    /**
     * @return default writer
     */
    public static ConsoleWriter defaultWriter() {
        return new ConsoleWriter(DEFAULT_MINIMUM_LEVEL, GroupLogPattern.from(DEFAULT_PATTERN), DEFAULT_OUT_STREAM);
    }

    /**
     * @param configuration properties map to make a console writer
     * @param outStream     out stream type, either stdout or stderr
     * @return console writer per the specified configuration
     */
    public static ConsoleWriter from(Map<String, String> configuration, @Nullable String outStream) {
        String level = configuration.get("level");
        String pattern = configuration.get("pattern");
        return new ConsoleWriter(level == null ? DEFAULT_MINIMUM_LEVEL : Level.valueOf(level.toUpperCase()),
                GroupLogPattern.from(pattern == null ? DEFAULT_PATTERN : pattern),
                outStream == null ? DEFAULT_OUT_STREAM : OutStreamType.valueOf(outStream.trim().toUpperCase()));
    }

    private static void flushErr(StringBuilder logTextBuilder) {
        STREAM_ERR.println(logTextBuilder);
        STREAM_ERR.flush();
    }

    private static void flushOut(StringBuilder logTextBuilder) {
        STREAM_OUT.println(logTextBuilder);
        STREAM_OUT.flush();
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
        StringBuilder logTextBuilder = new StringBuilder();
        logPattern.render(logEntry, logTextBuilder);
        switch (this.outStreamType) {
            case STDOUT:
                flushOut(logTextBuilder);
                return;
            case STDERR:
                flushErr(logTextBuilder);
                return;
            default:
                throw new IllegalArgumentException("Unsupported out stream type: " + this.outStreamType);
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
        STDERR
    }
}
