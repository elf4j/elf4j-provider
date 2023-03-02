package elf4j.impl;

import elf4j.impl.util.StackTraceFrame;
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
    String callerThreadName;
    long callerThreadId;

    public static LogEntry.LogEntryBuilder newBuilder(boolean callerThreadRequired) {
        if (!callerThreadRequired) {
            return builder();
        }
        Thread callerThread = Thread.currentThread();
        return builder().callerThreadName(callerThread.getName()).callerThreadId(callerThread.getId());
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
    public static class ThreadInformation {
        String name;
        long id;
    }
}
