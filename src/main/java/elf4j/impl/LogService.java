package elf4j.impl;

public interface LogService {
    boolean isEnabled(NativeLogger nativeLogger);

    void log(NativeLogger nativeLogger, Throwable exception, Object message, Object[] args);
}
