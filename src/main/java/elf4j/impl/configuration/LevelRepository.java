package elf4j.impl.configuration;

import elf4j.Level;
import elf4j.impl.NativeLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LevelRepository {
    private static final Level DEFAULT_LOGGER_MINIMUM_LEVEL = Level.TRACE;
    Map<String, Level> loggerNameValueMap = new HashMap<>();

    public LevelRepository(Properties properties) {
        properties.stringPropertyNames().forEach(name -> {
            if (name.startsWith("level")) {
                String[] nameElements = name.split("@");
                switch (nameElements.length) {
                    case 1:
                        loggerNameValueMap.put("", Level.valueOf(properties.getProperty("level").toUpperCase()));
                        break;
                    case 2:
                        loggerNameValueMap.put(nameElements[1].trim(),
                                Level.valueOf(properties.getProperty(name).toUpperCase()));
                        break;
                    default:
                        throw new IllegalArgumentException("level key: " + name);
                }
            }
        });
    }

    public Level getLoggerMinimumLevel(NativeLogger nativeLogger) {
        String callerClassName = nativeLogger.getName();
        while (callerClassName.length() > 0) {
            if (loggerNameValueMap.containsKey(callerClassName)) {
                return loggerNameValueMap.get(callerClassName);
            }
            int end = callerClassName.lastIndexOf('.');
            if (end == -1) {
                if (loggerNameValueMap.containsKey(callerClassName)) {
                    return loggerNameValueMap.get(callerClassName);
                }
                return DEFAULT_LOGGER_MINIMUM_LEVEL;
            }
            callerClassName = callerClassName.substring(0, end);
        }
        return loggerNameValueMap.get("");
    }
}
