package cloud.fogbow.rcs.core.intercomponent.xmpp;

import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.NoAvailableResourcesException;
import cloud.fogbow.common.exceptions.QuotaExceededException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import cloud.fogbow.rcs.constants.Messages;

public class XmppErrorConditionToExceptionTranslator {

    public static void handleError(IQ response, String providerId) throws FogbowException {
        if (response == null) {
            throw new UnavailableProviderException(
                    String.format(Messages.Exception.UNABLE_TO_RETRIEVE_RESPONSE_FROM_MEMBER_S, providerId));
        } else if (response.getError() != null) {
            PacketError.Condition condition = response.getError().getCondition();
            String message = response.getError().getText();
            XmppErrorConditionToExceptionTranslator.throwException(condition, message);
        }
    }

    @VisibleForTesting
    static void throwException(PacketError.Condition condition, String message) throws FogbowException {
        switch (condition) {
        case forbidden:
            throw new UnauthorizedRequestException(message);
        case not_authorized:
            throw new UnauthenticatedUserException(message);
        case bad_request:
            throw new InvalidParameterException(message);
        case item_not_found:
            throw new InstanceNotFoundException(message);
        case conflict:
            throw new QuotaExceededException(message);
        case not_acceptable:
            throw new NoAvailableResourcesException(message);
        case remote_server_not_found:
            throw new UnavailableProviderException(message);
        case internal_server_error:
            throw new UnexpectedException(message);
        default:
            throw new FogbowException(message);
        }
    }
}
