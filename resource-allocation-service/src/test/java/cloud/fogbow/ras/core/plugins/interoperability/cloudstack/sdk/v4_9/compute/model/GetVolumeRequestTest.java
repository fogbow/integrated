package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.volume.model.GetVolumeRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetVolumeRequestTest {

    // test case: create GetVolumeRequestUrl successfully
    @Test
    public void testGetVolumeRequestRequestUrl() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                GetVolumeRequest.LIST_VOLUMES_COMMAND);
        String urlBaseExpected = uriBuilder.toString();

        String idExpected = "idVolume";
        String idStructureUrl = String.format("%s=%s", GetVolumeRequest.VOLUME_ID_KEY, idExpected);
        String virtualMachineIdExpected = "idExpexted";
        String virtualMachineIdStructureUrl = String.format(
                "%s=%s", GetVolumeRequest.VIRTUAL_MACHINE_ID_KEY, virtualMachineIdExpected);
        String typeExpected = "true";
        String typeStructureUrl = String.format("%s=%s", GetVolumeRequest.TYPE_KEY, typeExpected);
        String[] urlStructure = new String[] {
                urlBaseExpected,
                idStructureUrl,
                virtualMachineIdStructureUrl,
                typeStructureUrl
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        GetVolumeRequest getVolumeRequest = new GetVolumeRequest.Builder()
                .id(idExpected)
                .virtualMachineId(virtualMachineIdExpected)
                .type(typeExpected)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String destroyVirtualMachineRequestUrl = getVolumeRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, destroyVirtualMachineRequestUrl);
    }

    // test case: trying create GetVolumeRequestRequest but it occur an error
    @Test(expected = InternalServerErrorException.class)
    public void testCreateGetVolumeRequestRequestWithError() throws InternalServerErrorException {
        // exercise and verify
        new GetVolumeRequest.Builder().build(null);
    }

}
