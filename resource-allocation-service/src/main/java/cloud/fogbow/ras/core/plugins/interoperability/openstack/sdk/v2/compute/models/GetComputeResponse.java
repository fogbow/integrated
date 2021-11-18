package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.compute.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static cloud.fogbow.common.constants.OpenStackConstants.Compute.*;

/**
 * Documentation: https://developer.openstack.org/api-ref/compute/
 * <p>
 * Response Example:
 * {
 * "server":{
 * "id":"9168b536-cd40-4630-b43f-b259807c6e87",
 * "name":"new-server-test",
 * "addresses":{
 * "provider":[
 * {
 * "addr":"192.168.0.3"
 * }
 * ]
 * },
 * "flavor":{
 * "id":1
 * },
 * "status":"ACTIVE"
 * }
 * }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class GetComputeResponse {
    @SerializedName(SERVER_KEY_JSON)
    private Server server;

    public class Server {
        @SerializedName(ID_KEY_JSON)
        private String id;
        @SerializedName(NAME_KEY_JSON)
        private String name;
        @SerializedName(ADDRESSES_KEY_JSON)
        private Map<String, Address[]> addresses;
        @SerializedName(FLAVOR_KEY_JSON)
        private Flavor flavor;
        @SerializedName(STATUS_KEY_JSON)
        private String status;
        @SerializedName(FAULT_MSG_KEY_JSON)
        private String faultMessage;
    }

    public String getId() {
        return server.id;
    }

    public String getName() {
        return server.name;
    }

    public Map<String, Address[]> getAddresses() {
        return server.addresses;
    }

    public Flavor getFlavor() {
        return server.flavor;
    }

    public String getStatus() {
        return server.status;
    }

    public String getFaultMessage() {
        return server.faultMessage;
    }

    public static GetComputeResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetComputeResponse.class);
    }

    public class Flavor {
        @SerializedName(ID_KEY_JSON)
        private String id;

        public String getId() {
            return id;
        }
    }

    public class Address {
        @SerializedName(ADDRESS_KEY_JSON)
        private String address;

        public String getAddress() {
            return address;
        }
    }
}
