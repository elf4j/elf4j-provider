package elf4j.impl;

public class ConsoleWriter implements LogWriter {
    private final LogPattern layout;

    public ConsoleWriter(LogPattern layout) {
        this.layout = layout;
    }

    @Override
    public boolean isCallerFrameRequired() {
        return layout.isCallerFrameRequired();
    }

    @Override
    public boolean isCallerThreadInfoRequired() {
        return layout.isCallerThreadInfoRequired();
    }

    @Override
    public void write(LogEntry logEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        layout.render(logEntry, stringBuilder);
        System.out.println(stringBuilder);
    }
}
