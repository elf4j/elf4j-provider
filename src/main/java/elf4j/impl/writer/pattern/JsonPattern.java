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
    private static final String CALLER_DETAIL = "caller-detail";
    private static final String CALLER_THREAD = "caller-thread";
    private static final String MINIFY = "minify";
    private static final Set<String> DISPLAY_OPTIONS =
            Arrays.stream(new String[] { CALLER_THREAD, CALLER_DETAIL, MINIFY }).collect(Collectors.toSet());
    boolean includeCallerThread;
    boolean includeCallerDetail;
    Gson gson;

    public static JsonPattern from(@NonNull String pattern) {
        if (!LogPatternType.JSON.isTargetOf(pattern)) {
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
        if (!DISPLAY_OPTIONS.containsAll(options)) {
            throw new IllegalArgumentException("Invalid JSON display option inside: " + options);
        }
        return JsonPattern.builder()
                .includeCallerThread(options.contains(CALLER_THREAD))
                .includeCallerDetail(options.contains(CALLER_DETAIL))
                .gson(options.contains(MINIFY) ? new Gson() : new GsonBuilder().setPrettyPrinting().create())
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
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        gson.toJson(JsonLogEntry.from(logEntry, this), logTextBuilder);
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
                    .callerClass(jsonPattern.includeCallerDetail ? null : logEntry.getCallerClassName())
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
