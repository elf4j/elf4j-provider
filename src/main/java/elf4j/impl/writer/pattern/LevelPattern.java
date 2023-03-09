package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

@Value
public class LevelPattern implements LogPattern {
    private static final int DISPLAY_LENGTH_UNSET = -1;
    int displayLength;

    private LevelPattern(int displayLength) {
        this.displayLength = displayLength;
    }

    public static LevelPattern from(@NonNull String pattern) {
        if (!LogPatternType.LEVEL.isTargetOf(pattern)) {
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
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        String level = logEntry.getNativeLogger().getLevel().name();
        if (displayLength == DISPLAY_LENGTH_UNSET) {
            logTextBuilder.append(level);
            return;
        }
        char[] levelChars = level.toCharArray();
        for (int i = 0; i < displayLength; i++) {
            logTextBuilder.append(i < levelChars.length ? levelChars[i] : ' ');
        }
    }
}
