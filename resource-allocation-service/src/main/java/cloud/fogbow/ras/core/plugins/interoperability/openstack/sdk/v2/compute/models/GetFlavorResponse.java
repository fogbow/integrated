package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.compute.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import static cloud.fogbow.common.constants.OpenStackConstants.Compute.*;

/**
 * Documentation: https://developer.openstack.org/api-ref/compute/
 * <p>
 * Response Example:
 * {
 * "flavor":{
 * "id":"7",
 * "name":"m1.small.description",
 * "disk":20,
 * "ram":2048,
 * "vcpus":1
 * }
 * }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class GetFlavorResponse {
    @SerializedName(FLAVOR_KEY_JSON)
    private Flavor flavor;

    public String getId() {
        return flavor.id;
    }

    public String getName() {
        return flavor.name;
    }

    public int getDisk() {
        return flavor.disk;
    }

    public int getMemory() {
        return flavor.memory;
    }

    public int getVcpusCount() {
        return flavor.vcpusCount;
    }

    public static GetFlavorResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetFlavorResponse.class);
    }

    public class Flavor {
        @SerializedName(ID_KEY_JSON)
        private String id;
        @SerializedName(NAME_KEY_JSON)
        private String name;
        @SerializedName(DISK_KEY_JSON)
        private int disk;
        @SerializedName(MEMORY_KEY_JSON)
        private int memory;
        @SerializedName(VCPUS_KEY_JSON)
        private int vcpusCount;
    }
}
