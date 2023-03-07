package elf4j.impl.writer.pattern;

import java.util.List;
import java.util.Optional;

public enum LogPatternType {
    TIMESTAMP {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern, "timestamp").ifPresent(extractedPattern -> loggerPatterns.add(
                    TimestampPattern.from(extractedPattern)));
        }
    },
    LEVEL {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern,
                    "level").ifPresent(extractedPattern -> loggerPatterns.add(LevelPattern.from(extractedPattern)));
        }
    },
    THREAD {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern, "thread").ifPresent(extractedPattern -> loggerPatterns.add(
                    ThreadPattern.from(extractedPattern)));
        }
    },
    CLASS {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern,
                    "class").ifPresent(extractedPattern -> loggerPatterns.add(ClassPattern.from(extractedPattern)));
        }
    },
    METHOD {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern, "method").ifPresent(extractedPattern -> loggerPatterns.add(
                    MethodPattern.from(extractedPattern)));
        }
    },
    MESSAGE {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern, "message").ifPresent(extractedPattern -> loggerPatterns.add(
                    MessageAndExceptionPattern.from(extractedPattern)));
        }
    },
    JSON {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            extractNamedPatternIfLeading(pattern,
                    "json").ifPresent(extractedPattern -> loggerPatterns.add(JsonPattern.from(extractedPattern)));
        }
    },
    VERBATIM {
        @Override
        public void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns) {
            if (pattern.length() == 0) {
                return;
            }
            int end = pattern.indexOf("{");
            end = (end < 1) ? pattern.length() : end;
            loggerPatterns.add(VerbatimPattern.from(pattern.substring(0, end)));
            pattern.delete(0, end);
        }
    };

    private static Optional<String> extractNamedPatternIfLeading(StringBuilder pattern, String testPatternName) {
        int start = pattern.indexOf("{");
        if (start != 0) {
            return Optional.empty();
        }
        int end = pattern.indexOf("}");
        if (end < 0) {
            return Optional.empty();
        }
        String leadingPatternContent = pattern.substring(start + 1, end).trim();
        if (!leadingPatternContent.startsWith(testPatternName)) {
            return Optional.empty();
        }
        pattern.delete(start, end + 1);
        return Optional.of(leadingPatternContent);
    }

    public abstract void extractLeadingPattern(StringBuilder pattern, List<LogPattern> loggerPatterns);
}
