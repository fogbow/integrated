package cloud.fogbow.ras.core.plugins.interoperability.opennebula.network.v5_4;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cloud.fogbow.common.exceptions.*;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.OpenNebulaStateMapper;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.network.model.CreateNetworkReserveRequest;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.network.model.CreateNetworkUpdateRequest;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.secgroup.SecurityGroup;
import org.opennebula.client.vnet.VirtualNetwork;

import cloud.fogbow.common.models.CloudUser;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.api.http.response.NetworkInstance;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.NetworkAllocationMode;
import cloud.fogbow.ras.core.models.orders.NetworkOrder;
import cloud.fogbow.ras.core.plugins.interoperability.NetworkPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.OpenNebulaClientUtil;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.OpenNebulaConfigurationPropertyKeys;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.model.CreateSecurityGroupRequest;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.model.Rule;

public class OpenNebulaNetworkPlugin implements NetworkPlugin<CloudUser> {

	private static final Logger LOGGER = Logger.getLogger(OpenNebulaNetworkPlugin.class);

	private static final String ALL_PROTOCOLS = "ALL";
	private static final String CIDR_FORMAT = "%s/%s";
	private static final String INPUT_RULE_TYPE = "inbound";
	private static final String OUTPUT_RULE_TYPE = "outbound";
	private static final String SECURITY_GROUP_RESOURCE = "SecurityGroup";

	private static final int BASE_VALUE = 2;

	@VisibleForTesting
    static final String VIRTUAL_NETWORK_RESOURCE = "VirtualNetwork";
	@VisibleForTesting
    static final String SECURITY_GROUPS_SEPARATOR = ",";
	@VisibleForTesting
    static final String VNET_ADDRESS_RANGE_IP_PATH = "/VNET/AR_POOL/AR/IP";
	@VisibleForTesting
    static final String VNET_ADDRESS_RANGE_SIZE_PATH = "/VNET/AR_POOL/AR/SIZE";
	@VisibleForTesting
    static final String VNET_TEMPLATE_SECURITY_GROUPS_PATH = "/VNET/TEMPLATE/SECURITY_GROUPS";
	@VisibleForTesting
    static final String VNET_TEMPLATE_VLAN_ID_PATH = "/VNET/TEMPLATE/VLAN_ID";

	@VisibleForTesting
    static final String ADDRESS_RANGE_ID_PATH_FORMAT = "/VNET/AR_POOL/AR[%s]/AR_ID";
	@VisibleForTesting
    static final String ADDRESS_RANGE_IP_PATH_FORMAT = "/VNET/AR_POOL/AR[%s]/IP";
	@VisibleForTesting
    static final String ADDRESS_RANGE_SIZE_PATH_FORMAT = "/VNET/AR_POOL/AR[%s]/SIZE";
	@VisibleForTesting
    static final String ADDRESS_RANGE_USED_LEASES_PATH_FORMAT = "/VNET/AR_POOL/AR[%s]/USED_LEASES";

	@VisibleForTesting
    static final int IPV4_AMOUNT_BITS = 32;

	private String endpoint;
	private String defaultNetwork;

	public OpenNebulaNetworkPlugin(String confFilePath) throws FatalErrorException {
		Properties properties = PropertiesUtil.readProperties(confFilePath);
		this.endpoint = properties.getProperty(OpenNebulaConfigurationPropertyKeys.OPENNEBULA_RPC_ENDPOINT_KEY);
		this.defaultNetwork = properties.getProperty(OpenNebulaConfigurationPropertyKeys.DEFAULT_RESERVATIONS_NETWORK_ID_KEY);
	}

	@Override
	public boolean isReady(String cloudState) {
		return OpenNebulaStateMapper.map(ResourceType.NETWORK, cloudState).equals(InstanceState.READY);
	}

	@Override
	public boolean hasFailed(String cloudState) {
		return OpenNebulaStateMapper.map(ResourceType.NETWORK, cloudState).equals(InstanceState.FAILED);
	}

	@Override
	public String requestInstance(NetworkOrder networkOrder, CloudUser cloudUser) throws FogbowException {
		LOGGER.info(String.format(Messages.Log.REQUESTING_INSTANCE_FROM_PROVIDER));
		Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
		VirtualNetwork virtualNetwork = OpenNebulaClientUtil.getVirtualNetwork(client, this.defaultNetwork);
		CreateNetworkReserveRequest request = this.getCreateNetworkReserveRequest(networkOrder, virtualNetwork);

		String instanceId = this.doRequestInstance(client, request);
		return instanceId;
	}

