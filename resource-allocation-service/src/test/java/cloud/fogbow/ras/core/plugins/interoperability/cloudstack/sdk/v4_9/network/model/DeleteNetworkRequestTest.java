package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.network.model;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.network.model.DeleteNetworkRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DeleteNetworkRequestTest {

    // test case: When calling the build method, it must verify if It generates the right URL.
    @Test
    public void testBuildSuccessfully() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                CloudStackConstants.Network.DELETE_NETWORK_COMMAND);
        String urlBaseExpected = uriBuilder.toString();
        String networkId = "networkId";

        String networkIdStructureUrl =
                String.format("%s=%s", CloudStackConstants.Network.ID_KEY_JSON, networkId);

        String[] urlStructure = new String[] {
                urlBaseExpected,
                networkIdStructureUrl,
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        DeleteNetworkRequest deleteNetworkRequest = new DeleteNetworkRequest.Builder()
                .id(networkId)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String deleteNetworkRequestUrl = deleteNetworkRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, deleteNetworkRequestUrl);
    }

    // test case: When calling the build method with a null parameter,
    // it must verify if It throws an InternalServerErrorException.
    @Test(expected = InternalServerErrorException.class)
    public void testBuildFail() throws InternalServerErrorException {
        // exercise and verify
        new DeleteNetworkRequest.Builder().build(null);
    }
}
