package elf4j.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WriterThreadProvider {
    private static final ExecutorService WRITER_THREAD = Executors.newSingleThreadExecutor();
    public ExecutorService getWriterThread() {
        return WRITER_THREAD;
    }
}
