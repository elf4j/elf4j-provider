package elf4j.impl.util;

import java.util.*;

public class PropertiesUtils {
    public static final char DOT = '.';

    private PropertiesUtils() {
    }

    public static List<Map<String, String>> getPropertiesGroupOfType(String type, Properties properties) {
        List<String> typeKeys = new ArrayList<>();
        properties.stringPropertyNames()
                .stream()
                .filter(name -> properties.getProperty(name).equals(type))
                .forEach(typeKeys::add);
        List<Map<String, String>> propertiesGroup = new ArrayList<>();
        typeKeys.forEach(k -> propertiesGroup.add(getChildProperties(k, properties)));
        return propertiesGroup;
    }

    public static Map<String, String> getChildProperties(String prefix, Properties properties) {
        Map<String, String> childProperties = new HashMap<>();
        String parentPrefix = prefix + DOT;
        properties.stringPropertyNames()
                .stream()
                .filter(name -> name.startsWith(parentPrefix))
                .forEach(name -> childProperties.put(name.substring(name.indexOf(DOT) + 1),
                        properties.getProperty(name)));
        return childProperties;
    }
}