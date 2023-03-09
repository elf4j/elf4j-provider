package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.service.LogService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
public class NativeLogger implements Logger {
    /**
     * Taken from the name of the "owner class" - the class that created this logger instance. The owner class is
     * usually the same as the "caller class" - the class that calls the logging methods of this instance. In strange
     * scenarios, the caller class can be different from the owner class i.e. when the owner class passed this logger
     * instance out to another (caller) class.
     */
    @NonNull String name;
    @NonNull Level level;
    @EqualsAndHashCode.Exclude @NonNull LogService logService;

    public NativeLogger(@NonNull String name, @NonNull Level level, @NonNull LogService logService) {
        this.name = name;
        this.level = level;
        this.logService = logService;
    }

    @Override
    public NativeLogger atDebug() {
        return atLevel(Level.DEBUG);
    }

    @Override
    public NativeLogger atError() {
        return atLevel(Level.ERROR);
    }

    @Override
    public NativeLogger atInfo() {
        return atLevel(Level.INFO);
    }

    @Override
    public NativeLogger atTrace() {
        return atLevel(Level.TRACE);
    }

    @Override
    public NativeLogger atWarn() {
        return atLevel(Level.WARN);
    }

    @Override
    public @NonNull Level getLevel() {
        return this.level;
    }

    @Override
    public boolean isEnabled() {
        return this.logService.isEnabled(this);
    }

    @Override
    public void log(Object message) {
        this.service(null, message, null);
    }

    @Override
    public void log(String message, Object... args) {
        this.service(null, message, args);
    }

    @Override
    public void log(Throwable t) {
        this.service(t, null, null);
    }

    @Override
    public void log(Throwable t, Object message) {
        this.service(t, message, null);
    }

    @Override
    public void log(Throwable t, String message, Object... args) {
        this.service(t, message, args);
    }

    private NativeLogger atLevel(Level level) {
        return this.level == level ? this : new NativeLogger(this.name, level, logService);
    }

    private void service(Throwable exception, Object message, Object[] args) {
        this.logService.log(this, exception, message, args);
    }
}
