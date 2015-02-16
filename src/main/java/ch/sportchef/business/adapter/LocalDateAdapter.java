package ch.sportchef.business.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public String marshal(final LocalDate localDate) throws Exception {
        return String.format("%04d-%02d-%02d",
                localDate.getYear(), localDate.getMonth().getValue(), localDate.getDayOfMonth());
    }

    @Override
    public LocalDate unmarshal(final String string) throws Exception {
        final String[] data = string.split("\\-");
        final int year = Integer.parseInt(data[0]);
        final int month = Integer.parseInt(data[1]);
        final int dayOfMonth = Integer.parseInt(data[2]);
        return LocalDate.of(year, month, dayOfMonth);
    }

}
