package elf4j.impl;

import com.google.gson.Gson;
import elf4j.impl.util.StackTraceUtils;
import lombok.Builder;
import lombok.Value;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
public class JsonPattern implements LogPattern {
    Boolean includeCallerThread;
    Boolean includeCallerDetail;
    Gson gson = new Gson();

    public static JsonPattern from(String pattern) {
        Optional<String> patternOption = LogPattern.getPatternOption(pattern);
        if (!patternOption.isPresent()) {
            return new JsonPattern(false, false);
        }
        Set<String> options = Arrays.stream(patternOption.get().split(",")).collect(Collectors.toSet());
        return JsonPattern.builder()
                .includeCallerThread(options.contains("caller-thread"))
                .includeCallerDetail(options.contains("caller-detail"))
                .build();
    }

    @Override
    public boolean includeCallerDetail() {
        return this.includeCallerDetail;
    }

    @Override
    public boolean includeCallerThread() {
        return this.includeCallerThread;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logText) {
        gson.toJson(JsonLogEntry.from(logEntry), logText);
    }

    @Value
    @Builder
    static class JsonLogEntry {
        static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());
        String timestamp;
        String level;
        LogEntry.ThreadInformation callerThread;
        String callerClass;
        LogEntry.StackTraceFrame callerDetail;
        String message;
        String exception;

        static JsonLogEntry from(LogEntry logEntry) {
            return JsonLogEntry.builder()
                    .timestamp(DATE_TIME_FORMATTER.format(logEntry.getTimestamp()))
                    .callerClass(logEntry.getCallerClassName())
                    .level(logEntry.getNativeLogger().getLevel().name())
                    .callerThread(logEntry.getCallerThread())
                    .callerDetail(logEntry.getCallerFrame())
                    .message(logEntry.getResolvedMessage())
                    .exception(logEntry.getException() == null ? null :
                            StackTraceUtils.stackTraceTextOf(logEntry.getException()))
                    .build();
        }
    }
}
