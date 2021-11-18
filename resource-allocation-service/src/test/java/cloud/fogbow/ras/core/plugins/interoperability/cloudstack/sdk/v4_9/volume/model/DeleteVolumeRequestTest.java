package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.volume.model;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.volume.model.DeleteVolumeRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DeleteVolumeRequestTest {

    // test case: When calling the build method, it must verify if It generates the right URL.
    @Test
    public void testBuildSuccessfully() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                CloudStackConstants.Volume.DELETE_VOLUME_COMMAND);
        String urlBaseExpected = uriBuilder.toString();
        String volumeId = "volumeId";

        String volumeIdStructureUrl = CloudstackTestUtils.buildParameterStructureUrl(
                CloudStackConstants.Volume.ID_KEY_JSON, volumeId);

        String[] urlStructure = new String[] {
                urlBaseExpected,
                volumeIdStructureUrl
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        DeleteVolumeRequest deleteVolumeRequest = new DeleteVolumeRequest.Builder()
                .id(volumeId)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String deleteVolumeRequestUrl = deleteVolumeRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, deleteVolumeRequestUrl);
    }

    // test case: When calling the build method with a null parameter,
    // it must verify if It throws an InternalServerErrorException.
    @Test(expected = InternalServerErrorException.class)
    public void testBuildFail() throws InternalServerErrorException {
        // exercise and verify
        new DeleteVolumeRequest.Builder().build(null);
    }

}
