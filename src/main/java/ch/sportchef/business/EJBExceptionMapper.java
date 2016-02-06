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
package ch.sportchef.business;

import ch.sportchef.business.exception.ExpectationFailedException;

import javax.ejb.EJBException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.EXPECTATION_FAILED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    @Override
    public Response toResponse(final EJBException exception) {
        Response response = createResponse(exception);
        if (response == null) {
            response = Response.status(INTERNAL_SERVER_ERROR)
                    .header("Cause", exception.getMessage())
                    .build();
        }
        return response;
    }

    private Response createResponse(@NotNull final Throwable cause) {
        Response response = null;

        if (cause instanceof ExpectationFailedException) {
            response = Response.status(EXPECTATION_FAILED)
                    .header("Expectation", cause.getMessage())
                    .build();
        } else if (cause instanceof NotFoundException) {
            response = Response.status(NOT_FOUND)
                    .header("Cause", cause.getMessage())
                    .build();
        }

        // recursion if needed
        if (response == null) {
            final Throwable subCause = cause.getCause();
            if (subCause != null) {
                response = createResponse(subCause);
            }
        }

        return response;
    }

}
