package elf4j.impl.util;

import elf4j.impl.service.LogEntry;

/**
 *
 */
public class ThreadLocalContext {
    private ThreadLocalContext() {
    }

    /**
     *
     */
    public static void clear() {
        ThreadLocalHolder.INSTANCE.remove();
    }

    /**
     * @return context data stored in the current thread local
     */
    public static Data data() {
        return ThreadLocalHolder.INSTANCE.get();
    }

    /**
     *
     */
    @lombok.Data
    public static class Data {
        LogEntry.StackTraceFrame callerFrame;
    }

    private static class ThreadLocalHolder {
        private static final ThreadLocal<Data> INSTANCE = ThreadLocal.withInitial(Data::new);
    }
}