	@Override
	public NetworkInstance getInstance(NetworkOrder networkOrder, CloudUser cloudUser) throws FogbowException {
		String instanceId = networkOrder.getInstanceId();
		LOGGER.info(String.format(Messages.Log.GETTING_INSTANCE_S, instanceId));
		Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
		VirtualNetwork virtualNetwork = OpenNebulaClientUtil.getVirtualNetwork(client, instanceId);
		return this.doGetInstance(virtualNetwork);
	}

	@Override
	public void deleteInstance(NetworkOrder networkOrder, CloudUser cloudUser) throws FogbowException {
		String instanceId = networkOrder.getInstanceId();
		LOGGER.info(String.format(Messages.Log.DELETING_INSTANCE_S, instanceId));
		Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
		VirtualNetwork virtualNetwork = OpenNebulaClientUtil.getVirtualNetwork(client, instanceId);

		SecurityGroup securityGroup = this.getSecurityGroupForVirtualNetwork(client, virtualNetwork, instanceId);
		if (securityGroup != null) {
			this.deleteSecurityGroup(securityGroup);
		}

		this.doDeleteInstance(virtualNetwork);
	}

	@VisibleForTesting
    String doRequestInstance(Client client, CreateNetworkReserveRequest createNetworkReserveRequest)
			throws InvalidParameterException, InstanceNotFoundException, UnauthorizedRequestException {

		int defaultNetworkId = this.convertToInteger(this.defaultNetwork);
		String networkReserveTemplate = createNetworkReserveRequest.getVirtualNetworkReserved().marshalTemplate();
		String networkInstanceId = OpenNebulaClientUtil.reserveVirtualNetwork(client, defaultNetworkId, networkReserveTemplate);
		String networkUpdateTemplate = this.getNetworkUpdateTemplate(client, networkInstanceId);

		return OpenNebulaClientUtil.updateVirtualNetwork(client, this.convertToInteger(networkInstanceId), networkUpdateTemplate);
	}

	@VisibleForTesting
    NetworkInstance doGetInstance(VirtualNetwork virtualNetwork) throws InvalidParameterException {
		String id = virtualNetwork.getId();
		String name = virtualNetwork.getName();
		String vLan = virtualNetwork.xpath(VNET_TEMPLATE_VLAN_ID_PATH);
		String firstIP = virtualNetwork.xpath(VNET_ADDRESS_RANGE_IP_PATH);
		String rangeSize = virtualNetwork.xpath(VNET_ADDRESS_RANGE_SIZE_PATH);
		String address = this.generateAddressCidr(firstIP, rangeSize);

		String networkInterface = null;
		String macInterface = null;
		String interfaceState = null;
		NetworkAllocationMode allocationMode = NetworkAllocationMode.DYNAMIC;

		NetworkInstance networkInstance = new NetworkInstance(
				id,
				OpenNebulaStateMapper.DEFAULT_READY_STATE,
				name,
				address,
				firstIP,
				vLan,
				allocationMode,
				networkInterface,
				macInterface,
				interfaceState);

		return networkInstance;
	}

	@VisibleForTesting
    void doDeleteInstance(VirtualNetwork virtualNetwork) throws InternalServerErrorException {
		OneResponse response = virtualNetwork.delete();
		if (response.isError()) {
			throw new InternalServerErrorException(String.format(
					Messages.Exception.ERROR_WHILE_REMOVING_RESOURCE_S_S, VIRTUAL_NETWORK_RESOURCE, response.getMessage()));
		}
	}

