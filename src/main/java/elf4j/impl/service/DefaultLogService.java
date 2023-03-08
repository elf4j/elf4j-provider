package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.writer.LogWriter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class DefaultLogService implements LogService {
    @NonNull private final Class<?> loggerClass;
    private final LoggingConfiguration loggingConfiguration;
    private final WriterThreadProvider writerThreadProvider;

    public DefaultLogService(@NonNull Class<?> loggerClass,
            LoggingConfiguration loggingConfiguration,
            WriterThreadProvider writerThreadProvider) {
        this.loggerClass = loggerClass;
        this.loggingConfiguration = loggingConfiguration;
        this.writerThreadProvider = writerThreadProvider;
    }

    @Override
    public boolean isEnabled(NativeLogger nativeLogger) {
        return loggingConfiguration.isEnabled(nativeLogger);
    }

    @Override
    public void log(@NonNull NativeLogger nativeLogger, Throwable exception, Object message, Object[] args) {
        if (!loggingConfiguration.isEnabled(nativeLogger)) {
            return;
        }
        LogWriter writer = loggingConfiguration.getLogServiceWriter();
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
