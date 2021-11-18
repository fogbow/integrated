package cloud.fogbow.rcs.core.intercomponent.xmpp;

import org.jamppa.component.XMPPComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.xmpp.component.ComponentException;

import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.PropertiesHolder;
import cloud.fogbow.rcs.core.TestUtils;


@PrepareForTest({ PacketSenderHolder.class, PropertiesHolder.class })
public class PacketSenderHolderTest extends BaseUnitTests {

    private static final String METHOD_BUILD_XMPP_COMPONENT_MANAGER = "buildXmppComponentManager";
    private static final String METHOD_CONNECT_PACKET_SENDER = "connectPacketSender";
    private static final String METHOD_INIT = "init";
    private static final String METHOD_GET_PACKET_SENDER = "getPacketSender";
    private static final String METHOD_GET_XMPP_PASSWORD = "getXmppPassword";
    private static final String METHOD_GET_XMPP_PORT = "getXmppServerPort";
    private static final String METHOD_GET_XMPP_PROVIDER_ID = "getXmppProviderId";
    private static final String METHOD_GET_XMPP_SERVER_IP = "getXmppServerIp";
    private static final String METHOD_GET_XMPP_TIMEOUT = "getXmppTimeout";
    
    private static final String XMPP_JID = "rcs-member1";
    private static final String XMPP_PASSWORD = "fake-pass";
    private static final String XMPP_SERVER_IP = "fake-server-ip";
    private static final int XMPP_C2C_PORT = 5347;
    private static final long XMPP_TIMEOUT = 5000;

    @Before
    public void setup() {
        PowerMockito.mockStatic(PacketSenderHolder.class);
    }
    
    // test case: When calling the init method, it must verify if the
    // connectPacketSender method has been called.
    @Test
    public void testInitSuccessFul() throws Exception {
        // setup
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_INIT);

        XMPPComponent component = Mockito.mock(XmppComponentManager.class);
        PowerMockito.doReturn(component).when(PacketSenderHolder.class, METHOD_CONNECT_PACKET_SENDER);

        // exercise
        PacketSenderHolder.init();

        // verify
        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.connectPacketSender();
    }
    
    // test case: When calling the connectPacketSender method, it must verify if the
    // connect method from XMPPComponent has been called.
    @Test
    public void testConnectPacketSender() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_CONNECT_PACKET_SENDER);

        XMPPComponent component = Mockito.mock(XmppComponentManager.class);
        PowerMockito.doReturn(component).when(PacketSenderHolder.class, METHOD_BUILD_XMPP_COMPONENT_MANAGER);

        // exercise
        PacketSenderHolder.connectPacketSender();

        // verify
        Mockito.verify(component, Mockito.times(TestUtils.RUN_ONCE)).connect();
    }
    
    // test case: When calling the connectPacketSender method and an error occurs
    // while establishing a connection to the XMPP component, the
    // IllegalStateException must be thrown.
    @Test(expected = IllegalStateException.class) // Verify
    public void testConnectPacketSenderFail() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_CONNECT_PACKET_SENDER);

        XMPPComponent component = Mockito.mock(XmppComponentManager.class);
        PowerMockito.doReturn(component).when(PacketSenderHolder.class, METHOD_BUILD_XMPP_COMPONENT_MANAGER);
        Mockito.doThrow(new ComponentException()).when(component).connect();

        // exercise
        PacketSenderHolder.connectPacketSender();
    }
    
    // test case: When calling the buildXmppComponentManager method, it must verify
    // that the call was successful.
    @Test
    public void testBuildXmppComponentManager() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_BUILD_XMPP_COMPONENT_MANAGER);

        PowerMockito.doReturn(XMPP_JID).when(PacketSenderHolder.class, METHOD_GET_XMPP_PROVIDER_ID);
        PowerMockito.doReturn(XMPP_PASSWORD).when(PacketSenderHolder.class, METHOD_GET_XMPP_PASSWORD);
        PowerMockito.doReturn(XMPP_SERVER_IP).when(PacketSenderHolder.class, METHOD_GET_XMPP_SERVER_IP);
        PowerMockito.doReturn(XMPP_C2C_PORT).when(PacketSenderHolder.class, METHOD_GET_XMPP_PORT);
        PowerMockito.doReturn(XMPP_TIMEOUT).when(PacketSenderHolder.class, METHOD_GET_XMPP_TIMEOUT);

        // exercise
        XMPPComponent component = PacketSenderHolder.buildXmppComponentManager();

        // verify
        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.getXmppProviderId();

        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.getXmppPassword();

        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.getXmppServerIp();

        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.getXmppServerPort();

        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.getXmppTimeout();

        Assert.assertNotNull(component);
    }
    
    // test case: When calling the getXmppTimeout method, it must verify that the
    // expected value has been returned.
    @Test
    public void testGetXmppTimeout() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_XMPP_TIMEOUT);

        long expected = XMPP_TIMEOUT;

        // exercise
        long timeout = PacketSenderHolder.getXmppTimeout();

        // verify
        Assert.assertEquals(expected, timeout);
    }
    
    // test case: When calling the getXmppServerPort method, it must verify that the
    // expected value has been returned.
    @Test
    public void testGetXmppServerPort() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_XMPP_PORT);

        int expected = XMPP_C2C_PORT;

        // exercise
        int port = PacketSenderHolder.getXmppServerPort();

        // verify
        Assert.assertEquals(expected, port);
    }
    
    // test case: When calling the getXmppServerIp method, it must verify that the
    // expected value has been returned.
    @Test
    public void testGetXmppServerIp() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_XMPP_SERVER_IP);

        String expected = XMPP_SERVER_IP;

        // exercise
        String serverIp = PacketSenderHolder.getXmppServerIp();

        // verify
        Assert.assertEquals(expected, serverIp);
    }
    
    // test case: When calling the getXmppPassword method, it must verify that the
    // expected value has been returned.
    @Test
    public void testGetXmppPassword() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_XMPP_PASSWORD);

        String expected = XMPP_PASSWORD;

        // exercise
        String serverIp = PacketSenderHolder.getXmppPassword();

        // verify
        Assert.assertEquals(expected, serverIp);
    }
    
    // test case: When calling the getXmppProviderId method, it must verify that the
    // expected value has been returned.
    @Test
    public void testGetXmppProviderId() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_XMPP_PROVIDER_ID);

        String expected = XMPP_JID;

        // exercise
        String serverIp = PacketSenderHolder.getXmppProviderId();

        // verify
        Assert.assertEquals(expected, serverIp);
    }
    
    // test case: When calling the getPacketSender method, it must verify if the
    // init method has been called.
    @Test
    public void testGetPacketSender() throws Exception {
        // set up
        PowerMockito.doCallRealMethod().when(PacketSenderHolder.class, METHOD_GET_PACKET_SENDER);

        // exercise
        PacketSenderHolder.getPacketSender();

        // verify
        PowerMockito.verifyStatic(PacketSenderHolder.class, Mockito.times(TestUtils.RUN_ONCE));
        PacketSenderHolder.init();
    }

}
