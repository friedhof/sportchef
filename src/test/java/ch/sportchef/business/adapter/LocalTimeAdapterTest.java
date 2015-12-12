package ch.sportchef.business.adapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LocalTimeAdapterTest {

    private static final String TIME_TEST_STRING = "13:02";
    private static final LocalTime TIME_TEST_OBJECT = LocalTime.of(13, 2);

    private static LocalTimeAdapter adapter;

    @BeforeClass
    public static void beforeClass() {
        adapter = new LocalTimeAdapter();
    }

    @AfterClass
    public static void afterClass() {
        adapter = null;
    }

    @Test
    public void marshal() throws Exception {
        assertThat(adapter.marshal(TIME_TEST_OBJECT), is(TIME_TEST_STRING));
    }

    @Test
    public void unmarshal() throws Exception {
        assertThat(adapter.unmarshal(TIME_TEST_STRING), is(TIME_TEST_OBJECT));
    }
}
