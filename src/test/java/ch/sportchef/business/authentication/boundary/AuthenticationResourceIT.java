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
package ch.sportchef.business.authentication.boundary;

import ch.sportchef.test.IntegrationTests;
import ch.sportchef.test.TestData;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.experimental.categories.Category;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;

@Category(IntegrationTests.class)
public class AuthenticationResourceIT {

    @ClassRule
    public static final JAXRSClientProvider PROVIDER = buildWithURI("http://localhost:8080/sportchef/api/users");

    private static final String TEST_USER_EMAIL = "auth.test@sportchef.ch";

    private static String testUserLocation;

    @BeforeClass
    public static void createTestUser() {
        testUserLocation = TestData.createTestUser("AuthTest", "AuthTest", "AuthTest",  TEST_USER_EMAIL, false);
    }

    @AfterClass
    public static void deleteTestUser() {
        TestData.deleteTestUser(testUserLocation);
    }

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/authentication");

}
