package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @Mock ConfigurationService mockConfigurationService;

    @Mock LoggerConfiguration mockLoggerConfiguration;

    @Mock WriterThreadProvider mockWriterThreadProvider;
    NativeLogger nativeLogger;
    LogService logService;

    @BeforeEach
    void init() {
        logService = new LogService(Logger.class, mockConfigurationService, mockWriterThreadProvider);
        nativeLogger = new NativeLogger(this.getClass().getName(), Level.TRACE, logService);
        given(mockConfigurationService.getLoggerConfiguration(nativeLogger)).willReturn(mockLoggerConfiguration);
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
        @Captor ArgumentCaptor<LogEntry> captorLogEntry;
        @Mock private ExecutorService mockExecutorService;

        @Test
        void async() {
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLoggerConfiguration.isAsyncEnabled()).willReturn(true);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);
            given(mockWriterThreadProvider.getWriterThread()).willReturn(mockExecutorService);

            logService.log(nativeLogger, null, null, null);

            then(mockWriterThreadProvider).should().getWriterThread();
            then(mockExecutorService).should().execute(any(Runnable.class));
        }

        @Test
        void callThreadInfoEager() {
            given(mockLogWriter.isCallerThreadInfoRequired()).willReturn(true);
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertEquals(Thread.currentThread().getName(), captorLogEntry.getValue().getCallerThreadName());
            assertEquals(Thread.currentThread().getId(), captorLogEntry.getValue().getCallerThreadId());
        }

        @Test
        void callThreadInfoLazy() {
            given(mockLogWriter.isCallerThreadInfoRequired()).willReturn(false);
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertNull(captorLogEntry.getValue().getCallerThreadName());
            assertEquals(0, captorLogEntry.getValue().getCallerThreadId());
        }

        @Test
        void exceptionIndicatesAttemptOfCallFrame() {
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);
            given(mockLogWriter.isCallerFrameRequired()).willReturn(true);

            assertThrows(NoSuchElementException.class, () -> logService.log(nativeLogger, null, null, null));
        }

        @Test
        void noCallFrameRequired() {
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLogWriter.isCallerFrameRequired()).willReturn(false);
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

        @Test
        void onlyLogWhenEnabled() {
            given(mockLoggerConfiguration.isEnabled()).willReturn(false);

            logService.log(nativeLogger, null, null, null);

            then(mockLoggerConfiguration).should(never()).getWriter();
        }

        @Test
        void sync() {
            given(mockLoggerConfiguration.isEnabled()).willReturn(true);
            given(mockLoggerConfiguration.isAsyncEnabled()).willReturn(false);
            given(mockLoggerConfiguration.getWriter()).willReturn(mockLogWriter);

            logService.log(nativeLogger, null, null, null);

            then(mockLogWriter).should().write(captorLogEntry.capture());
            assertSame(nativeLogger, captorLogEntry.getValue().getNativeLogger());
        }
    }
}