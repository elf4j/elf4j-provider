package elf4j.impl;

import elf4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @Mock ConfigurationService mockConfigurationService;

    @Mock LoggerConfiguration mockLoggerConfiguration;

    @Mock WriterThreadProvider mockWriterThreadProvider;
    @InjectMocks LogService logService;
    NativeLogger nativeLogger;

    @BeforeEach
    void init() {
        nativeLogger = new NativeLogger("test.Name", Level.TRACE, logService);
        given(mockConfigurationService.getLoggerConfiguration(nativeLogger)).willReturn(mockLoggerConfiguration);
    }

    static class SpyWriter implements LogWriter {
        boolean called;
        String runThreadName;

        LogEntry logEntry;

        @Override
        public void write(LogEntry logEntry) {
            called = true;
            runThreadName = Thread.currentThread().getName();
            this.logEntry = logEntry;
        }
    }

    @Nested
    class isEnabled {
        @Test
        void delegateToConfiguration() {
            logService.isEnabled(nativeLogger);

            then(mockLoggerConfiguration).should().isEnabled();
        }
    }

    @Nested
    class log {
        @Mock LogWriter mockLogWriter;
        @Mock private ExecutorService mockExecutorService;

        @Captor ArgumentCaptor<LogEntry> captorLogEntry;

        @Test
        void async() {
            given(mockConfigurationService.isAsyncEnabled()).willReturn(true);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(mockExecutorService);

            logService.log(nativeLogger, null, null, null);

            then(mockWriterThreadProvider).should().getWriterThread();
            then(mockExecutorService).should().execute(any(Runnable.class));
        }

        @Test
        void sync() {
            given(mockConfigurationService.isAsyncEnabled()).willReturn(false);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertSame(nativeLogger, captorLogEntry.getValue().getNativeLogger());
        }

        @Test
        void callThreadInfoEager() {
            given(mockLoggerConfiguration.isCallerThreadInfoRequired()).willReturn(true);
            given(mockConfigurationService.isAsyncEnabled()).willReturn(true);
            given(mockLoggerConfiguration.isCallerFrameRequired()).willReturn(false);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(Executors.newSingleThreadExecutor());
            SpyWriter spyWriter = new SpyWriter();
            given(mockLoggerConfiguration.getWriter()).willReturn(spyWriter);

            logService.log(nativeLogger, null, null, null);

            await().until(() -> spyWriter.logEntry != null);
            assertEquals(Thread.currentThread().getName(), spyWriter.logEntry.getCallerThreadName());
            assertEquals(Thread.currentThread().getId(), spyWriter.logEntry.getCallerThreadId());
            assertNotEquals(spyWriter.runThreadName, spyWriter.logEntry.getCallerThreadName());
        }

        @Test
        void callThreadInfoLazy() {
            given(mockLoggerConfiguration.isCallerThreadInfoRequired()).willReturn(true);
            given(mockLoggerConfiguration.isCallerFrameRequired()).willReturn(false);
            given(mockConfigurationService.isAsyncEnabled()).willReturn(false);
            SpyWriter spyWriter = new SpyWriter();
            given(mockLoggerConfiguration.getWriter()).willReturn(spyWriter);

            logService.log(nativeLogger, null, null, null);

            assertNull(spyWriter.logEntry.getCallerThreadName());
            assertNull(spyWriter.logEntry.getCallerThreadId());
            assertEquals(Thread.currentThread().getName(), spyWriter.runThreadName);
        }

        @Test
        void exceptionIndicatesAttemptOfCallFrame() {
            given(mockLoggerConfiguration.isCallerFrameRequired()).willReturn(true);

            assertThrows(NoSuchElementException.class, () -> logService.log(nativeLogger, null, null, null));
        }

        @Test
        void noCallFrameRequired() {
            given(mockLoggerConfiguration.isCallerFrameRequired()).willReturn(false);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);
            Exception exception = new Exception();
            String message = "test message {}";
            Object[] args = { "test arg" };

            logService.log(nativeLogger, exception, message, args);

            then(mockLogWriter).should()
                    .write(LogEntry.builder()
                            .nativeLogger(nativeLogger)
                            .message(message)
                            .arguments(args)
                            .exception(exception)
                            .callerFrame(null)
                            .build());
        }
    }
}