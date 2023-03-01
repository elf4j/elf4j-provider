package elf4j.impl;

public interface LogPattern {
    void render(LogEntry logEntry, StringBuilder stringBuilder);
}
