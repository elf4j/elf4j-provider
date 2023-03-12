package elf4j.impl.util;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

/**
 *
 */
public class StackTraceUtils {
    private StackTraceUtils() {
    }

    /**
     * @param calleeClass whose caller is being searched for
     * @return immediate caller frame of the specified callee class
     */
    public static LogEntry.StackTraceFrame callerOf(@NonNull Class<?> calleeClass) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int depth = 0;
        String calleeClassName = calleeClass.getName();
        for (; depth < stackTrace.length; depth++) {
            if (stackTrace[depth].getClassName().equals(calleeClassName)) {
                depth++;
                break;
            }
        }
        for (; depth < stackTrace.length; depth++) {
            if (!stackTrace[depth].getClassName().equals(calleeClassName)) {
                StackTraceElement stackTraceElement = stackTrace[depth];
                return LogEntry.StackTraceFrame.builder()
                        .className(stackTraceElement.getClassName())
                        .methodName(stackTraceElement.getMethodName())
                        .lineNumber(stackTraceElement.getLineNumber())
                        .fileName(stackTraceElement.getFileName())
                        .build();
            }
        }
        throw new NoSuchElementException("Caller of class '" + calleeClass + "' not found in call stack");
    }

    /**
     * @param throwable to extract stack trace text from
     * @return stack trace text of the specified throwable
     */
    public static String stackTraceTextOf(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter, true)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
