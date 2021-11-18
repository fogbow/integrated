package cloud.fogbow.ras.core.plugins.interoperability.opennebula.securityrule.v5_4;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cloud.fogbow.common.exceptions.*;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.SecurityRuleUtil;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.model.CreateSecurityGroupRequest;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.model.GetSecurityGroupResponse;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.securityrule.model.Rule;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.secgroup.SecurityGroup;
import org.opennebula.client.vnet.VirtualNetwork;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.CloudUser;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.SecurityRuleInstance;
import cloud.fogbow.ras.api.parameters.SecurityRule;
import cloud.fogbow.ras.api.parameters.SecurityRule.Direction;
import cloud.fogbow.ras.api.parameters.SecurityRule.EtherType;
import cloud.fogbow.ras.api.parameters.SecurityRule.Protocol;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.models.orders.Order;
import cloud.fogbow.ras.core.plugins.interoperability.SecurityRulePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.OpenNebulaClientUtil;
import cloud.fogbow.ras.core.plugins.interoperability.opennebula.OpenNebulaConfigurationPropertyKeys;

public class OpenNebulaSecurityRulePlugin implements SecurityRulePlugin<CloudUser> {

    public static final Logger LOGGER = Logger.getLogger(OpenNebulaSecurityRulePlugin.class);

    private static final String CONTENT_SEPARATOR = ",";
    private static final String EMPTY_STRING = "";
    private static final String ID_SEPARATOR = "@";
    private static final String RANGE_SEPARATOR = ":";
    
    @VisibleForTesting
    static final String ALL_ADDRESSES_REMOTE_PREFIX = "0.0.0.0/0";
    @VisibleForTesting
    static final String SECURITY_GROUPS_PATH = "/VNET/TEMPLATE/SECURITY_GROUPS";
    @VisibleForTesting
    static final int MINIMUM_RANGE_PORT = 1;
    @VisibleForTesting
    static final int MAXIMUM_RANGE_PORT = 65536;
    
    // fields indexes for instance id
    private static final int GROUP_ID_INDEX = 0;
    private static final int NETWORK_ID_INDEX = 1;
    private static final int TYPE_INDEX = 2;
    private static final int IP_INDEX = 3;
    private static final int SIZE_INDEX = 4;
    private static final int RANGE_INDEX = 5;
    private static final int PROTOCOL_INDEX = 6;
    private static final int SECURITY_RULE_ID_FIELDS_NUMBER = 7;

    // range ports index
    private static final int PORT_FROM_INDEX = 0;
    private static final int PORT_TO_INDEX = 1;

    // sliced address positions
    private static final int IP_POSITION = 0;
    private static final int SIZE_POSITION = 1;
    
    private String endpoint;

    public OpenNebulaSecurityRulePlugin(String confFilePath) throws FatalErrorException {
        Properties properties = PropertiesUtil.readProperties(confFilePath);
        this.endpoint = properties.getProperty(OpenNebulaConfigurationPropertyKeys.OPENNEBULA_RPC_ENDPOINT_KEY);
    }

    @Override
    public String requestSecurityRule(SecurityRule securityRule, Order majorOrder, CloudUser cloudUser)
            throws FogbowException {
        LOGGER.info(String.format(Messages.Log.REQUESTING_INSTANCE_FROM_PROVIDER));
        Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
        SecurityGroup securityGroup = getSecurityGroup(client, majorOrder);
        Rule rule = createSecurityRuleRequest(securityRule, securityGroup);
        return doRequestSecurityRule(securityGroup, rule);
    }
    
    @Override
    public List<SecurityRuleInstance> getSecurityRules(Order majorOrder, CloudUser cloudUser) throws FogbowException {
        LOGGER.info(String.format(Messages.Log.GETTING_INSTANCE_S, majorOrder.getInstanceId()));
        Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
        SecurityGroup securityGroup = getSecurityGroup(client, majorOrder);
        return doGetSecurityRules(securityGroup);
    }

