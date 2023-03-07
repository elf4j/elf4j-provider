package elf4j.impl.writer.pattern;

import elf4j.Level;
import elf4j.impl.NativeLogger;
import elf4j.impl.service.LogEntry;
import elf4j.impl.service.LogService;
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
                .callerThread(LogEntry.ThreadInformation.builder()
                        .name(Thread.currentThread().getName())
                        .id(Thread.currentThread().getId())
                        .build())
                .callerFrame(LogEntry.StackTraceFrame.builder()
                        .className(JsonPattern.class.getName())
                        .methodName("testMethod")
                        .lineNumber(42)
                        .fileName("testFileName")
                        .build())
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