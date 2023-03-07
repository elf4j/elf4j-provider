package elf4j.impl.configuration;

import elf4j.Level;
import elf4j.impl.NativeLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LevelRepository {
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
        for (String loggerName = nativeLogger.getName();
             loggerName.length() > 0; loggerName = loggerName.substring(0, (loggerName.lastIndexOf("."))))
            if (loggerNameValueMap.containsKey(loggerName)) {
                return loggerNameValueMap.get(loggerName);
            }
        throw new IllegalArgumentException(String.format("no min level found for key: %s in level map: %s",
                nativeLogger.getName(),
                loggerNameValueMap));
    }
}
