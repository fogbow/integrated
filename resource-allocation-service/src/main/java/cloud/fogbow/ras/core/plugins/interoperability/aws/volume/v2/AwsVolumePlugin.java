package cloud.fogbow.ras.core.plugins.interoperability.aws.volume.v2;

import java.util.Properties;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.AwsV2User;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.api.http.response.VolumeInstance;
import cloud.fogbow.ras.api.http.response.quotas.allocation.VolumeAllocation;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.VolumeOrder;
import cloud.fogbow.ras.core.plugins.interoperability.VolumePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2ClientUtil;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2CloudUtil;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2ConfigurationPropertyKeys;
import cloud.fogbow.ras.core.plugins.interoperability.aws.AwsV2StateMapper;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateVolumeRequest;
import software.amazon.awssdk.services.ec2.model.CreateVolumeResponse;
import software.amazon.awssdk.services.ec2.model.DeleteVolumeRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Volume;

public class AwsVolumePlugin implements VolumePlugin<AwsV2User> {

	private static final Logger LOGGER = Logger.getLogger(AwsVolumePlugin.class);
	private static final String RESOURCE_NAME = "Volume";
	
	private String region;
	private String zone;
	
	public AwsVolumePlugin(String confFilePath) {
		Properties properties = PropertiesUtil.readProperties(confFilePath);
		this.region = properties.getProperty(AwsV2ConfigurationPropertyKeys.AWS_REGION_SELECTION_KEY);
		this.zone = properties.getProperty(AwsV2ConfigurationPropertyKeys.AWS_AVAILABILITY_ZONE_KEY);
	}
	
	@Override
	public boolean isReady(String instanceState) {
		return AwsV2StateMapper.map(ResourceType.VOLUME, instanceState).equals(InstanceState.READY);
	}

	@Override
	public boolean hasFailed(String instanceState) {
		return AwsV2StateMapper.map(ResourceType.VOLUME, instanceState).equals(InstanceState.FAILED);
	}

	@Override
	public String requestInstance(VolumeOrder volumeOrder, AwsV2User cloudUser) throws FogbowException {
		LOGGER.info(String.format(Messages.Log.REQUESTING_INSTANCE_FROM_PROVIDER));
		Ec2Client client = AwsV2ClientUtil.createEc2Client(cloudUser.getToken(), this.region);
		
		CreateVolumeRequest request = CreateVolumeRequest.builder()
			.size(volumeOrder.getVolumeSize())
			.availabilityZone(this.zone)
			.build();

		return doRequestInstance(request, volumeOrder, client);
	}

	@Override
	public VolumeInstance getInstance(VolumeOrder volumeOrder, AwsV2User cloudUser) throws FogbowException {
		LOGGER.info(String.format(Messages.Log.GETTING_INSTANCE_S, volumeOrder.getInstanceId()));
		Ec2Client client = AwsV2ClientUtil.createEc2Client(cloudUser.getToken(), this.region);
		String volumeId = volumeOrder.getInstanceId();
		return doGetInstance(volumeId, client);
	}

	@Override
	public void deleteInstance(VolumeOrder volumeOrder, AwsV2User cloudUser) throws FogbowException {
		LOGGER.info(String.format(Messages.Log.DELETING_INSTANCE_S, volumeOrder.getInstanceId()));
		Ec2Client client = AwsV2ClientUtil.createEc2Client(cloudUser.getToken(), this.region);
		String volumeId = volumeOrder.getInstanceId();
		doDeleteInstance(volumeId, client);
	}

	@VisibleForTesting
    void doDeleteInstance(String volumeId, Ec2Client client) throws InternalServerErrorException {
	    DeleteVolumeRequest request = DeleteVolumeRequest.builder()
	            .volumeId(volumeId)
	            .build();
	    try {
			client.deleteVolume(request);
		} catch (SdkException e) {
			LOGGER.error(String.format(Messages.Log.ERROR_WHILE_REMOVING_RESOURCE_S_S, RESOURCE_NAME, request.volumeId()), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}
	
    @VisibleForTesting
    VolumeInstance doGetInstance(String volumeId, Ec2Client client) throws FogbowException {
        DescribeVolumesRequest request = DescribeVolumesRequest.builder()
                .volumeIds(volumeId)
                .build();
        
        DescribeVolumesResponse response = AwsV2CloudUtil.doDescribeVolumesRequest(request, client);
        return buildVolumeInstance(response);
    }

    @VisibleForTesting
    VolumeInstance buildVolumeInstance(DescribeVolumesResponse response) throws FogbowException {
        Volume volume = AwsV2CloudUtil.getVolumeFrom(response);
        String id = volume.volumeId();
        String cloudState = volume.stateAsString();
        String name = volume.tags().listIterator().next().value();
        Integer size = volume.size();
        return new VolumeInstance(id, cloudState, name, size);
    }
	
    @VisibleForTesting
    String doRequestInstance(CreateVolumeRequest request, VolumeOrder order, Ec2Client client) throws FogbowException {
        String volumeId;
        try {
            CreateVolumeResponse response = client.createVolume(request);
            volumeId = response.volumeId();
            AwsV2CloudUtil.createTagsRequest(volumeId, AwsV2CloudUtil.AWS_TAG_NAME, order.getName(), client);
            updateVolumeAllocation(order, response);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return volumeId;
    }

    @VisibleForTesting
    void updateVolumeAllocation(VolumeOrder volumeOrder, CreateVolumeResponse volumeResponse) {
        synchronized (volumeOrder) {
            VolumeAllocation actualAllocation = new VolumeAllocation(volumeResponse.size());
            volumeOrder.setActualAllocation(actualAllocation);
        }
    }
    
}
