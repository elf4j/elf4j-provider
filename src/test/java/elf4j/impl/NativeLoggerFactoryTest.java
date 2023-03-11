package elf4j.impl;

import elf4j.Level;
import elf4j.impl.configuration.LoggingConfiguration;
import elf4j.impl.service.DefaultLogService;
import elf4j.impl.service.WriterThreadProvider;
import elf4j.impl.util.StackTraceUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class NativeLoggerFactoryTest {
    @Mock LoggingConfiguration mockLoggingConfiguration;
    @Mock WriterThreadProvider mockWriterThreadProvider;

    @Nested
    class customizedFactory {
        @Test
        void level() {
            Class<?> stubLoggerAccessInterface = NativeLoggerFactory.class;
            Class<?> stubLoggerServiceInterface = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR,
                    stubLoggerAccessInterface,
                    stubLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider);

            assertEquals(Level.ERROR, nativeLoggerFactory.logger().getLevel());
        }

        @Test
        void loggerClass() {
            Class<?> stubLoggerAccessInterface = NativeLoggerFactory.class;
            Class<?> mockLoggerServiceInterface = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR,
                    stubLoggerAccessInterface,
                    mockLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider);

            assertEquals(new DefaultLogService(mockLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider), nativeLoggerFactory.logger().getLogService());
        }

        @Test
        void name() {
            Class<?> mockLoggerInterface = this.getClass();
            Class<?> stubLoggerServiceInterface = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR,
                    mockLoggerInterface,
                    stubLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider);

            NativeLogger logger = nativeLoggerFactory.logger();

            assertSame(StackTraceUtils.callerOf(mockLoggerInterface).getClassName(), logger.getName());
        }

        @Test
        void service() {
            Class<?> stubLoggerAccessInterface = NativeLoggerFactory.class;
            Class<?> stubLoggerServiceInterface = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(Level.ERROR,
                    stubLoggerAccessInterface,
                    stubLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider);

            assertEquals(new DefaultLogService(stubLoggerServiceInterface,
                    mockLoggingConfiguration,
                    mockWriterThreadProvider), nativeLoggerFactory.logger().getLogService());
        }
    }
}