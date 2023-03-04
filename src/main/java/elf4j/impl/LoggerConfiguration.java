package elf4j.impl;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoggerConfiguration {
    boolean enabled;
    LogWriter writer;
}
