package cloud.fogbow.rcs.core.intercomponent.xmpp.handlers;

import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.TestUtils;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.models.ServiceType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.xmpp.packet.IQ;

import java.util.ArrayList;
import java.util.List;

@PrepareForTest({ RemoteFacade.class })
public class RemoteGetAllServicesRequestHandlerTest extends BaseUnitTests {

    private RemoteGetAllServicesRequestHandler remoteHandler;

    @Before
    public void setUp() {
        this.remoteHandler = Mockito.spy(new RemoteGetAllServicesRequestHandler());
    }

    // test case: check if the method makes the expected calls
    @Test
    public void testHandle() throws Exception {
        // set up
        IQ iq = new IQ();

        List<ServiceType> content = new ArrayList<>();
        content.add(ServiceType.RAS);
        content.add(ServiceType.AS);

        RemoteFacade facade = Mockito.spy(RemoteFacade.getInstance());
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(facade);
        Mockito.doReturn(content).when(facade).getServices();

        // exercise
        this.remoteHandler.handle(iq);

        // verify
        Mockito.verify(facade, Mockito.times(TestUtils.RUN_ONCE)).getServices();
        Mockito.verify(this.remoteHandler, Mockito.times(TestUtils.RUN_ONCE)).marshal(Mockito.any(IQ.class), Mockito.any());
    }

}