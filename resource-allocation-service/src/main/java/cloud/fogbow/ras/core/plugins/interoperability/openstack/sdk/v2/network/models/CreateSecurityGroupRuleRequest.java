package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.network.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import static cloud.fogbow.common.constants.OpenStackConstants.Network.*;

/**
 * Documentation: https://developer.openstack.org/api-ref/network/v2/
 * <p>
 * Request Example:
 * {
 * "security_group_rule": {
 * "direction": "ingress",
 * "security_group_id": "a7734e61-b545-452d-a3cd-0189cbd9747a"
 * "remote_ip_prefix":"fake-prefix",
 * "port_range_min": "80",
 * "port_range_max": "80",
 * }
 * }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class CreateSecurityGroupRuleRequest {
    @SerializedName(SECURITY_GROUP_RULE_KEY_JSON)
    private SecurityGroupRule securityGroupRule;

    public CreateSecurityGroupRuleRequest(SecurityGroupRule securityGroupRule) {
        this.securityGroupRule = securityGroupRule;
    }

    public String toJson() {
        return GsonHolder.getInstance().toJson(this);
    }

    public static class SecurityGroupRule {
        @SerializedName(DIRECTION_KEY_JSON)
        private String direction;
        @SerializedName(SECURITY_GROUP_ID_KEY_JSON)
        private String securityGroupId;
        @SerializedName(REMOTE_IP_PREFIX_KEY_JSON)
        private String remoteIpPrefix;
        @SerializedName(PROTOCOL_KEY_JSON)
        private String protocol;
        @SerializedName(MIN_PORT_KEY_JSON)
        private Integer minPort;
        @SerializedName(MAX_PORT_KEY_JSON)
        private Integer maxPort;
        @SerializedName(ETHER_TYPE_KEY_JSON)
        private final String etherType;

        public SecurityGroupRule(Builder builder) {
            this.direction = builder.direction;
            this.securityGroupId = builder.securityGroupId;
            this.remoteIpPrefix = builder.remoteIpPrefix;
            this.protocol = builder.protocol;
            this.minPort = builder.minPort;
            this.maxPort = builder.maxPort;
            this.etherType = builder.etherType;
        }
    }

    public static class Builder {
        private String direction;
        private String securityGroupId;
        private String remoteIpPrefix;
        private String protocol;
        private Integer minPort;
        private Integer maxPort;
        private String etherType;

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder securityGroupId(String securityGroupId) {
            this.securityGroupId = securityGroupId;
            return this;
        }

        public Builder remoteIpPrefix(String remoteIpPrefix) {
            this.remoteIpPrefix = remoteIpPrefix;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder minPort(int minPort) {
            this.minPort = minPort;
            return this;
        }

        public Builder maxPort(int maxPort) {
            this.maxPort = maxPort;
            return this;
        }

        public Builder etherType(String etherType) {
            this.etherType = etherType;
            return this;
        }

        public CreateSecurityGroupRuleRequest build() {
            SecurityGroupRule securityGroupRule = new SecurityGroupRule(this);
            return new CreateSecurityGroupRuleRequest(securityGroupRule);
        }
    }
}