	@VisibleForTesting
    Integer getAddressRangeIndex(VirtualNetwork virtualNetwork, String lowAddress, int addressRangeSize)
			throws InvalidParameterException {

		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			String addressRangeFirstIp = virtualNetwork.xpath(String.format(ADDRESS_RANGE_IP_PATH_FORMAT, i));
			String currentAddressRangeSize = virtualNetwork.xpath(String.format(ADDRESS_RANGE_SIZE_PATH_FORMAT, i));
			String addressRangeLeases = virtualNetwork.xpath(String.format(ADDRESS_RANGE_USED_LEASES_PATH_FORMAT, i));
			int usedLeases = NumberUtils.toInt(addressRangeLeases);

			// NOTE(pauloewerton): no more address ranges
			if (addressRangeFirstIp.isEmpty() || currentAddressRangeSize.isEmpty()) {
				break;
			}

			String addressRangeCidr = String.format(CIDR_FORMAT, addressRangeFirstIp,
					this.calculateCidr(this.convertToInteger(currentAddressRangeSize)));

			SubnetUtils.SubnetInfo subnetInfo = new SubnetUtils(addressRangeCidr).getInfo();
			int availableAddresses = subnetInfo.getAddressCount() - usedLeases;
			if (subnetInfo.isInRange(lowAddress) && availableAddresses >= addressRangeSize) {
				return i;
			}
		}

