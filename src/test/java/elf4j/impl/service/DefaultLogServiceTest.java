package elf4j.impl.service;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.NativeLogger;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.writer.LogWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DefaultLogServiceTest {
    @Mock LoggingConfiguration mockLoggingConfiguration;

    @Mock WriterThreadProvider mockWriterThreadProvider;
    NativeLogger nativeLogger;
    DefaultLogService logService;

    @BeforeEach
    void init() {
        logService = new DefaultLogService(Logger.class, mockLoggingConfiguration, mockWriterThreadProvider);
        nativeLogger = new NativeLogger(this.getClass().getName(), Level.TRACE, logService);
    }

    static class StubSyncExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    @Nested
    class isEnabled {
        @Test
        void delegateToConfiguration() {
            logService.isEnabled(nativeLogger);

            then(mockLoggingConfiguration).should().isEnabled(nativeLogger);
        }
    }

    @Nested
    class log {
        @Mock LogWriter mockLogWriter;
        @Captor ArgumentCaptor<LogEntry> captorLogEntry;
        @Mock private ExecutorService mockExecutorService;

        @Test
        void async() {
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(true);
            given(mockLoggingConfiguration.getLogServiceWriter()).willReturn(mockLogWriter);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(mockExecutorService);

            logService.log(nativeLogger, null, null, null);

            then(mockExecutorService).should().execute(any(Runnable.class));
        }

        @Test
        void callThreadInfoEager() {
            given(mockLogWriter.includeCallerThread()).willReturn(true);
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(true);
            given(mockLoggingConfiguration.getLogServiceWriter()).willReturn(mockLogWriter);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(new StubSyncExecutor());

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertEquals(Thread.currentThread().getName(), captorLogEntry.getValue().getCallerThread().getName());
            assertEquals(Thread.currentThread().getId(), captorLogEntry.getValue().getCallerThread().getId());
        }

        @Test
        void callThreadInfoLazy() {
            given(mockLogWriter.includeCallerThread()).willReturn(false);
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(true);
            given(mockLoggingConfiguration.getLogServiceWriter()).willReturn(mockLogWriter);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(new StubSyncExecutor());

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertNull(captorLogEntry.getValue().getCallerThread());
        }

        @Test
        void exceptionIndicatesAttemptOfCallFrame() {
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(true);
            given(mockLoggingConfiguration.getLogServiceWriter()).willReturn(mockLogWriter);
            given(mockLogWriter.includeCallerDetail()).willReturn(true);

            assertThrows(NoSuchElementException.class, () -> logService.log(nativeLogger, null, null, null));
        }

        @Test
        void noCallFrameRequired() {
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(true);
            given(mockLogWriter.includeCallerDetail()).willReturn(false);
            given(mockLoggingConfiguration.getLogServiceWriter()).willReturn(mockLogWriter);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(new StubSyncExecutor());
            Exception exception = new Exception();
            String message = "test message {}";
            Object[] args = { "test arg" };

            logService.log(nativeLogger, exception, message, args);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            LogEntry logEntry = captorLogEntry.getValue();
            assertNull(logEntry.getCallerFrame());
            assertSame(exception, logEntry.getException());
            assertSame(message, logEntry.getMessage());
            assertSame(args, logEntry.getArguments());
        }

        @Test
        void onlyLogWhenEnabled() {
            given(mockLoggingConfiguration.isEnabled(any(NativeLogger.class))).willReturn(false);

            logService.log(nativeLogger, null, null, null);

            then(mockLoggingConfiguration).should(never()).getLogServiceWriter();
        }
    }
}