package elf4j.impl;

public class VerbatimPattern implements LogPattern {
    private final String text;

    public VerbatimPattern(String text) {
        this.text = text;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder stringBuilder) {
        stringBuilder.append(text);
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return false;
    }

    @Override
    public boolean isCallerFrameRequired() {
        return false;
    }
}
