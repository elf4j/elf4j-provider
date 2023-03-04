package elf4j.impl;

import lombok.NonNull;

class LogService {
    @NonNull private final Class<?> loggerClass;
    private final ConfigurationService configurationService;
    private final WriterThreadProvider writerThreadProvider;

    LogService(@NonNull Class<?> loggerClass,
            ConfigurationService configurationService,
            WriterThreadProvider writerThreadProvider) {
        this.loggerClass = loggerClass;
        this.configurationService = configurationService;
        this.writerThreadProvider = writerThreadProvider;
    }

    boolean isEnabled(NativeLogger nativeLogger) {
        return this.configurationService.getLoggerConfiguration(nativeLogger).isEnabled();
    }

    void log(@NonNull NativeLogger nativeLogger, Throwable exception, Object message, Object[] args) {
        LoggerConfiguration loggerConfiguration = this.configurationService.getLoggerConfiguration(nativeLogger);
        if (!loggerConfiguration.isEnabled()) {
            return;
        }
        LogWriter writer = loggerConfiguration.getWriter();
        LogEntry logEntry = LogEntry.newBuilder(writer, getLoggerClass())
                .nativeLogger(nativeLogger)
                .exception(exception)
                .message(message)
                .arguments(args)
                .build();
        this.writerThreadProvider.getWriterThread().execute(() -> writer.write(logEntry));
    }

    @NonNull Class<?> getLoggerClass() {
        return this.loggerClass;
    }
}
