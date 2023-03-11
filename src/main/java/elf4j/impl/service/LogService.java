package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.writer.PerformanceSensitive;

public interface LogService extends PerformanceSensitive {
    boolean isEnabled(NativeLogger nativeLogger);

    void log(NativeLogger nativeLogger, Throwable exception, Object message, Object[] args);
}
