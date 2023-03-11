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
    private static final Class<Logger> DEFAULT_ACCESS_INTERFACE = Logger.class;
    private static final Level DEFAULT_LOGGER_SEVERITY_LEVEL = Level.TRACE;
    private static final Class<?> DEFAULT_SERVICE_INTERFACE = NativeLogger.class;
    @NonNull private final Level defaultLoggerLevel;
    @NonNull private final Class<?> accessInterface;
    @NonNull private final LogService logService;

    /**
     * Default constructor required by {@link java.util.ServiceLoader}
     */
    public NativeLoggerFactory() {
        this(DEFAULT_ACCESS_INTERFACE, DEFAULT_SERVICE_INTERFACE);
    }

    public NativeLoggerFactory(@NonNull Class<?> accessInterface, @NonNull Class<?> serviceInterface) {
        this(DEFAULT_LOGGER_SEVERITY_LEVEL,
                accessInterface,
                serviceInterface,
                ConfigurationInstanceHolder.INSTANCE,
                new WriterThreadProvider());
    }

    NativeLoggerFactory(@NonNull Level defaultLoggerLevel,
            @NonNull Class<?> accessInterface,
            @NonNull Class<?> serviceInterface,
            @NonNull LoggingConfiguration loggingConfiguration,
            @NonNull WriterThreadProvider writerThreadProvider) {
        this.defaultLoggerLevel = defaultLoggerLevel;
        this.accessInterface = accessInterface;
        this.logService = new DefaultLogService(serviceInterface, loggingConfiguration, writerThreadProvider);
    }

    public static void refreshConfiguration() {
        refreshConfiguration(null);
    }

    public static void refreshConfiguration(Properties properties) {
        ConfigurationInstanceHolder.INSTANCE.refresh(properties);
    }

    @Override
    public NativeLogger logger() {
        return new NativeLogger(StackTraceUtils.callerOf(this.accessInterface).getClassName(),
                defaultLoggerLevel,
                logService);
    }

    private static class ConfigurationInstanceHolder {
        private static final LoggingConfiguration INSTANCE = new DefaultLoggingConfiguration();
    }
}
