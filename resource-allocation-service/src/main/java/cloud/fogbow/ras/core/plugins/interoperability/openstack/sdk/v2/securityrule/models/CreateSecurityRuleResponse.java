package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.securityrule.models;

import cloud.fogbow.common.constants.OpenStackConstants;
import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

/**
 * Documentation: https://developer.openstack.org/api-ref/network/v2/
 * <p>
 *     {
 *       "security_group_rule": {
 *           "direction": "ingress",
 *           "ethertype": "IPv4",
 *           "id": "2bc0accf-312e-429a-956e-e4407625eb62",
 *           "port_range_max": 80,
 *           "port_range_min": 80,
 *           "protocol": "tcp",
 *           "remote_group_id": "85cc3048-abc3-43cc-89b3-377341426ac5",
 *           "remote_ip_prefix": null,
 *           "security_group_id": "a7734e61-b545-452d-a3cd-0189cbd9747a",
 *           "project_id": "e4f50856753b4dc6afee5fa6b9b6c550",
 *           "revision_number": 1,
 *           "tenant_id": "e4f50856753b4dc6afee5fa6b9b6c550",
 *           "created_at": "2018-03-19T19:16:56Z",
 *           "updated_at": "2018-03-19T19:16:56Z",
 *           "description": ""
 *       }
 *     }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class CreateSecurityRuleResponse {
    @SerializedName(OpenStackConstants.Network.SECURITY_GROUP_RULE_KEY_JSON)
    public SecurityRule securityRule;

    public CreateSecurityRuleResponse(SecurityRule securityRule) {
        this.securityRule = securityRule;
    }

    public String getId() {
        return securityRule.id;
    }

    public String toJson() {
        return GsonHolder.getInstance().toJson(this);
    }

    public static CreateSecurityRuleResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, CreateSecurityRuleResponse.class);
    }

    public static class SecurityRule {
        @SerializedName(OpenStackConstants.Network.ID_KEY_JSON)
        private String id;

        public SecurityRule(String id) {
            this.id = id;
        }
    }
}
