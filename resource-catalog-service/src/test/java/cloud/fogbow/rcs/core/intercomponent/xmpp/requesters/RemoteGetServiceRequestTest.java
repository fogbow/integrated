package cloud.fogbow.rcs.core.intercomponent.xmpp.requesters;

import org.jamppa.component.PacketSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.xmpp.packet.IQ;

import cloud.fogbow.rcs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.PropertiesHolder;
import cloud.fogbow.rcs.core.TestUtils;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.rcs.core.models.ServiceType;

@PrepareForTest({ RemoteFacade.class })
public class RemoteGetServiceRequestTest extends BaseUnitTests {
    
    private RemoteGetServiceRequest remoteRequest;
    private PacketSender packetSender;
    
    @Before
    public void setUp() {
        this.remoteRequest = Mockito.spy(RemoteGetServiceRequest.builder()
                .member(PropertiesHolder.getInstance()
                        .getProperty(ConfigurationPropertyKeys.LOCAL_MEMBER_ID_KEY))
                .serviceType(ServiceType.MS)
                .build());

        this.packetSender = Mockito.mock(PacketSender.class);
        PacketSenderHolder.setPacketSender(this.packetSender);
    }

    // test case: When calling the send method, it must verify that the call was
    // successful.
    @Test
    public void testSend() throws Exception {
        // set up
        String content = TestUtils.FAKE_CONTENT_JSON;
        String key = TestUtils.FAKE_MEMBER_SERVICE_KEY;
        String senderId = TestUtils.FAKE_SENDER_ID;

        IQ response = this.testUtils.getRemoteResponse();
        Mockito.doReturn(response).when(this.packetSender).syncSendPacket(Mockito.any(IQ.class));

        RemoteFacade facade = this.testUtils.mockRemoteFacade();
        Mockito.doNothing().when(facade).cacheSave(Mockito.eq(key), Mockito.eq(content));

        // exercise
        this.remoteRequest.send();

        // verify
        Mockito.verify(this.remoteRequest, Mockito.times(TestUtils.RUN_ONCE)).marshal(Mockito.eq(senderId));
        Mockito.verify(this.remoteRequest, Mockito.times(TestUtils.RUN_ONCE)).unmarshal(Mockito.eq(response));

        Mockito.verify(facade, Mockito.times(TestUtils.RUN_ONCE)).cacheSave(key, content);
    }

}
