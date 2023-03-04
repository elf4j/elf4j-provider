package elf4j.impl;

public class VerbatimPattern implements LogPattern {
    private final String text;

    public VerbatimPattern(String text) {
        this.text = text;
    }

    public static VerbatimPattern from(String pattern) {
        return new VerbatimPattern(pattern);
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
        logTextBuilder.append(text);
    }
}
