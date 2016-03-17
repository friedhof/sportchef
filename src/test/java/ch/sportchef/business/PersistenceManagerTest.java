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
package ch.sportchef.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleController.class)
public class PersistenceManagerTest {

    @Test
    public void createSimpleController() {
        // arrange
        mockStatic(SimpleController.class);
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        when(SimpleController.loadOptional(anyObject(), anyObject())).thenReturn(simpleControllerMock);

        // act
        final SimpleController<PersistenceManagerTestClass> simpleController =
                PersistenceManager.createSimpleController(
                        PersistenceManagerTestClass.class, PersistenceManagerTestClass::new);

        // assert
        assertThat(simpleController, is(simpleControllerMock));
        verifyStatic(times(1));
        SimpleController.loadOptional(anyObject(), anyObject());
    }

}