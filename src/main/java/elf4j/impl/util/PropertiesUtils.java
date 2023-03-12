package elf4j.impl.util;

import java.util.*;

/**
 *
 */
public class PropertiesUtils {

    private PropertiesUtils() {
    }

    /**
     * @param prefix     all properties whose keys start with this prefix are to be searched for
     * @param properties search source
     * @return all properties whose keys start with the specified prefix
     */
    public static Map<String, String> getChildProperties(String prefix, Properties properties) {
        Map<String, String> childProperties = new HashMap<>();
        String parentPrefix = prefix + '.';
        properties.stringPropertyNames()
                .stream()
                .filter(name -> name.trim().startsWith(parentPrefix))
                .forEach(name -> childProperties.put(name.substring(name.indexOf('.') + 1).trim(),
                        properties.getProperty(name).trim()));
        return childProperties;
    }

    /**
     * @param type       the properties value whose key is used as parent prefix
     * @param properties search source
     * @return properties group each member of which is a set of properties having a unique key prefix of the specified
     *         type
     */
    public static List<Map<String, String>> getPropertiesGroupOfType(String type, Properties properties) {
        List<String> typeKeys = new ArrayList<>();
        properties.stringPropertyNames()
                .stream()
                .filter(name -> properties.getProperty(name).trim().equals(type))
                .forEach(typeKeys::add);
        List<Map<String, String>> propertiesGroup = new ArrayList<>();
        typeKeys.forEach(k -> propertiesGroup.add(getChildProperties(k, properties)));
        return propertiesGroup;
    }
}