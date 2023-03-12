package elf4j.impl.service;

import elf4j.impl.NativeLogger;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.util.StackTraceUtils;
import elf4j.impl.util.ThreadLocalContext;
import elf4j.impl.writer.LogWriter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 *
 */
@EqualsAndHashCode
public class DefaultLogService implements LogService {
    @NonNull private final Class<?> serviceInterface;
    private final LoggingConfiguration loggingConfiguration;
    private final WriterThreadProvider writerThreadProvider;

    /**
     * @param serviceInterface     the direct client facing class being called for logging service, usually the concrete
     *                             logger class
     * @param loggingConfiguration configuration for min logging output level and log writers
     * @param writerThreadProvider provides the async writer thread
     */
    public DefaultLogService(@NonNull Class<?> serviceInterface,
            LoggingConfiguration loggingConfiguration,
            WriterThreadProvider writerThreadProvider) {
        this.serviceInterface = serviceInterface;
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
                    StackTraceUtils.callerOf(this.getServiceInterface()));
        }
        if (writer.includeCallerThread()) {
            Thread callerThread = Thread.currentThread();
            logEntryBuilder.callerThread(new LogEntry.ThreadInformation(callerThread.getName(), callerThread.getId()));
        }
        LogEntry logEntry = logEntryBuilder.build();
        ThreadLocalContext.clear();
        writerThreadProvider.getWriterThread().execute(() -> writer.write(logEntry));
    }

    @NonNull Class<?> getServiceInterface() {
        return serviceInterface;
    }
}
