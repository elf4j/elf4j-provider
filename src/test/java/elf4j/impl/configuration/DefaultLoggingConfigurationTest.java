package elf4j.impl.configuration;

import elf4j.Level;
import elf4j.impl.NativeLogger;
import elf4j.impl.service.LogService;
import elf4j.impl.writer.LogWriter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultLoggingConfigurationTest {
    @Mock LevelRepository mockLevelRepository;
    @Mock WriterRepository mockWriterRepository;
    @Mock LogWriter stubLogWriter;
    @Mock LogService mockLogService;

    @Nested
    class isEnabled {
        @Test
        void loadFromReposOnlyOnce() {
            DefaultLoggingConfiguration defaultLoggingConfiguration =
                    new DefaultLoggingConfiguration(mockLevelRepository, mockWriterRepository);
            NativeLogger nativeLogger = new NativeLogger("test.owner.class.Name", Level.OFF, mockLogService);
            given(mockWriterRepository.getLogServiceWriter()).willReturn(stubLogWriter);
            given(stubLogWriter.getMinimumLevel()).willReturn(Level.TRACE);
            given(mockLevelRepository.getLoggerMinimumLevel(nativeLogger)).willReturn(Level.TRACE);

            defaultLoggingConfiguration.isEnabled(nativeLogger);

            then(mockWriterRepository).should().getLogServiceWriter();
            then(mockLevelRepository).should().getLoggerMinimumLevel(nativeLogger);

            defaultLoggingConfiguration.isEnabled(nativeLogger);

            then(mockWriterRepository).shouldHaveNoMoreInteractions();
            then(mockLevelRepository).shouldHaveNoMoreInteractions();
        }
    }
}