package elf4j.impl.util;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StackTraceFrame {
    String className;

    String methodName;

    int lineNumber;

    String fileName;
}
