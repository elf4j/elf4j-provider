package elf4j.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class LoggerConfiguration {
    boolean asyncEnabled;
    boolean enabled;
    LogWriter writer;
}
