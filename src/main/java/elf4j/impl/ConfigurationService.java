package elf4j.impl;

import elf4j.Level;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationService {
    private final Map<NativeLogger, LoggerConfiguration> loggerConfigurationCache = new ConcurrentHashMap<>();
    private final PropertiesLoader propertiesLoader;
    private LevelRepository levelRepository;
    private WriterRepository writerRepository;

    public ConfigurationService() {
        this(new PropertiesLoader());
    }

    public ConfigurationService(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
        setRepositories(propertiesLoader.load());
    }

    private void setRepositories(@NonNull Properties properties) {
        this.levelRepository = new LevelRepository(properties);
        this.writerRepository = new WriterRepository(properties);
    }

    public LoggerConfiguration getLoggerConfiguration(NativeLogger nativeLogger) {
        return this.loggerConfigurationCache.computeIfAbsent(nativeLogger, this::loadLoggerConfigurationCache);
    }

    private LoggerConfiguration loadLoggerConfigurationCache(NativeLogger nativeLogger) {
        Level loggerMinimumLevel = levelRepository.getLoggerMinimumLevel(nativeLogger);
        Level writerMinimumLevel = writerRepository.getMinimumLevel();
        int effectiveMinimumLevelOrdinal = Math.max(loggerMinimumLevel.ordinal(), writerMinimumLevel.ordinal());
        boolean loggerEnabled = nativeLogger.getLevel().ordinal() >= effectiveMinimumLevelOrdinal;
        return new LoggerConfiguration(loggerEnabled);
    }

    public LogWriter getWriter() {
        return writerRepository.getGroupWriter();
    }

    public void refresh(@Nullable Properties properties) {
        Properties refreshed = this.propertiesLoader.load();
        if (properties != null) {
            refreshed.putAll(properties);
        }
        setRepositories(refreshed);
        this.loggerConfigurationCache.clear();
    }
}
