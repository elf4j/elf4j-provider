package elf4j.impl;

public class LevelPattern implements LogPattern {

    public LevelPattern() {
    }

    @Override
    public boolean isCallerFrameRequired() {
        return false;
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return false;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        logTextBuilder.append(logEntry.getNativeLogger().getLevel());
    }

}
