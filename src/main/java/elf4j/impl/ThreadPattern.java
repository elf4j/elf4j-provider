package elf4j.impl;

import lombok.Value;

@Value
public class ThreadPattern implements LogPattern {
    LogOption threadLogOption;

    @Override
    public boolean isCallerFrameRequired() {
        return false;
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return true;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        logTextBuilder.append(threadLogOption == LogOption.ID ? logEntry.getCallerThread().getId() :
                logEntry.getCallerThread().getName());
    }

    enum LogOption {
        ID,
        NAME
    }
}
