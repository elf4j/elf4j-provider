package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class NativeLoggerFactoryTest {
    @Nested
    class newLogger {
        NativeLogger nativeLogger = (NativeLogger) Logger.instance();

        @Test
        void defaultLevel() {
            assertSame(Level.TRACE, nativeLogger.getLevel());
        }

        @Test
        void name() {
            assertEquals(this.getClass().getName(), nativeLogger.getName());
        }

        @Test
        void service() {
            assertSame(NativeLoggerFactory.getLogService(), nativeLogger.getLogService());
        }
    }
}