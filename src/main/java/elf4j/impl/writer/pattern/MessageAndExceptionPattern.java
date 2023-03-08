package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import elf4j.impl.util.StackTraceUtils;
import lombok.Value;

@Value
public class MessageAndExceptionPattern implements LogPattern {
    public static MessageAndExceptionPattern from(String pattern) {
        if (!LogPatternType.MESSAGE.isTargetOf(pattern)) {
            throw new IllegalArgumentException("pattern text: " + pattern);
        }
        return new MessageAndExceptionPattern();
    }

    @Override
    public boolean includeCallerDetail() {
        return false;
    }

    @Override
    public boolean includeCallerThread() {
        return false;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logText) {
        logText.append(logEntry.getResolvedMessage());
        if (logEntry.getException() == null) {
            return;
        }
        logText.append(System.lineSeparator()).append(StackTraceUtils.stackTraceTextOf(logEntry.getException()));
    }
}
