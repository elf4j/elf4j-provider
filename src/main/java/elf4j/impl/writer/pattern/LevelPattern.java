package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.Value;

@Value
public class LevelPattern implements LogPattern {
    private static final int DISPLAY_LENGTH_UNSET = -1;
    int displayLength;

    private LevelPattern(int displayLength) {
        this.displayLength = displayLength;
    }

    public static LevelPattern from(String pattern) {
        if (!pattern.startsWith("level")) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        return new LevelPattern(LogPattern.getPatternOption(pattern)
                .map(Integer::parseInt)
                .orElse(DISPLAY_LENGTH_UNSET));
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
        String level = logEntry.getNativeLogger().getLevel().name();
        if (displayLength == DISPLAY_LENGTH_UNSET) {
            logText.append(level);
            return;
        }
        char[] levelChars = level.toCharArray();
        for (int i = 0; i < displayLength; i++) {
            logText.append(i < levelChars.length ? levelChars[i] : ' ');
        }
    }
}
