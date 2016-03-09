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
package ch.sportchef.business.event.entity;

import ch.sportchef.business.adapter.LocalDateAdapter;
import ch.sportchef.business.adapter.LocalTimeAdapter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long eventId;

    @Size(min = 1)
    private String title;

    private String location;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate date;

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime time;

    private String cssBackgroundColor;

    @Version
    @NotNull
    private Long version;

}
