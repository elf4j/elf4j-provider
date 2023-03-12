package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.writer.PerformanceSensitive;

/**
 *
 */
public interface LogService extends PerformanceSensitive {
    /**
     * @param nativeLogger to check for enablement
     * @return true if the logger's level is at or above configured minimum
     */
    boolean isEnabled(NativeLogger nativeLogger);

    /**
     * @param nativeLogger the serviced logger
     * @param exception    to log
     * @param message      to log, can have argument placeholders
     * @param args         to replace the placeholders in the message
     */
    void log(NativeLogger nativeLogger, Throwable exception, Object message, Object[] args);
}
