package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model.GetVirtualMachineRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import static cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model.GetVirtualMachineRequest.VIRTUAL_MACHINE_ID_KEY;

public class GetVirtualMachineRequestTest {

    // test case: create GetVirtualMachineRequestUrl successfully
    @Test
    public void testCreateGetVirtualMachineRequestUrl() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                GetVirtualMachineRequest.LIST_VMS_COMMAND);
        String urlBaseExpected = uriBuilder.toString();
        String idExpected = "idExpexted";
        String idStructureUrl = String.format("%s=%s", VIRTUAL_MACHINE_ID_KEY, idExpected);
        String[] urlStructure = new String[] {
                urlBaseExpected,
                idStructureUrl
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        GetVirtualMachineRequest getVirtualMachineRequest = new GetVirtualMachineRequest.Builder()
                .id(idExpected)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String getVirtualMachineRequesttUrl = getVirtualMachineRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, getVirtualMachineRequesttUrl);
    }

    // test case: trying create GetVirtualMachineRequestUrl but it occur an error
    @Test(expected = InternalServerErrorException.class)
    public void testCreateGetVirtualMachineRequestWithError() throws InternalServerErrorException {
        // exercise and verify
        new GetVirtualMachineRequest.Builder().build(null);
    }

}
