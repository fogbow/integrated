package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.network.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import static cloud.fogbow.common.constants.OpenStackConstants.Network.ID_KEY_JSON;
import static cloud.fogbow.common.constants.OpenStackConstants.Network.SECURITY_GROUP_KEY_JSON;

/**
 * Documentation: https://developer.openstack.org/api-ref/network/v2/
 * <p>
 * Response Example:
 * {
 * "security_group":{
 * "id": "38ce2d8e-e8f1-48bd-83c2-d33cb9f50c3d"
 * }
 * }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class CreateSecurityGroupResponse {
    @SerializedName(SECURITY_GROUP_KEY_JSON)
    private SecurityGroup securityGroup;

    public CreateSecurityGroupResponse(SecurityGroup securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getId() {
        return securityGroup.id;
    }

    public static CreateSecurityGroupResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, CreateSecurityGroupResponse.class);
    }

    public String toJson() {
        return GsonHolder.getInstance().toJson(this);
    }

    public static class SecurityGroup {
        @SerializedName(ID_KEY_JSON)
        private String id;

        public SecurityGroup(String id) {
            this.id = id;
        }
    }
}
