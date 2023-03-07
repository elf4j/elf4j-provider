package elf4j.impl.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class PropertiesLoader {
    public static final String ELF4J_PROPERTIES_LOCATION = "elf4j.properties.location";
    private static final Properties DEFAULT_PROPERTIES = new Properties();
    private static final String[] DEFAULT_PROPERTIES_LOCATIONS =
            new String[] { "/elf4j-test.properties", "/elf4j.properties" };

    static {
        DEFAULT_PROPERTIES.setProperty("writer.default", "console");
    }

    public Properties load() {
        Properties properties = new Properties(DEFAULT_PROPERTIES);
        String propertiesLocation = System.getProperty(ELF4J_PROPERTIES_LOCATION);
        InputStream propertiesInputStream;
        if (propertiesLocation == null) {
            propertiesInputStream = fromDefaultPropertiesLocation();
            if (propertiesInputStream == null) {
                return properties;
            }
        } else {
            propertiesInputStream = getClass().getResourceAsStream(propertiesLocation);
            if (propertiesInputStream == null) {
                throw new IllegalArgumentException(
                        "Null resource stream from properties location: " + propertiesLocation);
            }
        }
        try {
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            throw new UncheckedIOException("Error loading properties stream from location: " + propertiesLocation, e);
        }
        return properties;
    }

    private InputStream fromDefaultPropertiesLocation() {
        return Arrays.stream(DEFAULT_PROPERTIES_LOCATIONS)
                .map(location -> getClass().getResourceAsStream(location))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
