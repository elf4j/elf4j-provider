package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.util.StackTraceUtils;
import elf4j.spi.LoggerFactory;
import lombok.NonNull;

import java.util.Properties;

public class NativeLoggerFactory implements LoggerFactory {
    private static final Class<?> DEFAULT_LOGGER_CLASS = NativeLogger.class;
    private static final Class<Logger> DEFAULT_LOGGER_INTERFACE = Logger.class;
    private static final Level DEFAULT_LOGGER_SEVERITY_LEVEL = Level.TRACE;
    @NonNull private final LogService logService;
    @NonNull private final Level loggerDefaultLevel;
    @NonNull private final Class<?> loggerInterface;

    /**
     * Default constructor required by {@link java.util.ServiceLoader}
     */
    public NativeLoggerFactory() {
        this(DEFAULT_LOGGER_INTERFACE,
                DEFAULT_LOGGER_CLASS,
                DEFAULT_LOGGER_SEVERITY_LEVEL,
                ConfigurationServiceInstanceHolder.INSTANCE,
                new WriterThreadProvider());
    }

    NativeLoggerFactory(@NonNull Class<?> loggerInterface,
            @NonNull Class<?> loggerClass,
            @NonNull Level loggerDefaultLevel,
            @NonNull ConfigurationService configurationService,
            @NonNull WriterThreadProvider writerThreadProvider) {
        this.loggerDefaultLevel = loggerDefaultLevel;
        this.loggerInterface = loggerInterface;
        this.logService = new LogServiceDefault(loggerClass, configurationService, writerThreadProvider);
    }

    public static void refreshConfiguration() {
        refreshConfiguration(null);
    }

    public static void refreshConfiguration(Properties properties) {
        ConfigurationServiceInstanceHolder.INSTANCE.refresh(properties);
    }

    @Override
    public NativeLogger logger() {
        return new NativeLogger(StackTraceUtils.callerOf(this.loggerInterface).getClassName(),
                loggerDefaultLevel,
                logService);
    }

    private static class ConfigurationServiceInstanceHolder {
        private static final ConfigurationService INSTANCE = new ConfigurationService();
    }
}
