package ch.sportchef.business.configuration.entity;

import ch.sportchef.test.UnitTests;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

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

    @Test
    public void getContactCompany() {
        assertThat(configuration.getContactCompany(), is("My Company"));
    }

    @Test
    public void getContactName() {
        assertThat(configuration.getContactName(), is("My Name"));
    }

    @Test
    public void getContactStreet() {
        assertThat(configuration.getContactStreet(), is("My Street"));
    }

    @Test
    public void getContactCity() {
        assertThat(configuration.getContactCity(), is("My City"));
    }

    @Test
    public void getContactPhone() {
        assertThat(configuration.getContactPhone(), is("My Phone"));
    }

    @Test
    public void getContactEmail() {
        assertThat(configuration.getContactEmail(), is("My Email"));
    }

    @Test
    public void getContactWeb() {
        assertThat(configuration.getContactWeb(), is("My Web"));
    }

}