    @Override
    public void deleteSecurityRule(String securityRuleId, CloudUser cloudUser) throws FogbowException {
        LOGGER.info(String.format(Messages.Log.DELETING_INSTANCE_S, securityRuleId));
        Client client = OpenNebulaClientUtil.createClient(this.endpoint, cloudUser.getToken());
        Rule rule = doUnpackingSecurityRuleId(securityRuleId);
        String securityGroupId = rule.getGroupId();
        doDeleteSecurityRule(client, rule, securityGroupId);
    }

    @VisibleForTesting
    void doDeleteSecurityRule(Client client, Rule rule, String securityGroupId)
            throws FogbowException {

        SecurityGroup securityGroup = OpenNebulaClientUtil.getSecurityGroup(client, securityGroupId);
        GetSecurityGroupResponse group = doGetSecurityGroupResponse(securityGroup);

        List<Rule> rules = getRulesFrom(group);
        if (removeRule(rules, rule)) {
            String template = generateUpdateRequest(group, rules);
            updateSecurityGroup(securityGroup, template);
        } else {
            throw new InstanceNotFoundException(Messages.Exception.RULE_NOT_AVAILABLE);
        }
    }

    @VisibleForTesting
    String generateUpdateRequest(GetSecurityGroupResponse group, List<Rule> rules) {
        String id = group.getId();
        String name = group.getName();

        CreateSecurityGroupRequest request = CreateSecurityGroupRequest.builder()
                .id(id)
                .name(name)
                .rules(rules)
                .build();

        return request.marshalTemplate();
    }

    @VisibleForTesting
    boolean removeRule(List<Rule> rules, Rule ruleToRemove) {
        if (rules != null) {
            for (Rule rule : rules) {
                if (rule.equals(ruleToRemove)) {
                    return rules.remove(rule);
                }
            }
        }
        return false;
    }

    @VisibleForTesting
    Rule doUnpackingSecurityRuleId(String securityRuleId) throws FogbowException {
        String[] fields = securityRuleId.split(ID_SEPARATOR);
        if (fields.length == SECURITY_RULE_ID_FIELDS_NUMBER) {
            String groupId = getValueFrom(fields[GROUP_ID_INDEX]);
            String networkId = getValueFrom(fields[NETWORK_ID_INDEX]);
            String type = getValueFrom(fields[TYPE_INDEX]);
            String ip = getValueFrom(fields[IP_INDEX]);
            String size = getValueFrom(fields[SIZE_INDEX]);
            String range = getValueFrom(fields[RANGE_INDEX]);
            String protocol = getValueFrom(fields[PROTOCOL_INDEX]);

            Rule rule = Rule.builder()
                    .groupId(groupId)
                    .networkId(networkId)
                    .type(type)
                    .ip(ip)
                    .size(size)
                    .range(range)
                    .protocol(protocol)
                    .build();

            return rule;
        }
        throw new InvalidParameterException(String.format(Messages.Exception.INVALID_PARAMETER_S, securityRuleId));
    }

    private String getValueFrom(String value) {
        return !value.isEmpty() ? value : null;
    }
    
    @VisibleForTesting
    List<SecurityRuleInstance> doGetSecurityRules(SecurityGroup securityGroup) {
        List<SecurityRuleInstance> instances = new ArrayList<>();
        GetSecurityGroupResponse group = doGetSecurityGroupResponse(securityGroup);
        List<Rule> rules = getRulesFrom(group);
        for (Rule rule : rules) {
            rule.setGroupId(group.getId());
            SecurityRuleInstance instance = buildSecurityRule(rule);
            instances.add(instance);
        }
        return instances;
    }

