package elf4j.impl;

import elf4j.impl.util.CallFrame;
import elf4j.impl.util.LogEntryUtils;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LogEntry {
    NativeLogger nativeLogger;
    Object message;
    Object[] arguments;
    Throwable exception;
    CallFrame callerFrame;

    String callerThreadName;

    Long callerThreadId;

    public String getResolvedMessage() {
        return LogEntryUtils.resolve(this.message, this.arguments);
    }
}
