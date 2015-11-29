/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public final String marshal(final LocalDate localDate) {
        return String.format("%04d-%02d-%02d",
                localDate.getYear(), localDate.getMonth().getValue(), localDate.getDayOfMonth());
    }

    @Override
    public final LocalDate unmarshal(final String string) throws NumberFormatException {
        final String[] data = string.split("\\-");
        final int year = Integer.parseInt(data[0]);
        final int month = Integer.parseInt(data[1]);
        final int dayOfMonth = Integer.parseInt(data[2]);
        return LocalDate.of(year, month, dayOfMonth);
    }

}
