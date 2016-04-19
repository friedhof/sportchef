/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
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
package ch.sportchef.business;

import ch.sportchef.business.authentication.entity.Role;
import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Singleton
@Startup
public class AutoInstall {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private UserService userService;

    @Inject
    private EventService eventService;

    @Inject
    private EventImageService eventImageService;

    @PostConstruct
    private void setup() {
        createAdminUser();
        createFirstEvent();
    }

    private void createAdminUser() {
        final Configuration configuration = configurationService.getConfiguration();
        final String adminEmail = configuration.getAdminEmail();
        final Optional<User> adminUser = userService.findByEmail(adminEmail);
        if (!adminUser.isPresent()) {
            final String firstname = configuration.getAdminFirstname();
            final String lastname = configuration.getAdminLastname();
            final String phone = configuration.getAdminPhone();
            final User admin = User.builder()
                    .firstName(firstname)
                    .lastName(lastname)
                    .email(adminEmail)
                    .phone(phone)
                    .role(Role.ADMIN)
                    .build();
            userService.create(admin);
        }
    }

    private void createFirstEvent() {
        final List<Event> events = eventService.findAll();
        if (events.isEmpty()) {
            final Event newEvent = Event.builder()
                    .title("Testevent")
                    .location("Testlocation")
                    .date(LocalDate.now())
                    .time(LocalTime.now())
                    .build();
            final Event event = eventService.create(newEvent);
            eventImageService.chooseRandomDefaultImage(event.getEventId());
        }
    }

}
