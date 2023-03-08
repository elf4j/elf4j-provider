package elf4j.impl.writer.pattern;

import elf4j.Level;
import elf4j.impl.NativeLogger;
import elf4j.impl.service.LogEntry;
import elf4j.impl.service.LogEntry.StackTraceFrame;
import elf4j.impl.service.LogEntry.ThreadInformation;
import elf4j.impl.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JsonPatternTest {
    @Mock LogService stubLogService;
    LogEntry mockLogEntry;
    String mockMessage = "testLogMessage {}";

    @BeforeEach
    void beforeEach() {
        mockLogEntry = LogEntry.builder()
                .nativeLogger(new NativeLogger("testLoggerName", Level.ERROR, stubLogService))
                .callerThread(ThreadInformation.builder()
                        .name(Thread.currentThread().getName())
                        .id(Thread.currentThread().getId())
                        .build())
                .callerFrame(StackTraceFrame.builder()
                        .className(JsonPattern.class.getName())
                        .methodName("testMethod")
                        .lineNumber(42)
                        .fileName("testFileName")
                        .build())
                .message(mockMessage)
                .arguments(new Object[] { "testArg1" })
                .exception(new Exception("testExceptionMessage"))
                .build();
    }

    @Nested
    class from {
        @Test
        void noPatternOptionDefaults() {
            JsonPattern jsonPattern = JsonPattern.from("json");

            assertFalse(jsonPattern.includeCallerThread());
            assertFalse(jsonPattern.includeCallerDetail());
        }

        @Test
        void includeCallerOption() {
            JsonPattern jsonPattern = JsonPattern.from("json:caller-detail");

            assertFalse(jsonPattern.includeCallerThread());
            assertTrue(jsonPattern.includeCallerDetail());
        }

        @Test
        void includeThreadOption() {
            JsonPattern jsonPattern = JsonPattern.from("json:caller-thread");

            assertTrue(jsonPattern.includeCallerThread());
            assertFalse(jsonPattern.includeCallerDetail());
        }

        @Test
        void includeCallerAndThreadOptions() {
            JsonPattern jsonPattern = JsonPattern.from("json:caller-thread,caller-detail");

            assertTrue(jsonPattern.includeCallerThread());
            assertTrue(jsonPattern.includeCallerDetail());
        }
    }

    @Nested
    class render {
        JsonPattern jsonPattern = JsonPattern.from("json");

        @Test
        void resolveMessage() {
            StringBuilder layout = new StringBuilder();

            jsonPattern.render(mockLogEntry, layout);
            String rendered = layout.toString();

            assertFalse(rendered.contains("testLogMessage {}"));
            assertTrue(rendered.contains(mockLogEntry.getResolvedMessage()));
        }
    }
}