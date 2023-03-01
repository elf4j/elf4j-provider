package elf4j.impl.util;

import java.util.Objects;
import java.util.function.Supplier;

public class LogEntryUtils {
    private static final int ADDITIONAL_STRING_BUILDER_CAPACITY = 32;

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
}