package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.attachment.model;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.attachment.model.AttachmentJobStatusRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

public class AttachmentJobStatusRequestTest {

    // test case: When calling the build method, it must verify if It generates the right URL.
    @Test
    public void testBuildSuccessfully() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                CloudStackConstants.Attachment.QUERY_ASYNC_JOB_RESULT_COMMAND);
        String urlBaseExpected = uriBuilder.toString();
        String jobId = "jobId";

        String volumeIdStructureUrl = String.format(
                "%s=%s", CloudStackConstants.Attachment.JOB_ID_KEY_JSON, jobId);

        String[] urlStructure = new String[] {
                urlBaseExpected,
                volumeIdStructureUrl
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        AttachmentJobStatusRequest attachmentJobStatusRequest = new AttachmentJobStatusRequest.Builder()
                .jobId(jobId)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String attachmentJobStatusRequestUrl = attachmentJobStatusRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, attachmentJobStatusRequestUrl);
    }

    // test case: When calling the build method with a null parameter,
    // it must verify if It throws an InternalServerErrorException.
    @Test(expected = InternalServerErrorException.class)
    public void testBuildFail() throws InternalServerErrorException {
        // exercise and verify
        new AttachmentJobStatusRequest.Builder().build(null);
    }

}
