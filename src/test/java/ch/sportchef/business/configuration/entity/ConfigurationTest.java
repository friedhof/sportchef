package ch.sportchef.business.configuration.entity;

import ch.sportchef.test.UnitTests;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Category(UnitTests.class)
public class ConfigurationTest {

    private static Configuration configuration;

    @BeforeClass
    public static void prepareConfiguration() throws IOException {
        final Properties properties = new Properties();
        final InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("cfg_test.properties");
        properties.load(inputStream);
        configuration = new Configuration(properties);
    }

    @AfterClass
    public static void cleanupConfiguration() {
        configuration = null;
    }

}
