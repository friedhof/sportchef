package ch.sportchef.business.adapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static java.time.Month.AUGUST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LocalDateAdapterTest {

    private static final String DATE_TEST_STRING = "2015-08-31";
    private static final LocalDate DATE_TEST_OBJECT = LocalDate.of(2015, AUGUST, 31);

    private static LocalDateAdapter adapter;

    @BeforeClass
    public static void beforeClass() {
        adapter = new LocalDateAdapter();
    }

    @AfterClass
    public static void afterClass() {
        adapter = null;
    }

    @Test
    public void marshal() throws Exception {
        assertThat(adapter.marshal(DATE_TEST_OBJECT), is(DATE_TEST_STRING));
    }

    @Test
    public void unmarshal() throws Exception {
        assertThat(adapter.unmarshal(DATE_TEST_STRING), is(DATE_TEST_OBJECT));
    }
}
