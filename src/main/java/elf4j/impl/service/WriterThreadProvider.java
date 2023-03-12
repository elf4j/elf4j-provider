package elf4j.impl.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 */
public class WriterThreadProvider {
    private static final Executor WRITER_THREAD = Executors.newSingleThreadExecutor();

    /**
     * @return thread executor to service the writers asynchronously
     */
    public Executor getWriterThread() {
        return WRITER_THREAD;
    }
}
