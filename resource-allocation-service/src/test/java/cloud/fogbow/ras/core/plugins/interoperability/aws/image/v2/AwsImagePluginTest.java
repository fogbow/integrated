package cloud.fogbow.ras.core.plugins.interoperability.aws.image.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cloud.fogbow.ras.core.datastore.DatabaseManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.models.AwsV2User;
import cloud.fogbow.common.util.HomeDir;
import cloud.fogbow.ras.api.http.response.ImageInstance;
import cloud.fogbow.ras.api.http.response.ImageSummary;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.BaseUnitTests;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2ClientUtil;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2CloudUtil;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2StateMapper;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.BlockDeviceMapping;
import software.amazon.awssdk.services.ec2.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ec2.model.EbsBlockDevice;
import software.amazon.awssdk.services.ec2.model.Image;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AwsV2ClientUtil.class, AwsV2CloudUtil.class, DatabaseManager.class})
public class AwsImagePluginTest extends BaseUnitTests {

    private static final String ANY_VALUE = "anything";
    private static final String CLOUD_NAME = "amazon";
    private static final String FIRST_IMAGE_ID = "first-image-id";
    private static final String FIRST_IMAGE_NAME = "first-image";
    private static final String SECOND_IMAGE_ID = "second-image-id";
    private static final String SECOND_IMAGE_NAME = "second-image";
    private static final String THIRD_IMAGE_ID = "third-image-id";
    private static final String THIRD_IMAGE_NAME = "third-image";

    private static final long EXPECTED_IMAGE_SIZE = 8*(long)Math.pow(1024, 3);

    private AwsImagePlugin plugin;

    @Before
    public void setUp() throws FogbowException{
        String awsConfFilePath = HomeDir.getPath()
                + SystemConstants.CLOUDS_CONFIGURATION_DIRECTORY_NAME
                + File.separator
                + CLOUD_NAME
                + File.separator
                + SystemConstants.CLOUD_SPECIFICITY_CONF_FILE_NAME;

        testUtils.mockReadOrdersFromDataBase();
        this.plugin = Mockito.spy(new AwsImagePlugin(awsConfFilePath));
    }

