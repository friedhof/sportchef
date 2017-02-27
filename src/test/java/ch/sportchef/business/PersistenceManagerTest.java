/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016, 2017 Marcus Fihlon
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
package ch.sportchef.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.PersistenceController;
import pl.setblack.airomem.core.builders.PrevaylerBuilder;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PrevaylerBuilder.class)
public class PersistenceManagerTest {

    @Test
    public void createController() {
        // arrange
        mockStatic(PrevaylerBuilder.class);
        @SuppressWarnings("unchecked")
        final PersistenceController<Serializable> controllerMock = mock(PersistenceController.class);
        final PrevaylerBuilder builderMock = mock(PrevaylerBuilder.class);
        when(PrevaylerBuilder.newBuilder()).thenReturn(builderMock);
        when(builderMock.withFolder(anyObject())).thenReturn(builderMock);
        when(builderMock.useSupplier(anyObject())).thenReturn(builderMock);
        when(builderMock.build()).thenReturn(controllerMock);

        // act
        final PersistenceController<PersistenceManagerTestClass> persistenceController =
                PersistenceManager.createController(
                        PersistenceManagerTestClass.class, PersistenceManagerTestClass::new);

        // assert
        assertThat("The PersistenceManager should return the mock object.",
                persistenceController, is(controllerMock));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUtilityClassConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // arrange
        Constructor<PersistenceManager> constructor;
        constructor = PersistenceManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // act
        constructor.newInstance();

        // assert
    }

}
