package cloud.fogbow.ras.core.plugins.interoperability.azure.util;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.ras.api.http.response.ImageSummary;
import cloud.fogbow.ras.core.plugins.interoperability.azure.sdk.compute.model.AzureGetImageRef;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AzureImageOperationUtil.class,
        URLDecoder.class,
        URLEncoder.class
})
public class AzureImageOperationUtilTest {

    @Rule
    private ExpectedException expectedException = ExpectedException.none();

    // test case: When calling the buildImageSummaryBy method,
    // it must verify if It return the right ImageSummary.
    @Test
    public void testBuildImageSummaryBySuccessfully() {
        // set up
        String publisherExpected = "publisherExpected";
        String offerExpected = "offerExpected";
        String skuExpected = "skuExpected";
        AzureGetImageRef azureVirtualMachineImage = Mockito.mock(AzureGetImageRef.class);
        Mockito.when(azureVirtualMachineImage.getPublisher()).thenReturn(publisherExpected);
        Mockito.when(azureVirtualMachineImage.getOffer()).thenReturn(offerExpected);
        Mockito.when(azureVirtualMachineImage.getSku()).thenReturn(skuExpected);

        String summaryIdExpected = new StringBuilder()
                .append(publisherExpected)
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR)
                .append(offerExpected)
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR)
                .append(skuExpected)
                .toString();

        String summaryNameExpected = new StringBuilder()
                .append(offerExpected)
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_NAME_SEPARATOR)
                .append(skuExpected)
                .toString();

        // execute
        ImageSummary imageSummary = AzureImageOperationUtil.buildImageSummaryBy(azureVirtualMachineImage);

        // verify
        Assert.assertEquals(summaryIdExpected, imageSummary.getId());
        Assert.assertEquals(summaryNameExpected, imageSummary.getName());
    }

    // test case: When calling the buildAzureVirtualMachineImageBy method,
    // it must verify if It return the right VirtualMachineImage.
    @Test
    public void testBuildAzureVirtualMachineImageBySuccessfully() {
        // set up
        String publisherExpected = "publisherExpected";
        String offerExpected = "offerExpected";
        String skuExpected = "skuExpected";

        String summaryIdExpected = new StringBuilder()
                .append(publisherExpected)
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR)
                .append(offerExpected)
                .append(AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR)
                .append(skuExpected)
                .toString();

        // execute
        AzureGetImageRef azureVirtualMachineImage =
                AzureImageOperationUtil.buildAzureVirtualMachineImageBy(summaryIdExpected);

        // verify
        Assert.assertEquals(publisherExpected, azureVirtualMachineImage.getPublisher());
        Assert.assertEquals(offerExpected, azureVirtualMachineImage.getOffer());
        Assert.assertEquals(skuExpected, azureVirtualMachineImage.getSku());
    }

    // test case: When calling encodeImageId method with mocked methods,
    // it must verify if it returns the a new encoded id
    @Test
    public void testEncodeImageIdSuccessfully() throws InternalServerErrorException {
        // set up
        String id = "publisher@#offer@#sku";

        // exercise
        String encodedId = AzureImageOperationUtil.encode(id);

        // verify
        Assert.assertNotEquals(encodedId, id);
    }

    // test case: When calling encodeImageId method and a UnsupportedEncodingException
    // occurs, it must verify if it rethrow a InternalServerErrorException
    @Test
    public void testEncodeImageIdFail() throws UnsupportedEncodingException, InternalServerErrorException {
        // set up
        PowerMockito.mockStatic(URLEncoder.class);
        String separator = AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR;
        String id = "publisher" + separator + "offer" + separator + "sku";

        String encodedId = Base64.getEncoder().encodeToString(id.getBytes());

        String message = "error message";
        PowerMockito.when(URLEncoder.encode(Mockito.eq(encodedId), Mockito.anyString()))
                .thenThrow(new UnsupportedEncodingException(message));

        // verify
        this.expectedException.expect(InternalServerErrorException.class);
        this.expectedException.expectMessage(message);

        // exercise
        AzureImageOperationUtil.encode(id);
    }

    // test case: When calling decodeImageId method with mocked methods,
    // it must verify if it returns the right decoded id
    @Test
    public void testDecodeImageIdSuccessfully() throws InternalServerErrorException, UnsupportedEncodingException {
        // set up
        String separator = AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR;
        String expectedId = "publisher" + separator + "offer" + separator + "sku";

        String encodedId = Base64.getEncoder().encodeToString(expectedId.getBytes());
        String urlEncodedId = URLEncoder.encode(encodedId, StandardCharsets.UTF_8.toString());

        // exercise
        String decoded = AzureImageOperationUtil.decode(urlEncodedId);

        // verify
        Assert.assertEquals(expectedId, decoded);
    }

    // test case: When calling decodeImageId method and a UnsupportedEncodingException
    // occurs, it must verify if it rethrow a InternalServerErrorException
    @Test
    public void testDecodeImageIdFail() throws UnsupportedEncodingException, InternalServerErrorException {
        // set up
        PowerMockito.mockStatic(URLDecoder.class);
        String id = "AfsfsDFfggH==";

        String message = "error message";
        PowerMockito.when(URLDecoder.decode(Mockito.eq(id), Mockito.anyString()))
                .thenThrow(new UnsupportedEncodingException(message));

        // verify
        this.expectedException.expect(InternalServerErrorException.class);
        this.expectedException.expectMessage(message);

        // exercise
        AzureImageOperationUtil.decode(id);
    }

    // test case: When encoding a value, if this value is decoded
    // the initial value should be return
    @Test
    public void testEncodeDecodeSuccessfully() throws InternalServerErrorException {
        // set up
        String separator = AzureImageOperationUtil.IMAGE_SUMMARY_ID_SEPARATOR;
        String expectedId = "publisher" + separator + "offer" + separator + "sku";

        // exercise
        String encoded = AzureImageOperationUtil.encode(expectedId);
        String decoded = AzureImageOperationUtil.decode(encoded);

        // verify
        Assert.assertNotEquals(expectedId, encoded);
        Assert.assertEquals(expectedId, decoded);
    }
}
