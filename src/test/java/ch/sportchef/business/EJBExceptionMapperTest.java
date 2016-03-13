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

import ch.sportchef.business.exception.ExpectationFailedException;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.EJBException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.EXPECTATION_FAILED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EJBExceptionMapperTest {

    private EJBExceptionMapper ejbExceptionMapper;

    @Before
    public void setUp() {
        ejbExceptionMapper = new EJBExceptionMapper();
    }

    @Test
    public void testExpectationFailedException() {
        // arrange
        final String message = "This is an exception message!";
        final ExpectationFailedException exception = new ExpectationFailedException(message);
        final EJBException ejbException = new EJBException(exception);

        // act
        final Response response = ejbExceptionMapper.toResponse(ejbException);

        // assert
        assertThat(response.getStatus(), is(EXPECTATION_FAILED.getStatusCode()));
        assertThat(response.getHeaderString("Expectation"), is(message));
    }

    @Test
    public void testNotFoundException() {
        // arrange
        final String message = "This is an exception message!";
        final NotFoundException exception = new NotFoundException(message);
        final EJBException ejbException = new EJBException(exception);

        // act
        final Response response = ejbExceptionMapper.toResponse(ejbException);

        // assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
        assertThat(response.getHeaderString("Cause"), is(message));
    }

    @Test
    public void testInternalServerError() {
        // arrange
        final String message = "This is an exception message!";
        final NullPointerException exception = new NullPointerException(message);
        final EJBException ejbException = new EJBException(exception);

        // act
        final Response response = ejbExceptionMapper.toResponse(ejbException);

        // assert
        assertThat(response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.getHeaderString("Cause"), is("java.lang.NullPointerException: " + message));
    }
}