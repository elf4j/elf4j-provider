package elf4j.impl.writer.pattern;

import java.util.List;
import java.util.Optional;

public enum LogPatternType {
    TIMESTAMP {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("timestamp", pattern).ifPresent(leadingPattern -> loggerPatterns.add(
                    TimestampPattern.from(leadingPattern)));
        }
    },
    LEVEL {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("level",
                    pattern).ifPresent(leadingPattern -> loggerPatterns.add(LevelPattern.from(leadingPattern)));
        }
    },
    THREAD {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("thread",
                    pattern).ifPresent(leadingPattern -> loggerPatterns.add(ThreadPattern.from(leadingPattern)));
        }
    },
    CLASS {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("class",
                    pattern).ifPresent(leadingPattern -> loggerPatterns.add(ClassPattern.from(leadingPattern)));
        }
    },
    METHOD {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("method",
                    pattern).ifPresent(leadingPattern -> loggerPatterns.add(MethodPattern.from(leadingPattern)));
        }
    },
    MESSAGE {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("message", pattern).ifPresent(leadingPattern -> loggerPatterns.add(
                    MessageAndExceptionPattern.from(leadingPattern)));
        }
    },
    JSON {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedLeadingPattern("json", pattern).ifPresent(leadingPattern -> loggerPatterns.add(JsonPattern.from(
                    leadingPattern)));
        }
    },
    VERBATIM {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            if (pattern.length() == 0) {
                return;
            }
            int end = pattern.indexOf(CURLY_BRACE_OPEN);
            end = (end < 1) ? pattern.length() : end;
            loggerPatterns.add(VerbatimPattern.from(pattern.substring(0, end)));
            pattern.delete(0, end);
        }
    };

    private static final String CURLY_BRACE_CLOSE = "}";
    private static final String CURLY_BRACE_OPEN = "{";

    private static Optional<String> extractNamedLeadingPattern(String leadingPatternName, StringBuilder pattern) {
        int start = pattern.indexOf(CURLY_BRACE_OPEN);
        if (start != 0) {
            return Optional.empty();
        }
        int end = pattern.indexOf(CURLY_BRACE_CLOSE);
        if (end < 0) {
            return Optional.empty();
        }
        String content = pattern.substring(start + 1, end).trim();
        if (!content.startsWith(leadingPatternName)) {
            return Optional.empty();
        }
        pattern.delete(start, end + 1);
        return Optional.of(content);
    }

    public abstract void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns);
}
