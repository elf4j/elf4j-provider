package elf4j.impl.service;

import elf4j.impl.NativeLogger;

public interface LogService {
    boolean isEnabled(NativeLogger nativeLogger);

    void log(NativeLogger nativeLogger, Throwable exception, Object message, Object[] args);
}
