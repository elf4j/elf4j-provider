package elf4j.impl.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WriterThreadProvider {
    private static final Executor WRITER_THREAD = Executors.newSingleThreadExecutor();

    public Executor getWriterThread() {
        return WRITER_THREAD;
    }
}
