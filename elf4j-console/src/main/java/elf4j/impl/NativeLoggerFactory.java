package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.util.StackTraceUtils;
import elf4j.spi.LoggerFactory;

public class NativeLoggerFactory implements LoggerFactory {
    private static final Level DEFAULT_LOGGER_SEVERITY_LEVEL = Level.TRACE;
    private static final LogService LOG_SERVICE =
            new LogService(new ConfigurationService(), new WriterThreadProvider());
    private static final Class<Logger> DEFAULT_LOGGER_CLASS = Logger.class;
    private final Class<?> loggerClass;
    private final Level loggerDefaultLevel;

    /**
     * Default constructor required for {@link java.util.ServiceLoader}
     */
    public NativeLoggerFactory() {
        this(DEFAULT_LOGGER_CLASS, DEFAULT_LOGGER_SEVERITY_LEVEL);
    }

    protected NativeLoggerFactory(Class<?> loggerClass, Level loggerDefaultLevel) {
        this.loggerClass = loggerClass;
        this.loggerDefaultLevel = loggerDefaultLevel;
    }

    static LogService getLogService() {
        return LOG_SERVICE;
    }

    @Override
    public NativeLogger logger() {
        return new NativeLogger(loggerInstanceRequesterClassName(), loggerDefaultLevel, LOG_SERVICE);
    }

    private String loggerInstanceRequesterClassName() {
        return StackTraceUtils.getCaller(this.loggerClass).getClassName();
    }
}
