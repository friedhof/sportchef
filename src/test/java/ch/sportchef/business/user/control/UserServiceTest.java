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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.user.control;

import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.metrics.healthcheck.UserServiceHealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;
import pl.setblack.airomem.core.VoidCommand;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.io.Serializable;
import java.security.Principal;
import java.util.Optional;

import static ch.sportchef.business.authentication.entity.Role.USER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PersistenceManager.class)
public class UserServiceTest {

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest(postConstruct = true)
    private UserService userService; // used only for postConstruct test

    @Inject
    private HealthCheckRegistry healthCheckRegistryMock;

    @Test
    public void postConstruct() {
        // arrange

        // act

        // assert
        verify(healthCheckRegistryMock, times(1)).register(
                eq(UserService.class.getName()),
                any(UserServiceHealthCheck.class));
    }

    @Test
    public void create() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        final User userToCreate = User.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+41 79 555 00 01")
                .email("john.doe@sportchef.ch")
                .build();

        // act
        userService.create(userToCreate);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void update() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        final User userToUpdate = User.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+41 79 555 00 01")
                .email("john.doe@sportchef.ch")
                .build();

        // act
        userService.update(userToUpdate);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void findByUserId() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();

        // act
        userService.findByUserId(1L);

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(userRepositoryMock, times(1)).findByUserId(1L);
    }

    @Test
    public void findByEmail() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();

        // act
        userService.findByEmail("john.doe@sportchef.ch");

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(userRepositoryMock, times(1)).findByEmail("john.doe@sportchef.ch");
    }

    @Test
    public void findAll() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();

        // act
        userService.findAll();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void delete() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();

        // act
        userService.delete(1L);

        // assert
        verify(simpleControllerMock, times(1)).execute(any(VoidCommand.class));
    }

    @Test
    public void getAuthenticatedUser() {
        // arrange
        final String email = "foo@bar";
        final User testUser = User.builder().email(email).role(USER).build();
        final Principal principalMock = mock(Principal.class);
        when(principalMock.getName()).thenReturn(email);
        final SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getUserPrincipal()).thenReturn(principalMock);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(testUser));
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();

        // act
        final Optional<User> authenticatedUser = userService.getAuthenticatedUser(securityContextMock);

        // assert
        assertThat(authenticatedUser.get(), is(testUser));
    }
}