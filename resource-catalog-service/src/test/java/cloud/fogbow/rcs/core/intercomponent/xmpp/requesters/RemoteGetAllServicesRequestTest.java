package cloud.fogbow.rcs.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.util.GsonHolder;
import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.TestUtils;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.models.ServiceType;
import org.dom4j.Element;
import org.jamppa.component.PacketSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class RemoteGetAllServicesRequestTest extends BaseUnitTests {

    private RemoteGetAllServicesRequest remoteRequest;
    private PacketSender packetSender;

    @Before
    public void setUp() {
        this.remoteRequest = Mockito.spy(RemoteGetAllServicesRequest.builder()
                .member("member1")
                .build());

        this.packetSender = Mockito.mock(PacketSender.class);
        PacketSenderHolder.setPacketSender(this.packetSender);
    }

    // test case: check if the method makes the expected calls
    @Test
    public void testSend() throws Exception {
        // set up
        String senderId = TestUtils.FAKE_SENDER_ID;

        IQ response = getRemoteResponse();
        Mockito.doReturn(response).when(this.packetSender).syncSendPacket(Mockito.any(IQ.class));

        // exercise
        this.remoteRequest.send();

        // verify
        Mockito.verify(this.remoteRequest, Mockito.times(TestUtils.RUN_ONCE)).marshal(Mockito.eq(senderId));
        Mockito.verify(this.remoteRequest, Mockito.times(TestUtils.RUN_ONCE)).unmarshal(Mockito.eq(response));
    }

    private IQ getRemoteResponse() {
        IQ response = new IQ();
        Element queryElement = response.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_SERVICE.toString());
        Element contentElement = queryElement.addElement(IqElement.CONTENT.toString());
        List<ServiceType> content = new ArrayList<>();
        content.add(ServiceType.AS);
        content.add(ServiceType.RAS);
        contentElement.setText(GsonHolder.getInstance().toJson(content));
        return response;
    }

}