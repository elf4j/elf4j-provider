package elf4j.impl.configuration;

import elf4j.Level;
import elf4j.impl.NativeLogger;
import elf4j.impl.writer.LogWriter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLoggingConfiguration implements LoggingConfiguration {
    private final Map<NativeLogger, Boolean> loggerConfigurationCache = new ConcurrentHashMap<>();
    private final PropertiesLoader propertiesLoader;
    private LevelRepository levelRepository;
    private WriterRepository writerRepository;

    public DefaultLoggingConfiguration() {
        this(new PropertiesLoader());
    }

    DefaultLoggingConfiguration(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
        setRepositories(propertiesLoader.load());
    }

    DefaultLoggingConfiguration(LevelRepository levelRepository, WriterRepository writerRepository) {
        this(new PropertiesLoader());
        this.levelRepository = levelRepository;
        this.writerRepository = writerRepository;
    }

    @Override
    public LogWriter getLogServiceWriter() {
        return writerRepository.getLogServiceWriter();
    }

    @Override
    public boolean isEnabled(NativeLogger nativeLogger) {
        return this.loggerConfigurationCache.computeIfAbsent(nativeLogger, this::loadLoggerConfigurationCache);
    }

    @Override
    public void refresh(@Nullable Properties properties) {
        Properties refreshed = this.propertiesLoader.load();
        if (properties != null) {
            refreshed.putAll(properties);
        }
        setRepositories(refreshed);
        this.loggerConfigurationCache.clear();
    }

    private boolean loadLoggerConfigurationCache(NativeLogger nativeLogger) {
        Level loggerMinimumLevel = levelRepository.getLoggerMinimumLevel(nativeLogger);
        Level logServiceWriterMinimumLevel = writerRepository.getLogServiceWriter().getMinimumLevel();
        int effectiveMinimumLevelOrdinal =
                Math.max(loggerMinimumLevel.ordinal(), logServiceWriterMinimumLevel.ordinal());
        return nativeLogger.getLevel().ordinal() >= effectiveMinimumLevelOrdinal;
    }

    private void setRepositories(@NonNull Properties properties) {
        this.levelRepository = new LevelRepository(properties);
        this.writerRepository = new WriterRepository(properties);
    }
}
