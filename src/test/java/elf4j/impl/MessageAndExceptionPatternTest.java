package elf4j.impl;

import elf4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MessageAndExceptionPatternTest {
    @Mock LogService stubLogService;
    LogEntry mockLogEntry;
    String mockMessage = "testLogMessage {}";
    Object[] mockArgs = new Object[] { "testArg1" };
    Exception mockException = new Exception("testExceptionMessage");

    @BeforeEach
    void beforeEach() {
        mockLogEntry = LogEntry.builder()
                .nativeLogger(new NativeLogger("testLoggerName", Level.ERROR, stubLogService))
                .callerThread(new LogEntry.ThreadInformation(Thread.currentThread().getName(),
                        Thread.currentThread().getId()))
                .callerFrame(new LogEntry.StackTraceFrame(JsonPattern.class.getName(),
                        "testMethod",
                        42,
                        "testFileName"))
                .message(mockMessage)
                .arguments(mockArgs)
                .exception(mockException)
                .build();
    }

    @Nested
    class from {
        @Test
        void errorOnInvalidPatternText() {
            assertThrows(IllegalArgumentException.class, () -> MessageAndExceptionPattern.from("badPatternText"));
        }
    }

    @Nested
    class render {
        @Test
        void includeBothMessageAndException() {
            MessageAndExceptionPattern messageAndExceptionPattern = MessageAndExceptionPattern.from("message");
            StringBuilder logText = new StringBuilder();

            messageAndExceptionPattern.render(mockLogEntry, logText);
            String rendered = logText.toString();

            assertTrue(rendered.contains(mockLogEntry.getResolvedMessage()));
            assertTrue(rendered.contains(mockException.getMessage()));
        }
    }
}