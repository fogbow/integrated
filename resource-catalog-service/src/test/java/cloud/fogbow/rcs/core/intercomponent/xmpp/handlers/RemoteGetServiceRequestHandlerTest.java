package cloud.fogbow.rcs.core.intercomponent.xmpp.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.xmpp.packet.IQ;

import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.rcs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.PropertiesHolder;
import cloud.fogbow.rcs.core.TestUtils;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.models.ServiceType;

@PrepareForTest({ RemoteFacade.class })
public class RemoteGetServiceRequestHandlerTest extends BaseUnitTests {

    private RemoteGetServiceRequestHandler remoteHandler;
    
    @Before
    public void setUp() {
        this.remoteHandler = Mockito.spy(new RemoteGetServiceRequestHandler());
    }

    // test case: When calling the handle method, it must verify that the call was
    // successful.
    @Test
    public void testHandle() throws Exception {
        // set up
        String content = TestUtils.FAKE_CONTENT_JSON;
        String member = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.LOCAL_MEMBER_ID_KEY);
        ServiceType serviceType = ServiceType.MS;

        IQ iq = this.testUtils.generateRemoteRequest(member, serviceType.getName());
        IQ iqResponse = this.testUtils.getRemoteResponseHandle(iq);
        Mockito.doReturn(iqResponse).when(this.remoteHandler).getResultiIQ(iq);
        
        RemoteFacade facade = this.testUtils.mockRemoteFacade();

        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        Mockito.doReturn(httpResponse).when(facade).requestService(Mockito.any(), Mockito.any());
        Mockito.when(httpResponse.getContent()).thenReturn(content);

        // exercise
        this.remoteHandler.handle(iq);

        // verify
        Mockito.verify(this.remoteHandler, Mockito.times(TestUtils.RUN_ONCE)).getResultiIQ(Mockito.eq(iq));
        Mockito.verify(this.remoteHandler, Mockito.times(TestUtils.RUN_ONCE)).unmarshal(Mockito.eq(iq),
                Mockito.eq(IqElement.MEMBER));
        Mockito.verify(this.remoteHandler, Mockito.times(TestUtils.RUN_ONCE)).unmarshal(Mockito.eq(iq),
                Mockito.eq(IqElement.SERVICE));
        Mockito.verify(facade, Mockito.times(TestUtils.RUN_ONCE)).requestService(Mockito.eq(member),
                Mockito.eq(serviceType));
        Mockito.verify(httpResponse, Mockito.times(TestUtils.RUN_ONCE)).getContent();
        Mockito.verify(this.remoteHandler, Mockito.times(TestUtils.RUN_ONCE)).marshal(Mockito.eq(iqResponse),
                Mockito.eq(content));
    }
    
}
