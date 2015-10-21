package ch.sportchef.business.exception;

import javax.validation.constraints.NotNull;

public class ExpectationFailedException extends RuntimeException {

    public ExpectationFailedException(@NotNull final String message) {
        super(message);
    }
}
