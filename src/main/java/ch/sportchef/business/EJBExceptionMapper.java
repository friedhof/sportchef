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
