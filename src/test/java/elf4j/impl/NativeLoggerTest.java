package elf4j.impl;

import elf4j.impl.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static elf4j.Level.TRACE;
import static elf4j.Level.WARN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NativeLoggerTest {
    @Mock LogService mockLogService;

    NativeLogger nativeLogger;

    @BeforeEach
    void init() {
        nativeLogger = new NativeLogger(this.getClass().getName(), TRACE, mockLogService);
    }

    @Nested
    class atLevels {
        @Test
        void instanceForDifferentLevel() {
            NativeLogger info = nativeLogger.atInfo();
            NativeLogger warn = info.atWarn();

            assertNotSame(warn, info);
            assertEquals(info.getName(), warn.getName());
            assertEquals(WARN, warn.getLevel());
        }

        @Test
        void instanceForSameLevel() {
            NativeLogger trace = nativeLogger.atTrace();
            NativeLogger debug = nativeLogger.atDebug();
            NativeLogger info = nativeLogger.atInfo();
            NativeLogger warn = nativeLogger.atWarn();
            NativeLogger error = nativeLogger.atError();

            assertSame(trace, trace.atTrace());
            assertSame(debug, debug.atDebug());
            assertSame(info, info.atInfo());
            assertSame(warn, warn.atWarn());
            assertSame(error, error.atError());
        }
    }

    @Nested
    class enabled {
        @Test
        void delegateToService() {
            nativeLogger.isEnabled();

            then(mockLogService).should().isEnabled(nativeLogger);
        }
    }

    @Nested
    class logDelegateToService {
        String plainTextMessage = "plainTextMessage";
        String textMessageWithArgHolders = "textMessage with 2 task holders of values {} and {}";

        Object[] args = new Object[] { "1stArgOfObjectType", (Supplier) () -> "2ndArgOfSupplierType" };

        Throwable exception = new Exception("Test exception message");

        @Test
        void exception() {
            nativeLogger.log(exception);

            then(mockLogService).should().log(same(nativeLogger), same(exception), isNull(), isNull());
        }

        @Test
        void exceptionWithMessage() {
            nativeLogger.log(exception, plainTextMessage);

            then(mockLogService).should().log(same(nativeLogger), same(exception), same(plainTextMessage), isNull());
        }

        @Test
        void exceptionWithMessageAndArgs() {
            nativeLogger.log(exception, textMessageWithArgHolders, args);

            then(mockLogService).should()
                    .log(same(nativeLogger), same(exception), same(textMessageWithArgHolders), same(args));
        }

        @Test
        void messageWithArguments() {
            nativeLogger.log(textMessageWithArgHolders, args);

            then(mockLogService).should()
                    .log(same(nativeLogger), isNull(), same(textMessageWithArgHolders), same(args));
        }

        @Test
        void plainText() {
            nativeLogger.log(plainTextMessage);

            then(mockLogService).should().log(same(nativeLogger), isNull(), same(plainTextMessage), isNull());
        }
    }
}