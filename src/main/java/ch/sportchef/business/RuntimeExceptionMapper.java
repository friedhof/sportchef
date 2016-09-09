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
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.EXPECTATION_FAILED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(final RuntimeException exception) {
        final Error error = searchForError(exception);
        return Response.status(error.getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new HashMap<String, String>() { {
                    put("error", error.getThrowable().getMessage());
                } }).build();
    }

    private Error searchForError(@NotNull final Throwable cause) {
        Error error = null;

        if (cause instanceof ConcurrentModificationException) {
            error = new Error(CONFLICT, cause);
        } else if (cause instanceof ExpectationFailedException) {
            error = new Error(EXPECTATION_FAILED, cause);
        } else if (cause instanceof NotFoundException) {
            error = new Error(NOT_FOUND, cause);
        }

        // recursion if needed
        if (error == null) {
            final Throwable subCause = cause.getCause();
            if (subCause != null) {
                error = searchForError(subCause);
            }
        }

        // fallback
        if (error == null) {
            error = new Error(INTERNAL_SERVER_ERROR, cause);
        }

        return error;
    }

    @Value
    private static final class Error {
        private Status status;
        private Throwable throwable;
    }
}
