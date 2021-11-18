package cloud.fogbow.common.exceptions;

import cloud.fogbow.common.constants.Messages;

public class UnauthenticatedUserException extends FogbowException {
    private static final long serialVersionUID = 1L;

    public UnauthenticatedUserException() {
        super(Messages.Exception.AUTHENTICATION_ERROR);
    }

    public UnauthenticatedUserException(String message) {
        super(message);
    }
}
