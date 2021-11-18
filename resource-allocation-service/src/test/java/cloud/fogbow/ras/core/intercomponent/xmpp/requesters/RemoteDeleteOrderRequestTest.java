package cloud.fogbow.ras.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.core.models.orders.Order;
import com.google.gson.Gson;
import org.dom4j.Element;
import org.jamppa.component.PacketSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

public class RemoteDeleteOrderRequestTest {

    private RemoteDeleteOrderRequest remoteDeleteOrderRequest;

    private Order order;
    private IQ response;
    private PacketSender packetSender;

    private ArgumentCaptor<IQ> iqArgumentCaptor = ArgumentCaptor.forClass(IQ.class);

    @Before
    public void setUp() {
        SystemUser systemUser = new SystemUser("fake-user-id", "fake-user-name", "token-provider"
        );
        this.order = new ComputeOrder(systemUser, "requesting-member",
                "providing-member", "default", "hostName", 10, 20, 30, "imageid",
                null, "publicKey", null);

        this.remoteDeleteOrderRequest = new RemoteDeleteOrderRequest(this.order);
        this.packetSender = Mockito.mock(PacketSender.class);
        PacketSenderHolder.setPacketSender(this.packetSender);
        this.response = new IQ();
    }

    // test case: check if IQ attributes is according to both Order parameters and remote delete order request rules
    @Test
    public void testSend() throws Exception {
        // set up
        Mockito.doReturn(this.response).when(this.packetSender).syncSendPacket(
                iqArgumentCaptor.capture());
        String federationUserJson = new Gson().toJson(this.order.getSystemUser());

        // exercise
        this.remoteDeleteOrderRequest.send();

        // verify
        IQ iq = this.iqArgumentCaptor.getValue();

        Assert.assertEquals(IQ.Type.set.toString(), iq.getType().toString());
        Assert.assertEquals(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + this.order.getProvider().toString(), iq.getTo().toString());
        Assert.assertEquals(this.order.getId(), iq.getID().toString());

        Element iqElementQuery = iq.getElement().element(IqElement.QUERY.toString());
        Assert.assertEquals(RemoteMethod.REMOTE_DELETE_ORDER.toString(),
                iqElementQuery.getNamespaceURI());

        String iqQueryOrderId = iqElementQuery.element(IqElement.ORDER_ID.toString()).getText();
        Assert.assertEquals(this.order.getId(), iqQueryOrderId);

        String iqQueryInstanceType = iqElementQuery.element(IqElement.INSTANCE_TYPE.toString())
                .getText();
        Assert.assertEquals(this.order.getType().toString(), iqQueryInstanceType.toString());

        Element iqElementFederationUser = iq.getElement()
                .element(IqElement.SYSTEM_USER.toString());
        Assert.assertEquals(federationUserJson, iqElementFederationUser.getText());
    }

    // test case: Check if "send" is properly forwading UnavailableProviderException thrown by
    // XmppErrorConditionToExceptionTranslator.handleError when the IQ response is null
    @Test(expected = UnavailableProviderException.class)
    public void testSendWhenResponseIsNull() throws Exception {
        // set up
        Mockito.doReturn(null).when(this.packetSender).syncSendPacket(
                this.iqArgumentCaptor.capture());

        // exercise/verify
        this.remoteDeleteOrderRequest.send();
    }

    // test case: Check if "send" is properly forwarding UnauthorizedRequestException thrown by
    // XmppErrorConditionToExceptionTranslator.handleError when the IQ response status is forbidden
    @Test(expected = UnauthorizedRequestException.class)
    public void testSendWhenResponseReturnsForbidden() throws Exception {
        // set up
        Mockito.doReturn(this.response).when(this.packetSender)
                .syncSendPacket(this.iqArgumentCaptor.capture());
        this.response.setError(new PacketError(PacketError.Condition.forbidden));

        // exercise/verify
        this.remoteDeleteOrderRequest.send();
    }

}
