package elf4j.impl.writer;

/**
 *
 */
public interface PerformanceSensitive {
    /**
     * @return true if log should include caller detail such as method, line number...
     */
    boolean includeCallerDetail();

    /**
     * @return true if log should include call thread information such thread name and id
     */
    boolean includeCallerThread();
}
