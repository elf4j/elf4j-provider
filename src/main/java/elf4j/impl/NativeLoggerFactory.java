package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.configuration.DefaultLoggingConfiguration;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.service.DefaultLogService;
import elf4j.impl.service.LogService;
import elf4j.impl.service.WriterThreadProvider;
import elf4j.impl.util.StackTraceUtils;
import elf4j.spi.LoggerFactory;
import lombok.NonNull;

import java.util.Properties;

public class NativeLoggerFactory implements LoggerFactory {
    private static final Class<?> DEFAULT_LOGGER_CLASS = NativeLogger.class;
    private static final Class<Logger> DEFAULT_LOGGER_INTERFACE = Logger.class;
    private static final Level DEFAULT_LOGGER_SEVERITY_LEVEL = Level.TRACE;
    @NonNull private final Level loggerDefaultLevel;
    @NonNull private final Class<?> loggerInterface;
    @NonNull private final LogService logService;

    /**
     * Default constructor required by {@link java.util.ServiceLoader}
     */
    public NativeLoggerFactory() {
        this(DEFAULT_LOGGER_INTERFACE, DEFAULT_LOGGER_CLASS);
    }

    public NativeLoggerFactory(@NonNull Class<?> loggerInterface, @NonNull Class<?> loggerClass) {
        this(loggerInterface,
                loggerClass,
                DEFAULT_LOGGER_SEVERITY_LEVEL,
                ConfigurationInstanceHolder.INSTANCE,
                new WriterThreadProvider());
    }

    NativeLoggerFactory(@NonNull Class<?> loggerInterface,
            @NonNull Class<?> loggerClass,
            @NonNull Level loggerDefaultLevel,
            @NonNull LoggingConfiguration loggingConfiguration,
            @NonNull WriterThreadProvider writerThreadProvider) {
        this.loggerDefaultLevel = loggerDefaultLevel;
        this.loggerInterface = loggerInterface;
        this.logService = new DefaultLogService(loggerClass, loggingConfiguration, writerThreadProvider);
    }

    public static void refreshConfiguration() {
        refreshConfiguration(null);
    }

    public static void refreshConfiguration(Properties properties) {
        ConfigurationInstanceHolder.INSTANCE.refresh(properties);
    }

    @Override
    public NativeLogger logger() {
        return new NativeLogger(getLoggerOwnerClassName(), loggerDefaultLevel, logService);
    }

    private String getLoggerOwnerClassName() {
        return StackTraceUtils.callerOf(this.loggerInterface).getClassName();
    }

    private static class ConfigurationInstanceHolder {
        private static final LoggingConfiguration INSTANCE = new DefaultLoggingConfiguration();
    }
}
