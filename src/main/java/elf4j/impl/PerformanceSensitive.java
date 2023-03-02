package elf4j.impl;

public interface PerformanceSensitive {
    boolean isCallerFrameRequired();

    boolean isCallerThreadInfoRequired();
}
