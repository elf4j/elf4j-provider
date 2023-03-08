package elf4j.impl.configuration;

import elf4j.impl.NativeLogger;
import elf4j.impl.writer.LogWriter;

import javax.annotation.Nullable;
import java.util.Properties;

public interface LoggingConfiguration {
    LogWriter getLogServiceWriter();

    boolean isEnabled(NativeLogger nativeLogger);

    void refresh(@Nullable Properties properties);
}
