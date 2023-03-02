package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class NativeLoggerFactoryTest {
    @Nested
    class customizedFactory {
        @Test
        void customizedLoggerClass() {
            Class<?> aCurrentCallStackClass = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR, aCurrentCallStackClass);

            assertEquals(aCurrentCallStackClass, nativeLoggerFactory.getLogService().getLoggerClass());
            assertSame(nativeLoggerFactory.getLogService(), nativeLoggerFactory.logger().getLogService());
        }

        @Test
        void customizedLoggerLevel() {
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR, this.getClass());

            assertEquals(Level.ERROR, nativeLoggerFactory.logger().getLevel());
        }
    }

    @Nested
    class defaultFactory {
        NativeLogger nativeLogger = (NativeLogger) Logger.instance();

        @Test
        void level() {
            assertSame(Level.TRACE, nativeLogger.getLevel());
        }

        @Test
        void name() {
            assertEquals(this.getClass().getName(), nativeLogger.getName());
        }

        @Test
        void service() {
            assertSame(Logger.class, nativeLogger.getLogService().getLoggerClass());
        }
    }
}