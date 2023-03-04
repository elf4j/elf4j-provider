package elf4j.impl;

public interface LogPattern extends PerformanceSensitive {
    void render(LogEntry logEntry, StringBuilder logTextBuilder);
}
