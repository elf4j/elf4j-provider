package elf4j.impl.writer.pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elf4j.impl.service.LogEntry;
import elf4j.impl.util.StackTraceUtils;
import lombok.Builder;
import lombok.NonNull;
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
    boolean includeCallerThread;
    boolean includeCallerDetail;
    Gson gson;

    public static JsonPattern from(@NonNull String pattern) {
        if (!LogPatternType.isPatternOfType(pattern, LogPatternType.JSON)) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        Optional<String> patternOption = LogPattern.getPatternOption(pattern);
        if (!patternOption.isPresent()) {
            return JsonPattern.builder()
                    .includeCallerThread(false)
                    .includeCallerDetail(false)
                    .gson(new GsonBuilder().setPrettyPrinting().create())
                    .build();
        }
        Set<String> options =
                Arrays.stream(patternOption.get().split(",")).map(String::trim).collect(Collectors.toSet());
        return JsonPattern.builder()
                .includeCallerThread(options.contains("caller-thread"))
                .includeCallerDetail(options.contains("caller-detail"))
                .gson(options.contains("minify") ? new Gson() : new GsonBuilder().setPrettyPrinting().create())
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
        gson.toJson(JsonLogEntry.from(logEntry, this), logText);
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

        static JsonLogEntry from(LogEntry logEntry, JsonPattern jsonPattern) {
            return JsonLogEntry.builder()
                    .timestamp(DATE_TIME_FORMATTER.format(logEntry.getTimestamp()))
                    .callerClass(logEntry.getCallerClassName())
                    .level(logEntry.getNativeLogger().getLevel().name())
                    .callerThread(jsonPattern.includeCallerThread ? logEntry.getCallerThread() : null)
                    .callerDetail(jsonPattern.includeCallerDetail ? logEntry.getCallerFrame() : null)
                    .message(logEntry.getResolvedMessage())
                    .exception(logEntry.getException() == null ? null :
                            StackTraceUtils.stackTraceTextOf(logEntry.getException()))
                    .build();
        }
    }
}
