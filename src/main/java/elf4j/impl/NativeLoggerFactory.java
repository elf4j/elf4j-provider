package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.util.StackTraceUtils;
import elf4j.spi.LoggerFactory;

public class NativeLoggerFactory implements LoggerFactory {
    private static final Level DEFAULT_LOGGER_SEVERITY_LEVEL = Level.TRACE;
    private static final ConfigurationService CONFIGURATION_SERVICE = new ConfigurationService();
    private static final WriterThreadProvider WRITER_THREAD_PROVIDER = new WriterThreadProvider();
    private static final Class<?> DEFAULT_LOGGER_CLASS = Logger.class;
    private final LogService logService;
    private final Class<?> loggerClass;
    private final Level loggerDefaultLevel;

    /**
     * Default constructor required for {@link java.util.ServiceLoader}
     */
    public NativeLoggerFactory() {
        this(DEFAULT_LOGGER_SEVERITY_LEVEL, DEFAULT_LOGGER_CLASS);
    }

    public NativeLoggerFactory(Level loggerDefaultLevel, Class<?> loggerClass) {
        this.loggerClass = loggerClass;
        this.loggerDefaultLevel = loggerDefaultLevel;
        this.logService = new LogService(loggerClass, CONFIGURATION_SERVICE, WRITER_THREAD_PROVIDER);
    }

    @Override
    public NativeLogger logger() {
        return new NativeLogger(loggerInstanceRequesterClassName(), loggerDefaultLevel, logService);
    }

    LogService getLogService() {
        return this.logService;
    }

    private String loggerInstanceRequesterClassName() {
        return StackTraceUtils.callerOf(this.loggerClass).getClassName();
    }
}
