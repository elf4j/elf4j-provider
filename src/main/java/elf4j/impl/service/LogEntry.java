package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.util.StackTraceUtils;
import elf4j.impl.writer.PerformanceSensitive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@Value
@Builder
@EqualsAndHashCode
public class LogEntry {
    private static final int ADDITIONAL_STRING_BUILDER_CAPACITY = 32;
    @NonNull NativeLogger nativeLogger;
    @EqualsAndHashCode.Exclude Instant timestamp = Instant.now();
    @Nullable Object message;
    @Nullable Object[] arguments;
    @Nullable Throwable exception;
    @Nullable StackTraceFrame callerFrame;
    @Nullable ThreadInformation callerThread;

    public static LogEntry.LogEntryBuilder newBuilder(PerformanceSensitive performanceSensitive, Class<?> loggerClass) {
        LogEntryBuilder builder = builder();
        if (performanceSensitive.includeCallerDetail()) {
            builder.callerFrame(StackTraceUtils.callerOf(loggerClass));
        }
        if (performanceSensitive.includeCallerThread()) {
            Thread callerThread = Thread.currentThread();
            builder.callerThread(new ThreadInformation(callerThread.getName(), callerThread.getId()));
        }
        return builder;
    }

    private static String resolve(Object msg, Object[] arguments) {
        String message = Objects.toString(supply(msg));
        int messageLength = message.length();
        StringBuilder builder = new StringBuilder(messageLength + ADDITIONAL_STRING_BUILDER_CAPACITY);
        int i = 0;
        int j = 0;
        while (i < messageLength) {
            char character = message.charAt(i);
            if (character == '{' && ((i + 1) < messageLength && message.charAt(i + 1) == '}') && j < arguments.length) {
                builder.append(supply(arguments[j++]));
                i += 2;
            } else {
                builder.append(character);
                i += 1;
            }
        }
        return builder.toString();
    }

    private static Object supply(Object o) {
        return o instanceof Supplier<?> ? ((Supplier<?>) o).get() : o;
    }

    public String getCallerClassName() {
        return callerFrame == null ? nativeLogger.getName() : callerFrame.getClassName();
    }

    public String getResolvedMessage() {
        return resolve(this.message, this.arguments);
    }

    @Value
    @Builder
    public static class StackTraceFrame {
        String className;
        String methodName;
        int lineNumber;
        String fileName;
    }

    @Value
    @Builder
    public static class ThreadInformation {
        String name;
        long id;
    }
}
