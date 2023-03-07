package elf4j.impl;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
class LogServiceDefault implements LogService {
    @NonNull private final Class<?> loggerClass;
    private final ConfigurationService configurationService;
    private final WriterThreadProvider writerThreadProvider;

    public LogServiceDefault(@NonNull Class<?> loggerClass,
            ConfigurationService configurationService,
            WriterThreadProvider writerThreadProvider) {
        this.loggerClass = loggerClass;
        this.configurationService = configurationService;
        this.writerThreadProvider = writerThreadProvider;
    }

    @Override
    public boolean isEnabled(NativeLogger nativeLogger) {
        return configurationService.getLoggerConfiguration(nativeLogger).isEnabled();
    }

    @Override
    public void log(@NonNull NativeLogger nativeLogger, Throwable exception, Object message, Object[] args) {
        LoggerConfiguration loggerConfiguration = configurationService.getLoggerConfiguration(nativeLogger);
        if (!loggerConfiguration.isEnabled()) {
            return;
        }
        LogWriter writer = configurationService.getWriter();
        LogEntry logEntry = LogEntry.newBuilder(writer, getLoggerClass())
                .nativeLogger(nativeLogger)
                .exception(exception)
                .message(message)
                .arguments(args)
                .build();
        writerThreadProvider.getWriterThread().execute(() -> writer.write(logEntry));
    }

    @NonNull Class<?> getLoggerClass() {
        return loggerClass;
    }
}