    @VisibleForTesting
    SecurityRuleInstance buildSecurityRule(Rule rule) {
        String id = doPackingSecurityRuleId(rule);
        String cidr = SecurityRuleUtil.getAddressCidr(rule);
        String range = rule.getRange();
        int portFrom = (range == null || range.isEmpty())
                ? MINIMUM_RANGE_PORT
                : SecurityRuleUtil.getPortInRange(rule, PORT_FROM_INDEX);
        int portTo = (range == null || range.isEmpty())
                ? MAXIMUM_RANGE_PORT
                : SecurityRuleUtil.getPortInRange(rule, PORT_TO_INDEX);
        Direction direction = SecurityRuleUtil.getDirectionFrom(rule);
        EtherType etherType = SecurityRuleUtil.getEtherTypeFrom(rule);
        Protocol protocol = SecurityRuleUtil.getProtocolFrom(rule);
        return new SecurityRuleInstance(id, direction, portFrom, portTo, cidr, etherType, protocol);
    }
    
    @VisibleForTesting
    String doRequestSecurityRule(SecurityGroup securityGroup, Rule rule) throws FogbowException {
        GetSecurityGroupResponse securityGroupResponse = doGetSecurityGroupResponse(securityGroup);
        String id = securityGroupResponse.getId();
        String name = securityGroupResponse.getName();
        List<Rule> rules = getRulesFrom(securityGroupResponse);
        rules.add(rule);
        
        CreateSecurityGroupRequest request = CreateSecurityGroupRequest.builder()
                .id(id)
                .name(name)
                .rules(rules)
                .build();
        
        String template = request.marshalTemplate();
        updateSecurityGroup(securityGroup, template);
        return doPackingSecurityRuleId(rule);
    }
    
    @VisibleForTesting
    List<Rule> getRulesFrom(GetSecurityGroupResponse response) {
        List<Rule> rules = new ArrayList<>();
        if (response.getTemplate().getRules() != null) {
            rules = response.getTemplate().getRules();
        }
        return rules;
    }

    @VisibleForTesting
    void updateSecurityGroup(SecurityGroup securityGroup, String template) throws FogbowException {
        OneResponse response = securityGroup.update(template);
        if (response.isError()) {
            String message = String.format(Messages.Log.ERROR_WHILE_UPDATING_SECURITY_GROUPS_S, template);
            LOGGER.error(message);
            throw new InternalServerErrorException(message);
        }
    }

    @VisibleForTesting
    String doPackingSecurityRuleId(Rule rule) {
        String[] attributes = new String[SECURITY_RULE_ID_FIELDS_NUMBER];
        attributes[GROUP_ID_INDEX] = rule.getGroupId();
        attributes[NETWORK_ID_INDEX] = getNetworkIdFrom(rule);
        attributes[TYPE_INDEX] = rule.getType();
        attributes[IP_INDEX] = rule.getIp();
        attributes[SIZE_INDEX] = rule.getSize();
        attributes[RANGE_INDEX] = rule.getRange();
        attributes[PROTOCOL_INDEX] = rule.getProtocol();

        String instanceId = StringUtils.join(attributes, ID_SEPARATOR);
        return instanceId;
    }

    @VisibleForTesting
    String getNetworkIdFrom(Rule rule) {
        return rule.getNetworkId() != null 
                ? String.valueOf(rule.getNetworkId()) 
                : EMPTY_STRING;
    }
    
    @VisibleForTesting
    GetSecurityGroupResponse doGetSecurityGroupResponse(SecurityGroup securityGroup) {
        String xml = securityGroup.info().getMessage();
        
        GetSecurityGroupResponse response = GetSecurityGroupResponse.unmarshaller()
        		.response(xml)
        		.unmarshal();
        
        return response;
    }

