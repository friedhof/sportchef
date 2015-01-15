package ch.sportchef.business;

import javax.ejb.EJBException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    @Override
    public Response toResponse(EJBException e) {
        Response unknownError = Response.serverError()
                .header("cause", e.getMessage())
                .build();

        Throwable cause = e.getCause();

        if (cause == null) {
            return unknownError;
        } else if (e.getCause() instanceof EntityNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("cause", cause.getMessage())
                    .build();
        } else if (cause instanceof OptimisticLockException) {
            return Response.status(Response.Status.CONFLICT)
                    .header("cause", cause.getMessage())
                    .build();
        }

        return unknownError;
    }
}