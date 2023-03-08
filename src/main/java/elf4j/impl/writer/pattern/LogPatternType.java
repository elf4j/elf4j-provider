package elf4j.impl.writer.pattern;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public enum LogPatternType {
    TIMESTAMP {
        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? TimestampPattern.from(pattern) : null;
        }

        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }
    },
    LEVEL {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? LevelPattern.from(pattern) : null;
        }
    },
    THREAD {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? ThreadPattern.from(pattern) : null;
        }
    },
    CLASS {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? ClassPattern.from(pattern) : null;
        }
    },
    METHOD {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? MethodPattern.from(pattern) : null;
        }
    },
    MESSAGE {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? MessageAndExceptionPattern.from(pattern) : null;
        }
    },
    JSON {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? JsonPattern.from(pattern) : null;
        }
    },
    VERBATIM {
        @Override
        public boolean isTargetOf(String pattern) {
            return isPatternOfType(this, pattern);
        }

        @Override
        LogPattern parsePattern(String pattern) {
            return this.isTargetOf(pattern) ? VerbatimPattern.from(pattern) : null;
        }
    };

    public static List<LogPattern> parseAllPatternsOrThrow(String pattern) {
        List<LogPattern> logPatterns = new ArrayList<>();
        int length = pattern.length();
        int i = 0;
        while (i < length) {
            String iPattern;
            char character = pattern.charAt(i);
            int iEnd = pattern.indexOf('}', i);
            if (character == '{' && iEnd != -1) {
                iPattern = pattern.substring(i + 1, iEnd);
                i = iEnd + 1;
            } else {
                if (iEnd == -1) {
                    iEnd = length;
                } else {
                    iEnd = pattern.indexOf('{', i);
                    if (iEnd == -1) {
                        iEnd = length;
                    }
                }
                iPattern = pattern.substring(i, iEnd);
                i = iEnd;
            }
            logPatterns.add(LogPatternType.parsePatternOrThrow(iPattern));
        }
        return logPatterns;
    }

    private static boolean isPatternOfType(LogPatternType targetPatternType, String pattern) {
        if (targetPatternType == VERBATIM) {
            return EnumSet.complementOf(EnumSet.of(VERBATIM)).stream().noneMatch(type -> type.isTargetOf(pattern));
        }
        return targetPatternType.name().equalsIgnoreCase(pattern.split(":", 2)[0].trim());
    }

    private static LogPattern parsePatternOrThrow(String pattern) {
        return EnumSet.allOf(LogPatternType.class)
                .stream()
                .map(type -> type.parsePattern(pattern))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("pattern: '" + pattern + "' not parsable"));
    }

    public abstract boolean isTargetOf(String pattern);

    abstract LogPattern parsePattern(String pattern);
}
