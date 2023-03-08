package elf4j.impl.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class PropertiesLoader {
    public static final String ELF4J_PROPERTIES_LOCATION = "elf4j.properties.location";
    private static final String[] DEFAULT_PROPERTIES_LOCATIONS =
            new String[] { "/elf4j-test.properties", "/elf4j.properties" };

    public Properties load() {
        Properties properties = new Properties();
        InputStream propertiesInputStream;
        final String customPropertiesLocation = System.getProperty(ELF4J_PROPERTIES_LOCATION);
        if (customPropertiesLocation == null) {
            propertiesInputStream = fromDefaultPropertiesLocation();
            if (propertiesInputStream == null) {
                return properties;
            }
        } else {
            propertiesInputStream = getClass().getResourceAsStream(customPropertiesLocation);
            if (propertiesInputStream == null) {
                throw new IllegalArgumentException(
                        "Null resource stream from specified properties location: " + customPropertiesLocation);
            }
        }
        try {
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Error loading properties stream from location: " + (customPropertiesLocation == null ?
                            "default location" : customPropertiesLocation), e);
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
