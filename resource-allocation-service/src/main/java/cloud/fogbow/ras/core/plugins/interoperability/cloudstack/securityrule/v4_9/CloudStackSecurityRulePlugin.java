package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.securityrule.v4_9;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.CloudStackUser;
import cloud.fogbow.common.util.CidrUtils;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackHttpClient;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackQueryAsyncJobResponse;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.api.http.response.SecurityRuleInstance;
import cloud.fogbow.ras.api.parameters.SecurityRule;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.Order;
import cloud.fogbow.ras.core.plugins.interoperability.SecurityRulePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudStackCloudUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.publicip.v4_9.CloudStackPublicIpPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.publicip.model.CreateFirewallRuleAsyncResponse;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.publicip.model.CreateFirewallRuleRequest;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model.DeleteFirewallRuleRequest;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model.DeleteFirewallRuleResponse;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model.ListFirewallRulesRequest;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model.ListFirewallRulesResponse;
import com.google.common.annotations.VisibleForTesting;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CloudStackSecurityRulePlugin implements SecurityRulePlugin<CloudStackUser> {
    public static final Logger LOGGER = Logger.getLogger(CloudStackSecurityRulePlugin.class);

    private String cloudStackUrl;
    private CloudStackHttpClient client;

    public CloudStackSecurityRulePlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);
        this.cloudStackUrl = properties.getProperty(CloudStackCloudUtils.CLOUDSTACK_URL_CONFIG);
        this.client = new CloudStackHttpClient();
    }

    @Override
    public String requestSecurityRule(SecurityRule securityRule,
                                      Order majorOrder,
                                      CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Log.REQUESTING_INSTANCE_FROM_PROVIDER));
        checkRequestSecurityParameters(securityRule, majorOrder);

        String cidr = securityRule.getCidr();
        String portFrom = Integer.toString(securityRule.getPortFrom());
        String portTo = Integer.toString(securityRule.getPortTo());
        String protocol = securityRule.getProtocol().toString();
        String publicIpId = CloudStackPublicIpPlugin.getPublicIpId(majorOrder.getId());
        CreateFirewallRuleRequest request = new CreateFirewallRuleRequest.Builder()
                .protocol(protocol)
                .startPort(portFrom)
                .endPort(portTo)
                .ipAddressId(publicIpId)
                .cidrList(cidr)
                .build(this.cloudStackUrl);

        return doRequestInstance(request, cloudStackUser);
    }

    @Override
    public List<SecurityRuleInstance> getSecurityRules(Order majorOrder,
                                                       CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Log.GETTING_INSTANCE_S, majorOrder.getInstanceId()));
        return doGetSecurityRules(majorOrder, cloudStackUser);
    }

    @Override
    public void deleteSecurityRule(String securityRuleId, CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Log.DELETING_INSTANCE_S, securityRuleId));
        DeleteFirewallRuleRequest request = new DeleteFirewallRuleRequest.Builder()
                .ruleId(securityRuleId)
                .build(this.cloudStackUrl);

        doDeleteInstance(request, cloudStackUser);
    }

    @VisibleForTesting
    List<SecurityRuleInstance> doGetSecurityRules(Order majorOrder,
                                                  CloudStackUser cloudStackUser)
            throws FogbowException {

        switch (majorOrder.getType()) {
            case PUBLIC_IP:
                String publicIpId = CloudStackPublicIpPlugin.getPublicIpId(majorOrder.getId());
                return getFirewallRules(publicIpId, cloudStackUser);
            case NETWORK:
                return new ArrayList<>();
            default:
                String errorMsg = String.format(Messages.Log.INVALID_LIST_SECURITY_RULE_TYPE_S, majorOrder.getType());
                throw new InternalServerErrorException(errorMsg);
        }
    }

    @VisibleForTesting
    void doDeleteInstance(DeleteFirewallRuleRequest request,
                          CloudStackUser cloudStackUser)
            throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

            String jsonResponse = CloudStackCloudUtils.doRequest(this.client,
                    uriRequest.toString(), cloudStackUser);
            DeleteFirewallRuleResponse response = DeleteFirewallRuleResponse.fromJson(jsonResponse);
            CloudStackCloudUtils.waitForResult(this.client, this.cloudStackUrl,
                    response.getJobId(), cloudStackUser);
    }

    @VisibleForTesting
    void checkRequestSecurityParameters(SecurityRule securityRule,
                                        Order majorOrder) throws InvalidParameterException {

        if (securityRule.getDirection() == SecurityRule.Direction.OUT) {
            throw new InvalidParameterException(Messages.Exception.INVALID_PARAMETER);
        }
        if (majorOrder.getType() != ResourceType.PUBLIC_IP) {
            throw new InvalidParameterException(Messages.Exception.INVALID_RESOURCE);
        }
    }

    @VisibleForTesting
    String doRequestInstance(CreateFirewallRuleRequest request,
                             CloudStackUser cloudStackUser) throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        String jsonResponse = CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
        CreateFirewallRuleAsyncResponse response = CreateFirewallRuleAsyncResponse.fromJson(jsonResponse);
        try {
            return CloudStackCloudUtils.waitForResult(this.client, this.cloudStackUrl, response.getJobId(), cloudStackUser);
        } catch (UnavailableProviderException e) {
            CloudStackQueryAsyncJobResponse asyncJobResponse = CloudStackCloudUtils.getAsyncJobResponse(
                        this.client, this.cloudStackUrl, response.getJobId(), cloudStackUser);
            deleteSecurityRule(asyncJobResponse.getJobInstanceId(), cloudStackUser);
            throw e;
        }
    }

    @VisibleForTesting
    List<SecurityRuleInstance> getFirewallRules(String ipAddressId, CloudStackUser cloudStackUser)
            throws FogbowException {

        ListFirewallRulesRequest request = new ListFirewallRulesRequest.Builder()
                .ipAddressId(ipAddressId)
                .build(this.cloudStackUrl);

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        String jsonResponse = CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
        ListFirewallRulesResponse response = ListFirewallRulesResponse.fromJson(jsonResponse);
        List<ListFirewallRulesResponse.SecurityRuleResponse> securityRulesResponse = response.getSecurityRulesResponse();
        return convertToFogbowSecurityRules(securityRulesResponse);
    }

    @VisibleForTesting
    List<SecurityRuleInstance> convertToFogbowSecurityRules(
            List<ListFirewallRulesResponse.SecurityRuleResponse> securityRulesResponse) {

        List<SecurityRuleInstance> securityRuleInstances = new ArrayList<SecurityRuleInstance>();
        for (ListFirewallRulesResponse.SecurityRuleResponse securityRuleResponse : securityRulesResponse) {
            SecurityRule.Direction direction = securityRuleResponse.getDirection();
            int portFrom = securityRuleResponse.getPortFrom();
            int portTo = securityRuleResponse.getPortTo();
            String cidr = securityRuleResponse.getCidr();
            String ipAddress = securityRuleResponse.getIpAddress();
            SecurityRule.EtherType etherType = inferEtherType(ipAddress);
            SecurityRule.Protocol protocol = getFogbowProtocol(securityRuleResponse.getProtocol());
            String instanceId = securityRuleResponse.getInstanceId();

            SecurityRuleInstance securityRuleInstance = new SecurityRuleInstance(instanceId, direction,
                    portFrom, portTo, cidr, etherType, protocol);
            securityRuleInstances.add(securityRuleInstance);
        }
        return securityRuleInstances;
    }

    @VisibleForTesting
    SecurityRule.Protocol getFogbowProtocol(String protocol) {
        switch (protocol) {
            case CloudStackConstants.SecurityGroup.TCP_VALUE_PROTOCOL:
                return SecurityRule.Protocol.TCP;
            case CloudStackConstants.SecurityGroup.UDP_VALUE_PROTOCOL:
                return SecurityRule.Protocol.UDP;
            case CloudStackConstants.SecurityGroup.ICMP_VALUE_PROTOCOL:
                return SecurityRule.Protocol.ICMP;
            case CloudStackConstants.SecurityGroup.ALL_VALUE_PROTOCOL:
                return SecurityRule.Protocol.ANY;
            default:
                return null;
        }
    }

    @VisibleForTesting
    SecurityRule.EtherType inferEtherType(String ipAddress) {
        if (CidrUtils.isIpv4(ipAddress)) {
            return SecurityRule.EtherType.IPv4;
        } else if (CidrUtils.isIpv6(ipAddress)) {
            return SecurityRule.EtherType.IPv6;
        } else {
            return null;
        }
    }

    @VisibleForTesting
    void setClient(CloudStackHttpClient client) {
        this.client = client;
    }
}
