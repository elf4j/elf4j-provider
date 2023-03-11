package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.util.StackTraceUtils;
import elf4j.impl.util.ThreadLocalContext;
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
    public boolean includeCallerDetail() {
        return this.loggingConfiguration.getLogServiceWriter().includeCallerThread();
    }

    @Override
    public boolean includeCallerThread() {
        return this.loggingConfiguration.getLogServiceWriter().includeCallerThread();
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
        LogEntry.LogEntryBuilder logEntryBuilder =
                LogEntry.builder().nativeLogger(nativeLogger).exception(exception).message(message).arguments(args);
        LogWriter writer = loggingConfiguration.getLogServiceWriter();
        if (writer.includeCallerDetail()) {
            LogEntry.StackTraceFrame overrideCallerFrame = ThreadLocalContext.data().getCallerFrame();
            logEntryBuilder.callerFrame(overrideCallerFrame != null ? overrideCallerFrame :
                    StackTraceUtils.callerOf(this.getLoggerClass()));
            ThreadLocalContext.clear();
        }
        if (writer.includeCallerThread()) {
            Thread callerThread = Thread.currentThread();
            logEntryBuilder.callerThread(new LogEntry.ThreadInformation(callerThread.getName(), callerThread.getId()));
        }
        LogEntry logEntry = logEntryBuilder.build();
        writerThreadProvider.getWriterThread().execute(() -> writer.write(logEntry));
    }

    @NonNull Class<?> getLoggerClass() {
        return loggerClass;
    }
}
