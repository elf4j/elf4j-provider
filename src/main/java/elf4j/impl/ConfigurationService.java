package elf4j.impl;

import elf4j.Level;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationService {
    private final Map<NativeLogger, LoggerConfiguration> loggerConfigurationCache = new ConcurrentHashMap<>();
    private SystemRepository systemRepository;

    private LoggerRepository loggerRepository;

    private WriterRepository writerRepository;

    public ConfigurationService() {
        loadRepositories();
    }

    private void loadRepositories() {
        Properties properties = loadProperties();
        buildSystemRepository(properties);
        buildWriterRepository(properties);
        buildLoggerRepository(properties);
    }

    private Properties loadProperties() {
        return null;
    }

    private void buildSystemRepository(Properties properties) {
    }

    private void buildWriterRepository(Properties properties) {

    }

    private void buildLoggerRepository(Properties properties) {

    }

    LoggerConfiguration getLoggerConfiguration(NativeLogger nativeLogger) {
        return this.loggerConfigurationCache.computeIfAbsent(nativeLogger, this::loadLoggerConfiguration);
    }

    private LoggerConfiguration loadLoggerConfiguration(NativeLogger nativeLogger) {
        boolean asyncEnabled = systemRepository.getAsyncEnabled();
        Level defaultMinimumLevel = systemRepository.getLevel();
        Level writerMinimumLevel = writerRepository.getMinimumLevel();
        Level loggerMinimumLevel = loggerRepository.getLoggerMinimumLevelOrDefault(nativeLogger, defaultMinimumLevel);
        int effectiveMinimumLevelOrdinal = Math.max(loggerMinimumLevel.ordinal(), writerMinimumLevel.ordinal());
        boolean loggerEnabled = nativeLogger.getLevel().ordinal() >= effectiveMinimumLevelOrdinal;
        return LoggerConfiguration.builder().enabled(loggerEnabled).writer(writerRepository.getGroupWriter()).build();
    }

    void refresh() {
        loadRepositories();
        this.loggerConfigurationCache.clear();
    }

    private Level getLoggerMinimumLevel(Map<String, Level> loggerConfigurationLevels,
            List<LogWriter> writers,
            @NonNull NativeLogger nativeLogger) {
        String loggerName = nativeLogger.getName();
        for (String name = loggerName; name.length() > 0; name = name.substring(0, name.lastIndexOf('.'))) {
            if (loggerConfigurationLevels.containsKey(name)) {
                return loggerConfigurationLevels.get(name);
            }
        }
        return Objects.requireNonNull(loggerConfigurationLevels.get(""));
    }
}
