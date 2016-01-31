/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015, 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    private static final String LOCAL_TIME_FORMAT = "%02d:%02d"; //NON-NLS

    @Override
    public String marshal(final LocalTime localTime) {
        return String.format(LOCAL_TIME_FORMAT,
                localTime.getHour(), localTime.getMinute());
    }

    @Override
    public LocalTime unmarshal(final String timeString) {
        final String[] data = timeString.split(":");
        final int hour = Integer.parseInt(data[0]);
        final int minute = Integer.parseInt(data[1]);
        return LocalTime.of(hour, minute);
    }

}
