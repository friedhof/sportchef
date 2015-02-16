package ch.sportchef.business.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public String marshal(final LocalTime localTime) throws Exception {
        return String.format("%02d:%02d",
                localTime.getHour(), localTime.getMinute());
    }

    @Override
    public LocalTime unmarshal(final String string) throws Exception {
        final String[] data = string.split("\\:");
        final int hour = Integer.parseInt(data[0]);
        final int minute = Integer.parseInt(data[1]);
        return LocalTime.of(hour, minute);
    }

}
