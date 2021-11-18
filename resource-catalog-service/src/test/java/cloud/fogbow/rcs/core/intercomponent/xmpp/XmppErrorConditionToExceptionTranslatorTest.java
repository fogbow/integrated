package cloud.fogbow.rcs.core.intercomponent.xmpp;

import org.junit.Assert;
import org.junit.Test;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

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

public class XmppErrorConditionToExceptionTranslatorTest {

    private final String providerId = "provider-id";
    private final String messageError = "message-error";
    
    // test case: checks if "handleError" is properly forwading
    // "UnauthorizedRequestException" from "throwException" when the packet error
    // condition is equals to "forbidden". In addition, it checks if its message
    // error is correct.
    @Test
    public void testHandleErrorThrowsUnauthorizedRequestException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.forbidden;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (UnauthorizedRequestException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "UnauthenticatedUserException" from "throwException" when the packet error
    // condition is equals to "not_authorized". In addition, it checks if its
    // message error is correct.
    @Test
    public void testHandleErrorThrowsUnauthenticatedUserException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.not_authorized;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (UnauthenticatedUserException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "InvalidParameterExceptionException" from "throwException" when the packet
    // error condition is equals to "bad_request". In addition, it checks if its
    // message error is correct.
    @Test
    public void testHandleErrorThrowsInvalidParameterExceptionException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.bad_request;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (InvalidParameterException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "InstanceNotFoundException" from "throwException" when the packet error
    // condition is equals to "item_not_found". In addition, it checks if its
    // message error is correct.
    @Test
    public void testHandleErrorThrowsInstanceNotFoundException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.item_not_found;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (InstanceNotFoundException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "QuotaExceededException" from "throwException" when the packet error
    // condition is equals to "conflict". In addition, it checks if its message
    // error is correct.
    @Test
    public void testHandleErrorThrowsQuotaExceededException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.conflict;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (QuotaExceededException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "NoAvailableResourcesException" from "throwException" when the packet error
    // condition is equals to "not_acceptable". In addition, it checks if its
    // message error is correct.
    @Test
    public void testHandleErrorThrowsNoAvailableResourcesException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.not_acceptable;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (NoAvailableResourcesException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "UnavailableProviderException" from "throwException" when the packet error
    // condition is equals to "remote_server_not_found". In addition, it checks if
    // its message error is correct.
    @Test
    public void testHandleErrorThrowsUnavailableProviderException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.remote_server_not_found;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (UnavailableProviderException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "UnexpectedException" from "throwException" when the packet error condition
    // is equals to "internal_server_error". In addition, it checks if its message
    // error is correct.
    @Test
    public void testHandleErrorThrowsUnexpectedException() throws FogbowException {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.internal_server_error;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (UnexpectedException e) {
            // verify: if the message is correct
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading "Exception" from
    // "throwException" when the packet error condition is not equals to any switch
    // case attribute. In addition, it checks if its message error is correct.
    @Test
    public void testHandleErrorThrowsException() {
        // set up
        IQ iq = new IQ();
        PacketError.Condition condition = PacketError.Condition.undefined_condition;
        PacketError packetError = createPaketErrorFrom(condition);
        iq.setError(packetError);

        try {
            // exercise
            XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
            // verify: if some exception occurred
            Assert.fail();
        } catch (Exception e) {
            // verify: if the message is correct
            Assert.assertEquals(e.getClass(), FogbowException.class);
            Assert.assertEquals(this.messageError, e.getMessage());
        }
    }

    // test case: checks if "handleError" is properly forwading
    // "UnavailableProviderException" when the response is null.
    @Test
    public void testHandleErrorThrowsUnavailableProviderExceptionWhenResponseIsNull() throws Exception {
        try {
            // exercise
            IQ response = null;
            XmppErrorConditionToExceptionTranslator.handleError(response, this.providerId);
            // verify
            Assert.fail();
        } catch (UnavailableProviderException e) {
            String expected = String.format(Messages.Exception.UNABLE_TO_RETRIEVE_RESPONSE_FROM_MEMBER_S,
                    this.providerId);
            Assert.assertEquals(expected, e.getMessage());
        }
    }

    //test case: checks if nothing happens if there is no error in response.
    @Test
    public void testHandleErrorWhenThereIsNoError() throws Exception {
        //set up
        IQ iq = new IQ();
        //exercise//verify
        XmppErrorConditionToExceptionTranslator.handleError(iq, this.providerId);
    }
    
    private PacketError createPaketErrorFrom(PacketError.Condition condition) {
        PacketError.Type type = null;
        return new PacketError(condition, type, this.messageError);
    }

}