    @VisibleForTesting
    Rule createSecurityRuleRequest(SecurityRule securityRule, SecurityGroup securityGroup) {
        int portFrom = securityRule.getPortFrom();
        int portTo = securityRule.getPortTo();

        String ip = getIpAddress(securityRule);
        String size = getAddressSize(securityRule);
        String groupId = securityGroup.getId();
        String type = getRuleTypeBy(securityRule.getDirection());
        String protocol = securityRule.getProtocol().name();
        String range = portFrom == portTo 
                ? String.valueOf(portFrom) 
                : portFrom + RANGE_SEPARATOR + portTo;

        Rule rule = Rule.builder()
                .groupId(groupId)
                .type(type)
                .protocol(protocol)
                .ip(ip)
                .size(size)
                .range(range)
                .build();

        return rule;
    }

    @VisibleForTesting
    String getRuleTypeBy(Direction direction) {
        String type = null;
        switch (direction) {
        case IN:
            type = SecurityRuleUtil.INBOUND_TEMPLATE_VALUE; break;
        case OUT:
            type = SecurityRuleUtil.OUTBOUND_TEMPLATE_VALUE;
        }
        return type;
    }
    
    @VisibleForTesting
    String getIpAddress(SecurityRule securityRule) {
        String[] cidrSliced = getCidrFrom(securityRule);
        return cidrSliced != null ? cidrSliced[IP_POSITION] : null;
    }
    
    @VisibleForTesting
    String getAddressSize(SecurityRule securityRule) {
        String[] cidrSliced = getCidrFrom(securityRule);
        return cidrSliced != null ? cidrSliced[SIZE_POSITION] : null;
    }

    @VisibleForTesting
    String[] getCidrFrom(SecurityRule securityRule) {
        SubnetUtils subnetUtils = new SubnetUtils(securityRule.getCidr());
        SubnetInfo subnetInfo = subnetUtils.getInfo();

        if (!subnetInfo.getCidrSignature().equals(ALL_ADDRESSES_REMOTE_PREFIX)) {
            String[] cidrAddress = { 
                    subnetInfo.getLowAddress(),
                    String.valueOf(subnetInfo.getAddressCountLong())
            };
            return cidrAddress;
        }
        return null;
    }

    @VisibleForTesting
    SecurityGroup getSecurityGroup(Client client, Order majorOrder) throws FogbowException {
        String securityGroupName = retrieveSecurityGroupName(majorOrder);
        String virtualNetworkId = majorOrder.getInstanceId();
        VirtualNetwork virtualNetwork = OpenNebulaClientUtil.getVirtualNetwork(client, virtualNetworkId);
        String content = getSecurityGroupContentFrom(virtualNetwork);
        SecurityGroup securityGroup = findSecurityGroupByName(client, content, securityGroupName);
        return securityGroup;
    }

    @VisibleForTesting
    SecurityGroup findSecurityGroupByName(Client client, String content, String name) throws FogbowException {
        String[] securityGroupIds = content.split(CONTENT_SEPARATOR);
        SecurityGroup securityGroup = null;
        for (String securityGroupId : securityGroupIds) {
            securityGroup = OpenNebulaClientUtil.getSecurityGroup(client, securityGroupId);
            if (securityGroup.getName().equals(name)) {
                return securityGroup;
            }
        }
        throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
    }

    @VisibleForTesting
    String getSecurityGroupContentFrom(VirtualNetwork virtualNetwork) throws FogbowException {
        String content = virtualNetwork.xpath(SECURITY_GROUPS_PATH);
        if (content == null || content.isEmpty()) {
            String message = Messages.Log.CONTENT_SECURITY_GROUP_NOT_DEFINED;
            throw new InternalServerErrorException(message);
        }
        return content;
    }

    @VisibleForTesting
    String retrieveSecurityGroupName(Order majorOrder) throws FogbowException {
        switch (majorOrder.getType()) {
        case NETWORK:
            return SystemConstants.PN_SECURITY_GROUP_PREFIX + majorOrder.getInstanceId();
        case PUBLIC_IP:
            return SystemConstants.PIP_SECURITY_GROUP_PREFIX + majorOrder.getInstanceId();
        default:
            throw new InvalidParameterException(Messages.Exception.INVALID_RESOURCE);
        }
    }

}
