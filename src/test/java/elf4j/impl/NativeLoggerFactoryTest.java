package elf4j.impl;

import elf4j.Level;
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
    @Mock ConfigurationService mockConfigurationService;
    @Mock WriterThreadProvider mockWriterThreadProvider;

    @Nested
    class customizedFactory {
        @Test
        void level() {
            Class<?> stubLoggerInterface = NativeLoggerFactory.class;
            Class<?> stubLoggerClass = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(stubLoggerInterface,
                    stubLoggerClass,
                    Level.ERROR,
                    mockConfigurationService,
                    mockWriterThreadProvider);

            assertEquals(Level.ERROR, nativeLoggerFactory.logger().getLevel());
        }

        @Test
        void loggerClass() {
            Class<?> stubLoggerInterface = NativeLoggerFactory.class;
            Class<?> mockLoggerClass = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(stubLoggerInterface,
                    mockLoggerClass,
                    Level.ERROR,
                    mockConfigurationService,
                    mockWriterThreadProvider);

            assertEquals(new LogServiceDefault(mockLoggerClass, mockConfigurationService, mockWriterThreadProvider),
                    nativeLoggerFactory.logger().getLogService());
        }

        @Test
        void name() {
            Class<?> mockLoggerInterface = this.getClass();
            Class<?> stubLoggerClass = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(mockLoggerInterface,
                    stubLoggerClass,
                    Level.ERROR,
                    mockConfigurationService,
                    mockWriterThreadProvider);

            NativeLogger logger = nativeLoggerFactory.logger();

            assertSame(StackTraceUtils.callerOf(mockLoggerInterface).getClassName(), logger.getName());
        }

        @Test
        void service() {
            Class<?> stubLoggerInterface = NativeLoggerFactory.class;
            Class<?> stubLoggerClass = this.getClass();
            NativeLoggerFactory nativeLoggerFactory = new NativeLoggerFactory(stubLoggerInterface,
                    stubLoggerClass,
                    Level.ERROR,
                    mockConfigurationService,
                    mockWriterThreadProvider);

            assertEquals(new LogServiceDefault(stubLoggerClass, mockConfigurationService, mockWriterThreadProvider),
                    nativeLoggerFactory.logger().getLogService());
        }
    }
}