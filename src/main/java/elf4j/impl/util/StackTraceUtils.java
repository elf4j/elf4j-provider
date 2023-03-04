package elf4j.impl.util;

import elf4j.impl.LogEntry;

import java.util.NoSuchElementException;

public class StackTraceUtils {
    public static LogEntry.StackTraceFrame callerOf(Class<?> calleeClass) {
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
}
