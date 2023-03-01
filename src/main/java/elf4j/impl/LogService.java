package elf4j.impl;

import elf4j.impl.util.StackTraceUtils;
import lombok.NonNull;

class LogService {
    private final ConfigurationService configurationService;

    private final WriterThreadProvider writerThreadProvider;

    LogService(ConfigurationService configurationService, WriterThreadProvider writerThreadProvider) {
        this.configurationService = configurationService;
        this.writerThreadProvider = writerThreadProvider;
    }

    boolean isEnabled(NativeLogger nativeLogger) {
        return this.configurationService.getLoggerConfiguration(nativeLogger).isEnabled();
    }

    void log(@NonNull NativeLogger nativeLogger, Throwable exception, Object message, Object[] args) {
        LoggerConfiguration loggerConfiguration = this.configurationService.getLoggerConfiguration(nativeLogger);
        boolean callerFrameRequired = loggerConfiguration.isCallerFrameRequired();
        boolean callerThreadInfoRequired = loggerConfiguration.isCallerThreadInfoRequired();
        boolean asyncEnabled = this.configurationService.isAsyncEnabled();
        boolean callThreadInfoEager = callerThreadInfoRequired && asyncEnabled;
        LogEntry logEntry = LogEntry.builder()
                .callerFrame(callerFrameRequired ? StackTraceUtils.getCaller(NativeLogger.class) : null)
                .callerThreadName(callThreadInfoEager ? Thread.currentThread().getName() : null)
                .callerThreadId(callThreadInfoEager ? Thread.currentThread().getId() : null)
                .nativeLogger(nativeLogger)
                .exception(exception)
                .message(message)
                .arguments(args)
                .build();
        LogWriter writer = loggerConfiguration.getWriter();
        if (asyncEnabled) {
            this.writerThreadProvider.getWriterThread().execute(() -> writer.write(logEntry));
        } else {
            writer.write(logEntry);
        }
    }
}


