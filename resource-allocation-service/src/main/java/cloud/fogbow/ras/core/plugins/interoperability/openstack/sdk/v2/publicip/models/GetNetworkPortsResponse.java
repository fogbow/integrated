package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.publicip.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static cloud.fogbow.common.constants.OpenStackConstants.PublicIp.ID_KEY_JSON;
import static cloud.fogbow.common.constants.OpenStackConstants.PublicIp.PORTS_KEY_JSON;

/**
 * Documentation : https://developer.openstack.org/api-ref/network/v2/#list-ports
 * <p>
 * Response Example :
 * <p>
 * {
 * "ports": [
 * {
 * "id": "d80b1a3b-4fc1-49f3-952e-1e2ab7081d8b"
 * }
 * ]
 * }
 */
public class GetNetworkPortsResponse {

    @SerializedName(PORTS_KEY_JSON)
    private List<Port> ports;

    public static class Port {
        @SerializedName(ID_KEY_JSON)
        private String id;

        public String getId() {
            return id;
        }
    }

    public List<Port> getPorts() {
        return ports;
    }

    public static GetNetworkPortsResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetNetworkPortsResponse.class);
    }
}
