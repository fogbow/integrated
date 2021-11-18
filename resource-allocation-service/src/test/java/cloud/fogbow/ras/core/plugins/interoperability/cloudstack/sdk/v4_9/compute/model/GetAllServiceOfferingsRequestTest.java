package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model.GetAllServiceOfferingsRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GetAllServiceOfferingsRequestTest {

    // test case: create GetAllServiceOfferingsRequestUrl successfully
    @Test
    public void testCreateGetAllServiceOfferingsRequestUrl() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                GetAllServiceOfferingsRequest.LIST_SERVICE_OFFERINGS_COMMAND);
        String urlExpected = uriBuilder.toString();

        // exercise
        GetAllServiceOfferingsRequest getAllServiceOfferingsRequest = new GetAllServiceOfferingsRequest.
                        Builder().build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String getAllServiceOfferingsRequestUrl = getAllServiceOfferingsRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpected, getAllServiceOfferingsRequestUrl);
    }

    // test case: trying create GetAllServiceOfferingsRequestUrl but it occur an error
    @Test(expected = InternalServerErrorException.class)
    public void testCreateGetAllServiceOfferingsRequestWithError() throws InternalServerErrorException {
        // exercise and verify
        new GetAllServiceOfferingsRequest.Builder().build(null);
    }
}