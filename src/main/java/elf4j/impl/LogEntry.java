package elf4j.impl;

import elf4j.impl.util.StackTraceUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@Value
@Builder
@EqualsAndHashCode
public class LogEntry {
    private static final int ADDITIONAL_STRING_BUILDER_CAPACITY = 32;
    NativeLogger nativeLogger;
    @EqualsAndHashCode.Exclude Instant timestamp = Instant.now();
    Object message;
    Object[] arguments;
    Throwable exception;
    StackTraceFrame callerFrame;
    ThreadInformation callerThread;

    public static LogEntry.LogEntryBuilder newBuilder(PerformanceSensitive performanceSensitive, Class<?> loggerClass) {
        LogEntryBuilder builder = builder();
        if (performanceSensitive.isCallerFrameRequired()) {
            builder.callerFrame(StackTraceUtils.callerOf(loggerClass));
        }
        if (performanceSensitive.isCallerThreadInfoRequired()) {
            Thread callerThread = Thread.currentThread();
            builder.callerThread(new ThreadInformation(callerThread.getName(), callerThread.getId()));
        }
        return builder;
    }

    public String getCallerClassName() {
        return callerFrame == null ? nativeLogger.getName() : callerFrame.getClassName();
    }

    public String getResolvedMessage() {
        return resolve(this.message, this.arguments);
    }

    public static String resolve(Object msg, Object[] arguments) {
        String message = Objects.toString(supply(msg));
        int length = message.length();
        StringBuilder builder = new StringBuilder(length + ADDITIONAL_STRING_BUILDER_CAPACITY);
        int argumentIndex = 0;
        for (int index = 0; index < length; ++index) {
            char character = message.charAt(index);
            if (character == '{' && index + 1 < length && message.charAt(index + 1) == '}'
                    && argumentIndex < arguments.length) {
                builder.append(supply(arguments[argumentIndex++]));
                index += 1;
            } else {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    private static Object supply(Object o) {
        return o instanceof Supplier<?> ? ((Supplier<?>) o).get() : o;
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