		return null;
	}

	@VisibleForTesting
    String getAddressRangeId(VirtualNetwork virtualNetwork, Integer addressRangeIndex, String cidr)
			throws UnacceptableOperationException {

		if (addressRangeIndex != null) {
			return virtualNetwork.xpath(String.format(ADDRESS_RANGE_ID_PATH_FORMAT, addressRangeIndex));
		} else {
			throw new UnacceptableOperationException(String.format(Messages.Exception.UNABLE_TO_CREATE_NETWORK_RESERVE_S,
					cidr));
		}
	}

	@VisibleForTesting
    String getNextAvailableAddress(VirtualNetwork virtualNetwork, Integer addressRangeIndex)
			throws InvalidParameterException {

		String addressRangeFirstIp = virtualNetwork.xpath(String.format(ADDRESS_RANGE_IP_PATH_FORMAT, addressRangeIndex));
		String currentAddressRangeSize = virtualNetwork.xpath(String.format(ADDRESS_RANGE_SIZE_PATH_FORMAT, addressRangeIndex));
		String addressRangeLeases = virtualNetwork.xpath(String.format(ADDRESS_RANGE_USED_LEASES_PATH_FORMAT, addressRangeIndex));
		int usedLeases = NumberUtils.toInt(addressRangeLeases);

		if (usedLeases > 0) {
			String addressRangeCidr = String.format(CIDR_FORMAT, addressRangeFirstIp,
					this.calculateCidr(this.convertToInteger(currentAddressRangeSize)));
			SubnetUtils.SubnetInfo subnetInfo = new SubnetUtils(addressRangeCidr).getInfo();

			return subnetInfo.getAllAddresses()[usedLeases];
		}

		return addressRangeFirstIp;
	}

	@VisibleForTesting
    CreateNetworkReserveRequest getCreateNetworkReserveRequest(NetworkOrder networkOrder, VirtualNetwork virtualNetwork)
			throws InvalidParameterException, UnacceptableOperationException {
		String cidr = networkOrder.getCidr();
		SubnetUtils.SubnetInfo subnetInfo = new SubnetUtils(cidr).getInfo();

		String name = networkOrder.getName();
		int size = subnetInfo.getAddressCount();

		Integer addressRangeIndex = this.getAddressRangeIndex(virtualNetwork, subnetInfo.getLowAddress(), size);
		String addressRangeId = this.getAddressRangeId(virtualNetwork, addressRangeIndex, cidr);
		String ip = this.getNextAvailableAddress(virtualNetwork, addressRangeIndex);

		return new CreateNetworkReserveRequest.Builder()
				.name(name)
				.size(size)
				.ip(ip)
				.addressRangeId(addressRangeId)
				.build();
	}

	@VisibleForTesting
    String getNetworkUpdateTemplate(Client client, String networkInstanceId)
			throws InvalidParameterException, UnauthorizedRequestException, InstanceNotFoundException {

		String securityGroupId = this.createSecurityGroup(client, networkInstanceId);

		CreateNetworkUpdateRequest updateRequest = new CreateNetworkUpdateRequest.Builder()
				.securityGroups(securityGroupId)
				.build();

		return updateRequest.getVirtualNetworkUpdate().marshalTemplate();
	}

	@VisibleForTesting
    String createSecurityGroup(Client client, String virtualNetworkId)
			throws InvalidParameterException, UnauthorizedRequestException, InstanceNotFoundException {

		VirtualNetwork virtualNetwork = OpenNebulaClientUtil.getVirtualNetwork(client, virtualNetworkId);
		String ip = virtualNetwork.xpath(VNET_ADDRESS_RANGE_IP_PATH);
		String size = virtualNetwork.xpath(VNET_ADDRESS_RANGE_SIZE_PATH);
		String name = this.generateSecurityGroupName(virtualNetworkId);

		// "ALL" setting applies to all protocols if a port range is not defined
		String protocol = ALL_PROTOCOLS;

		// An undefined port range is interpreted by opennebula as all open
		String rangeAll = null;

		// An undefined ip and size is interpreted by opennebula as any network
		String ipAny = null;
		String sizeAny = null;

		// The networkId and securityGroupId parameters are not used in this context.
		String networkId = null;
		String securityGroupId = null;
		
		Rule inputRule = Rule.builder()
		        .protocol(protocol)
		        .ip(ip)
		        .size(size)
		        .range(rangeAll)
		        .type(INPUT_RULE_TYPE)
		        .networkId(networkId)
		        .groupId(securityGroupId)
		        .build();
		
		Rule outputRule = Rule.builder()
		        .protocol(protocol)
		        .ip(ipAny)
		        .size(sizeAny)
		        .range(rangeAll)
		        .type(OUTPUT_RULE_TYPE)
		        .networkId(networkId)
		        .groupId(securityGroupId)
		        .build();
		
		List<Rule> rules = new ArrayList<>();
		rules.add(inputRule);
		rules.add(outputRule);
		
		CreateSecurityGroupRequest request = CreateSecurityGroupRequest.builder()
				.name(name)
				.rules(rules)
				.build();

		String template = request.marshalTemplate();
		return OpenNebulaClientUtil.allocateSecurityGroup(client, template);
	}

	@VisibleForTesting
    void deleteSecurityGroup(SecurityGroup securityGroup) {
		OneResponse response = securityGroup.delete();
		if (response.isError()) {
			LOGGER.error(String.format(Messages.Log.ERROR_WHILE_REMOVING_RESOURCE_S_S, SECURITY_GROUP_RESOURCE, response.getMessage()));
		}
	}

	@VisibleForTesting
    SecurityGroup getSecurityGroupForVirtualNetwork(Client client, VirtualNetwork virtualNetwork, String instanceId)
			throws UnauthorizedRequestException, InstanceNotFoundException, InvalidParameterException {
		SecurityGroup securityGroup = null;
		String securityGroupIdsStr = virtualNetwork.xpath(VNET_TEMPLATE_SECURITY_GROUPS_PATH);

		if (securityGroupIdsStr == null || securityGroupIdsStr.isEmpty()) {
			LOGGER.warn(Messages.Log.CONTENT_SECURITY_GROUP_NOT_DEFINED);
			return securityGroup;
		}

		String[] securityGroupIds =  securityGroupIdsStr.split(SECURITY_GROUPS_SEPARATOR);
		String securityGroupName = this.generateSecurityGroupName(instanceId);
		for (String securityGroupId : securityGroupIds) {
			securityGroup = OpenNebulaClientUtil.getSecurityGroup(client, securityGroupId);
			if (securityGroup.getName().equals(securityGroupName)) {
				return securityGroup;
			}
		}

		return securityGroup;
	}

	@VisibleForTesting
    String generateSecurityGroupName(String instanceId) {
		return SystemConstants.PN_SECURITY_GROUP_PREFIX + instanceId;
	}
	
	@VisibleForTesting
    String generateAddressCidr(String address, String rangeSize) throws InvalidParameterException {
		return String.format(CIDR_FORMAT, address, this.calculateCidr(this.convertToInteger(rangeSize)));
	}
	
	@VisibleForTesting
    int calculateCidr(int size) {
		int exponent = 1;
		int value = 0;
		for (int i = 0; i < IPV4_AMOUNT_BITS; i++) {
			if (exponent >= size) {
				value = IPV4_AMOUNT_BITS - i;
				return value;
			} else {
				exponent *= BASE_VALUE;
			}
		}

		return value;
	}

	@VisibleForTesting
    int convertToInteger(String number) throws InvalidParameterException {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			LOGGER.error(String.format(Messages.Log.ERROR_WHILE_CONVERTING_TO_INTEGER), e);
			throw new InvalidParameterException();
		}
	}
}
