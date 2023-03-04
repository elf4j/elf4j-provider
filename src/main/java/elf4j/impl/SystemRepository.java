package elf4j.impl;

import elf4j.Level;

import java.util.Properties;

public class SystemRepository {
    private final Properties properties;

    public SystemRepository(Properties properties) {
        this.properties = properties;
    }

    boolean getAsyncEnabled() {
        return Boolean.parseBoolean(this.properties.getProperty("async.write"));
    }

    Level getLevel() {
        String level = this.properties.getProperty("level");
        return level == null ? Level.TRACE : Level.valueOf(level.toUpperCase());
    }
}
