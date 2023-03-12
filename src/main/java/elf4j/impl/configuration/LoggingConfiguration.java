package elf4j.impl.configuration;

import elf4j.impl.NativeLogger;
import elf4j.impl.writer.LogWriter;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 *
 */
public interface LoggingConfiguration {
    /**
     * @return the top level (group) writer for the log service, may contain multiple individual writers.
     */
    LogWriter getLogServiceWriter();

    /**
     * @param nativeLogger the logger to check for enablement against configuration
     * @return true if the specified logger's level is at or above the configured minimum output level of both the
     *         writer and that configured for the logger; otherwise, false.
     */
    boolean isEnabled(NativeLogger nativeLogger);

    /**
     * @param properties used to refresh the logging configuration. If <code>null</code>, only properties reloaded from
     *                   the configuration file will be used. Otherwise, the specified properties will override others
     *                   that are reloaded from the configuration file.
     */
    void refresh(@Nullable Properties properties);
}
