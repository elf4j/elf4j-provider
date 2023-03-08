package elf4j.impl.writer.pattern;

import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Objects;

public enum LogPatternType {
    TIMESTAMP {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? TimestampPattern.from(pattern) : null;
        }
    },
    LEVEL {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? LevelPattern.from(pattern) : null;
        }
    },
    THREAD {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? ThreadPattern.from(pattern) : null;
        }
    },
    CLASS {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? ClassPattern.from(pattern) : null;
        }
    },
    METHOD {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? MethodPattern.from(pattern) : null;
        }
    },
    MESSAGE {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? MessageAndExceptionPattern.from(pattern) : null;
        }
    },
    JSON {
        @Override
        public LogPattern parsePattern(String pattern) {
            return isPatternOfType(pattern, this) ? JsonPattern.from(pattern) : null;
        }
    },
    VERBATIM {
        @Override
        public LogPattern parsePattern(String pattern) {
            return VerbatimPattern.from(pattern);
        }
    };

    public static LogPattern getLogPattern(String pattern) {
        return EnumSet.allOf(LogPatternType.class)
                .stream()
                .map(type -> type.parsePattern(pattern))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static boolean isPatternOfType(String pattern, LogPatternType targetPatternType) {
        return targetPatternType.name().equalsIgnoreCase(pattern.split(":", 2)[0].trim());
    }

    abstract LogPattern parsePattern(String pattern);
}
