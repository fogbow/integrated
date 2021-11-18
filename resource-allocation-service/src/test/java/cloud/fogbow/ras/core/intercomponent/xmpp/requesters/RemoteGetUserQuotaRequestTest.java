package cloud.fogbow.ras.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.ras.core.intercomponent.xmpp.IQMatcher;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.api.http.response.quotas.ComputeQuota;
import cloud.fogbow.ras.api.http.response.quotas.Quota;
import cloud.fogbow.ras.api.http.response.quotas.allocation.ComputeAllocation;
import com.google.gson.Gson;
import org.dom4j.Element;
import org.jamppa.component.PacketSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

public class RemoteGetUserQuotaRequestTest {

    private RemoteGetUserQuotaRequest remoteGetUserQuotaRequest;
    private PacketSender packetSender;

    private Quota quota;
    private String provider;
    private SystemUser systemUser;
    private ResourceType resourceType;

    @Before
    public void setUp() {
        this.systemUser = new SystemUser("fake-user-id", "fake-user-name", "token-provider"
        );

        this.provider = "provider";
        this.resourceType = ResourceType.COMPUTE;
        this.remoteGetUserQuotaRequest = new RemoteGetUserQuotaRequest(this.provider, "default", this.systemUser);
        this.packetSender = Mockito.mock(PacketSender.class);
        PacketSenderHolder.setPacketSender(this.packetSender);
        ComputeAllocation computeAllocation = new ComputeAllocation(30, 10, 20);
        ComputeAllocation usedQuota = new ComputeAllocation(60, 40, 50);
        this.quota = new ComputeQuota(computeAllocation, usedQuota);
    }

    //test case: checks if IQ attributes is according to both RemoteGetUserQuotaRequest constructor parameters
    //and remote get user quota request rules. In addition, it checks if the instance from a possible response is
    //properly created and returned by the "send" method
    @Test
    public void testSend() throws Exception {
        //set up
        IQ iqResponse = getQuotaIQResponse(this.quota);
        Mockito.doReturn(iqResponse).when(this.packetSender).syncSendPacket(Mockito.any(IQ.class));
        IQ expectedIQ = RemoteGetUserQuotaRequest.marshal(this.provider, "default", this.systemUser);

        //exercise
        Quota responseQuota = this.remoteGetUserQuotaRequest.send();

        //verify
        IQMatcher matcher = new IQMatcher(expectedIQ);
        Mockito.verify(this.packetSender).syncSendPacket(Mockito.argThat(matcher));

        ComputeAllocation expectedComputeAvailableQuota = (ComputeAllocation) this.quota.getAvailableQuota();
        ComputeAllocation actualComputeAvailableQuota = (ComputeAllocation) responseQuota.getAvailableQuota();
        Assert.assertEquals(expectedComputeAvailableQuota.getRam(), actualComputeAvailableQuota.getRam());
        Assert.assertEquals(expectedComputeAvailableQuota.getInstances(), actualComputeAvailableQuota.getInstances());
        Assert.assertEquals(expectedComputeAvailableQuota.getvCPU(), actualComputeAvailableQuota.getvCPU());

        ComputeAllocation expectedComputeUsedQuota = (ComputeAllocation) this.quota.getUsedQuota();
        ComputeAllocation actualComputeUsedQuota = (ComputeAllocation) responseQuota.getUsedQuota();
        Assert.assertEquals(expectedComputeUsedQuota.getRam(), actualComputeUsedQuota.getRam());
        Assert.assertEquals(expectedComputeUsedQuota.getInstances(), actualComputeUsedQuota.getInstances());
        Assert.assertEquals(expectedComputeUsedQuota.getvCPU(), actualComputeUsedQuota.getvCPU());

        ComputeAllocation expectedComputeTotalQuota = (ComputeAllocation) this.quota.getTotalQuota();
        ComputeAllocation actualComputeTotalQuota = (ComputeAllocation) responseQuota.getTotalQuota();
        Assert.assertEquals(expectedComputeTotalQuota.getRam(), actualComputeTotalQuota.getRam());
        Assert.assertEquals(expectedComputeTotalQuota.getInstances(), actualComputeTotalQuota.getInstances());
        Assert.assertEquals(expectedComputeTotalQuota.getvCPU(), actualComputeTotalQuota.getvCPU());
    }

    //test case: checks if "send" is properly forwading UnavailableProviderException thrown by
    //"XmppErrorConditionToExceptionTranslator.handleError" when the IQ response is null
    @Test(expected = UnavailableProviderException.class)
    public void testSendWhenResponseIsNull() throws Exception {
        //set up
        Mockito.doReturn(null).when(this.packetSender).syncSendPacket(Mockito.any());

        //exercise/verify
        this.remoteGetUserQuotaRequest.send();
    }

    //test case: checks if "send" is properly forwading UnauthorizedRequestException thrown by
    //"XmppErrorConditionToExceptionTranslator.handleError" when the IQ response status is forbidden
    @Test(expected = UnauthorizedRequestException.class)
    public void testSendWhenResponseReturnsForbidden() throws Exception {
        //set up
        IQ iqResponse = new IQ();
        Mockito.doReturn(iqResponse).when(this.packetSender).syncSendPacket(Mockito.any());
        iqResponse.setError(new PacketError(PacketError.Condition.forbidden));

        //exercise/verify
        this.remoteGetUserQuotaRequest.send();
    }

    //test case: checks if "send" is properly forwading InternalServerErrorException thrown by
    //"getUserQuotaFromResponse" when the user quota class name from the IQ response is undefined (wrong or not found)
    @Test(expected = InternalServerErrorException.class)
    public void testSendWhenImageClassIsUndefined() throws Exception {
        //set up
        IQ iqResponse = getQuotaIQResponseWithWrongClass(this.quota);
        Mockito.doReturn(iqResponse).when(this.packetSender).syncSendPacket(Mockito.any());

        //exercise/verify
        this.remoteGetUserQuotaRequest.send();
    }

    private IQ getQuotaIQResponse(Quota userQuota) {
        IQ responseIq = new IQ();
        Element queryEl = responseIq.getElement().addElement(IqElement.QUERY.toString(), RemoteMethod.REMOTE_GET_USER_QUOTA.toString());
        Element instanceElement = queryEl.addElement(IqElement.USER_QUOTA.toString());

        Element instanceClassNameElement = queryEl.addElement(IqElement.USER_QUOTA_CLASS_NAME.toString());
        instanceClassNameElement.setText(userQuota.getClass().getName());

        instanceElement.setText(new Gson().toJson(userQuota));
        return responseIq;
    }

    private IQ getQuotaIQResponseWithWrongClass(Quota userQuota) {
        IQ responseIq = new IQ();
        Element queryEl = responseIq.getElement().addElement(IqElement.QUERY.toString(), RemoteMethod.REMOTE_GET_USER_QUOTA.toString());
        Element instanceElement = queryEl.addElement(IqElement.USER_QUOTA.toString());

        Element instanceClassNameElement = queryEl.addElement(IqElement.USER_QUOTA_CLASS_NAME.toString());
        instanceClassNameElement.setText("wrong-class-name");

        instanceElement.setText(new Gson().toJson(userQuota));
        return responseIq;
    }
}
