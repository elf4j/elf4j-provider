package elf4j.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public enum LogPatternElementType {
    TIMESTAMP {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            Optional<String> content = parseAndTrimLeadingNamedPatternContent("timestamp", pattern);
            if (!content.isPresent()) {
                return Optional.empty();
            }
            DateTimeFormatter dateTimeFormatter = getPatternOption(content.get()).map(DateTimeFormatter::ofPattern)
                    .orElse(DEFAULT_TIMESTAMP_FORMATTER);
            if (dateTimeFormatter.getZone() == null) {
                dateTimeFormatter = dateTimeFormatter.withZone(DEFAULT_TIMESTAMP_ZONE);
            }
            return Optional.of(new TimestampPattern(dateTimeFormatter));
        }
    },
    LEVEL {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return parseAndTrimLeadingNamedPatternContent("level", pattern).map(content -> new LevelPattern());
        }
    },
    THREAD {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return parseAndTrimLeadingNamedPatternContent("thread", pattern).map(content -> new ThreadPattern(
                    getPatternOption(content).map(option -> ThreadPattern.LogOption.valueOf(option.toUpperCase()))
                            .orElse(ThreadPattern.LogOption.NAME)));
        }
    },
    CLASS {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return null;
        }
    },
    METHOD {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return null;
        }
    },
    MESSAGE {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return null;
        }
    },

    UNKNOWN {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            return null;
        }
    },
    VERBATIM {
        @Override
        public Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern) {
            if (pattern.length() == 0) {
                return Optional.empty();
            }
            int end = pattern.indexOf(CURLY_BRACE_OPEN);
            end = (end < 1) ? pattern.length() : end;
            VerbatimPattern verbatimPatternEntry = new VerbatimPattern(pattern.substring(0, end));
            pattern.delete(0, end);
            return Optional.of(verbatimPatternEntry);
        }
    };

    private static final String CURLY_BRACE_OPEN = "{";
    private static final String CURLY_BRACE_CLOSE = "}";
    private static final DateTimeFormatter DEFAULT_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
    private static final ZoneId DEFAULT_TIMESTAMP_ZONE = ZoneId.systemDefault();

    private static Optional<String> getPatternOption(String patternContent) {
        String[] contentEntries = patternContent.split(":", 2);
        return contentEntries.length == 1 ? Optional.empty() : Optional.of(contentEntries[1].trim());
    }

    private static Optional<String> parseAndTrimLeadingNamedPatternContent(String leadingPatternName,
            StringBuilder pattern) {
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

    public abstract Optional<LogPattern> parseAndTrimLeadingPattern(StringBuilder pattern);
} //todo
