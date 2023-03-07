package elf4j.impl;

import elf4j.Level;
import elf4j.Logger;
import elf4j.impl.service.LogService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.concurrent.Immutable;

@Immutable
@ToString
@EqualsAndHashCode
public class NativeLogger implements Logger {
    @NonNull private final LogService logService;
    @EqualsAndHashCode.Include @NonNull private final String name;
    @EqualsAndHashCode.Include @NonNull private final Level level;

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

    public @NonNull String getName() {
        return this.name;
    }

    @NonNull LogService getLogService() {
        return logService;
    }

    private NativeLogger atLevel(Level level) {
        return this.level == level ? this : new NativeLogger(this.name, level, logService);
    }

    private void service(Throwable exception, Object message, Object[] args) {
        this.logService.log(this, exception, message, args);
    }
}