    // test case: check if getAllImages returns all images expected in the expected
    // format and if the right calls are made.
    @Test
    public void testGetAllImages() throws FogbowException {
        // setup
        Ec2Client client = testUtils.getAwsMockedClient();

        DescribeImagesResponse response = DescribeImagesResponse.builder().images(getMockedImages()).build();
        PowerMockito.mockStatic(AwsV2CloudUtil.class);
        BDDMockito.given(AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client)))
                .willReturn(response);

        List<Image> imagesList = getMockedImages();

        AwsV2User cloudUser = Mockito.mock(AwsV2User.class);

        List<ImageSummary> expectedResult = new ArrayList<>();
        for (Image each : imagesList) {
            expectedResult.add(new ImageSummary(each.imageId(), each.name()));
        }

        // exercise
        List<ImageSummary> result = this.plugin.getAllImages(cloudUser);

        // verify
        Assert.assertEquals(expectedResult, result);

        PowerMockito.verifyStatic(AwsV2CloudUtil.class, Mockito.times(1));
        AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client));

        Mockito.verify(plugin, Mockito.times(1)).buildImagesSummary(Mockito.any());
    }

    // test case: check if the getImage returns the correct image when there are
    // some and if the right calls are made.
    @Test
    public void testGetImageWithResult() throws FogbowException {
        // setup
        Ec2Client client = testUtils.getAwsMockedClient();

        AwsV2User cloudUser = Mockito.mock(AwsV2User.class);
        DescribeImagesResponse response = DescribeImagesResponse.builder()
                .images(getMockedImages()
                        .stream()
                        .filter(each -> each.imageId().equalsIgnoreCase(FIRST_IMAGE_ID))
                        .collect(Collectors.toList()))
                .build();

        PowerMockito.mockStatic(AwsV2CloudUtil.class);
        BDDMockito.given(AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client)))
                .willReturn(response);
        BDDMockito.given(AwsV2CloudUtil.getImagesFrom(Mockito.any())).willCallRealMethod();
        ImageInstance expected = createImageInstance();

        // exercise
        ImageInstance image = this.plugin.getImage(FIRST_IMAGE_ID, cloudUser);

        // verify
        Assert.assertEquals(expected, image);

        PowerMockito.verifyStatic(AwsV2CloudUtil.class, Mockito.times(1));
        AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client));

        PowerMockito.verifyStatic(AwsV2CloudUtil.class, Mockito.times(1));
        AwsV2CloudUtil.getImagesFrom(Mockito.any());

        Mockito.verify(plugin, Mockito.times(1)).buildImageInstance(Mockito.any());
    }

    // test case : check getImage behavior when there is no image to be returned and
    // if the right calls are made.
    @Test(expected = InstanceNotFoundException.class) // verify
    public void testGetImageWithoutResult() throws FogbowException {
        // setup
        Ec2Client client = testUtils.getAwsMockedClient();

        AwsV2User cloudUser = Mockito.mock(AwsV2User.class);
        DescribeImagesResponse response = DescribeImagesResponse.builder().images(new ArrayList<>()).build();
        PowerMockito.mockStatic(AwsV2CloudUtil.class);
        BDDMockito.given(AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client)))
                .willReturn(response);
        BDDMockito.given(AwsV2CloudUtil.getImagesFrom(Mockito.any())).willCallRealMethod();

        // exercise
        this.plugin.getImage(ANY_VALUE, cloudUser);

        PowerMockito.verifyStatic(AwsV2CloudUtil.class, Mockito.times(1));
        AwsV2CloudUtil.doDescribeImagesRequest(Mockito.any(), Mockito.eq(client));

        PowerMockito.verifyStatic(AwsV2CloudUtil.class, Mockito.times(1));
        AwsV2CloudUtil.getImagesFrom(Mockito.any());

        Mockito.verify(plugin, Mockito.times(0)).buildImageInstance(Mockito.any());
    }

    // test case: check if testSize works properly with some specific args.
    @Test
    public void testSize() {
        // setup
        List<Integer> list = new ArrayList<>(
            Arrays.asList(0)
        );

        List<BlockDeviceMapping> blocks = getMockedBlocks(list);

        // exercise
        long size = this.plugin.getImageSize(blocks);

        //verify
        Assert.assertEquals(0, size);

        // setup
        list = new ArrayList<>(
            Arrays.asList(1, 4, 2, 8, 10)
        );

        blocks = getMockedBlocks(list);

        //exercise
        size = this.plugin.getImageSize(blocks);

        // verify
        Assert.assertEquals((long) Math.pow(1024, 3)*(1+4+2+8+10), size);
    }

    private List<BlockDeviceMapping> getMockedBlocks(List<Integer> sizes) {
        List<BlockDeviceMapping> blocks = new ArrayList<>();
        BlockDeviceMapping block;
        for (Integer size : sizes) {
            block = BlockDeviceMapping.builder().ebs(EbsBlockDevice.builder().volumeSize(size).build()).build();
            blocks.add(block);
        }
        return blocks;
    }

    private ImageInstance createImageInstance() {
		return new ImageInstance(
                FIRST_IMAGE_ID,
                FIRST_IMAGE_NAME,
                EXPECTED_IMAGE_SIZE,
                AwsImagePlugin.NO_VALUE_FLAG,
                AwsImagePlugin.NO_VALUE_FLAG,
                AwsV2StateMapper.AVAILABLE_STATE);
	}
    
    private List<Image> getMockedImages() {
        EbsBlockDevice ebs = EbsBlockDevice.builder()
                .volumeSize(8)
                .build();

        BlockDeviceMapping block = BlockDeviceMapping.builder()
                .ebs(ebs)
                .build();

        Image imageOne = Image.builder()
                .imageId(FIRST_IMAGE_ID)
                .name(FIRST_IMAGE_NAME)
                .blockDeviceMappings(block)
                .state(AwsV2StateMapper.AVAILABLE_STATE)
                .build();

        Image imageTwo = Image.builder()
                .imageId(SECOND_IMAGE_ID)
                .name(SECOND_IMAGE_NAME)
                .build();

        Image imageThree = Image.builder()
                .imageId(THIRD_IMAGE_ID)
                .name(THIRD_IMAGE_NAME)
                .build();

        List<Image> imagesList = new ArrayList<>();
        imagesList.add(imageOne);
        imagesList.add(imageTwo);
        imagesList.add(imageThree);

        return imagesList;
    }
}
