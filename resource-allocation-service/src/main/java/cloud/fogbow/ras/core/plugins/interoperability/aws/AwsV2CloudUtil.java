package cloud.fogbow.ras.core.plugins.interoperability.aws;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.ras.constants.Messages;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Image;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Volume;

public class AwsV2CloudUtil {

    public static final String AWS_TAG_GROUP_ID = "groupId";
    public static final String AWS_TAG_NAME = "Name";
    public static final String SECURITY_GROUP_RESOURCE = "Security Groups";
    
    public static Image getImagesFrom(DescribeImagesResponse response) throws FogbowException {
        if (response != null && !response.images().isEmpty()) {
            return response.images().listIterator().next();
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }
    
    public static DescribeImagesResponse doDescribeImagesRequest(DescribeImagesRequest request, Ec2Client client)
            throws FogbowException {
        try {
            return client.describeImages(request);
        } catch (Exception e) {
            throw new InstanceNotFoundException(e.getMessage());
        }
    }
    
    public static Volume getVolumeFrom(DescribeVolumesResponse response) throws FogbowException {
        if (response != null && !response.volumes().isEmpty()) {
            return response.volumes().listIterator().next();
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }
    
    public static DescribeVolumesResponse doDescribeVolumesRequest(DescribeVolumesRequest request, Ec2Client client)
            throws FogbowException {
        try {
            return client.describeVolumes(request);
        } catch (SdkException e) {
            throw new InstanceNotFoundException(e.getMessage());
        }
    }
    
    public static void createTagsRequest(String resourceId, String key, String value, Ec2Client client)
            throws FogbowException {

        Tag tag = Tag.builder()
                .key(key)
                .value(value)
                .build();

        CreateTagsRequest request = CreateTagsRequest.builder()
                .resources(resourceId)
                .tags(tag)
                .build();
        try {
            client.createTags(request);
        } catch (SdkException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public static void doDeleteSecurityGroup(String groupId, Ec2Client client) throws FogbowException {
        DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest.builder()
                .groupId(groupId)
                .build();
        try {
            client.deleteSecurityGroup(request);
        } catch (SdkException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public static String createSecurityGroup(String vpcId, String groupName, String description, Ec2Client client)
            throws FogbowException {
        
        CreateSecurityGroupRequest request = CreateSecurityGroupRequest.builder()
                .description(description)
                .groupName(groupName)
                .vpcId(vpcId)
                .build();
        try {
            CreateSecurityGroupResponse response = client.createSecurityGroup(request);
            return response.groupId();
        } catch (SdkException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public static void doAuthorizeSecurityGroupIngress(AuthorizeSecurityGroupIngressRequest request, Ec2Client client)
            throws FogbowException {
        try {
            client.authorizeSecurityGroupIngress(request);
        } catch (SdkException e) {
            AwsV2CloudUtil.doDeleteSecurityGroup(request.groupId(), client);
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public static DescribeInstancesResponse doDescribeInstanceById(String instanceId, Ec2Client client)
            throws FogbowException {

        DescribeInstancesRequest describeInstancesRequest = DescribeInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        try {
            return client.describeInstances(describeInstancesRequest);
        } catch (SdkException e) {
            throw new InstanceNotFoundException(e.getMessage());
        }
    }

    public static DescribeInstancesResponse doDescribeInstances(Ec2Client client) throws FogbowException {
        try {
            return client.describeInstances();
        } catch (SdkException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public static Instance getInstanceFrom(DescribeInstancesResponse response) throws FogbowException {
        if (!response.reservations().isEmpty()) {
            Reservation reservation = response.reservations().listIterator().next();
            if (!reservation.instances().isEmpty()) {
                return reservation.instances().listIterator().next();
            }
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }

    public static List<Volume> getInstanceVolumes(Instance instance, Ec2Client client) throws FogbowException {
        List<Volume> volumes = new ArrayList<>();
        DescribeVolumesRequest request;
        DescribeVolumesResponse response;

        List<String> volumeIds = AwsV2CloudUtil.getVolumeIds(instance);
        for (String volumeId : volumeIds) {
            request = DescribeVolumesRequest.builder().volumeIds(volumeId).build();
            response = AwsV2CloudUtil.doDescribeVolumesRequest(request, client);
            volumes.addAll(response.volumes());
        }
        return volumes;
    }

    public static List<String> getVolumeIds(Instance instance) {
        List<String> volumeIds = new ArrayList<String>();
        for (int i = 0; i < instance.blockDeviceMappings().size(); i++) {
            volumeIds.add(instance.blockDeviceMappings().get(i).ebs().volumeId());
        }
        return volumeIds;
    }
    
    public static Address getAddressById(String allocationId, Ec2Client client) throws FogbowException {
        DescribeAddressesRequest request = DescribeAddressesRequest.builder()
                .allocationIds(allocationId)
                .build();
        
        DescribeAddressesResponse response = doDescribeAddressesRequests(request, client);
        return getAddressFrom(response);
    }

    public static Address getAddressFrom(DescribeAddressesResponse response) throws FogbowException {
        if (response != null && !response.addresses().isEmpty()) {
            return response.addresses().listIterator().next();
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }

    public static DescribeAddressesResponse doDescribeAddressesRequests(DescribeAddressesRequest request,
            Ec2Client client) throws FogbowException {
        try {
            return client.describeAddresses(request);
        } catch (SdkException e) {
            throw new InstanceNotFoundException(e.getMessage());
        }
    }
    
    public static Subnet getSubnetById(String subnetId, Ec2Client client) throws FogbowException {
        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
                .subnetIds(subnetId)
                .build();
        
        DescribeSubnetsResponse response = doDescribeSubnetsRequest(request, client);
        return getSubnetFrom(response);
    }
    
    public static Subnet getSubnetFrom(DescribeSubnetsResponse response) throws FogbowException {
        if (response != null && !response.subnets().isEmpty()) {
            return response.subnets().listIterator().next();
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }

    public static DescribeSubnetsResponse doDescribeSubnetsRequest(DescribeSubnetsRequest request, Ec2Client client)
            throws FogbowException {
        try {
            return client.describeSubnets(request);
        } catch (SdkException e) {
            throw new InstanceNotFoundException(e.getMessage());
        }
    }
    
    public static String getGroupIdFrom(List<Tag> tags) throws FogbowException {
        for (Tag tag : tags) {
            if (tag.key().equals(AwsV2CloudUtil.AWS_TAG_GROUP_ID)) {
                return tag.value();
            }
        }
        throw new InternalServerErrorException(Messages.Exception.UNEXPECTED_ERROR);
    }
    
}
