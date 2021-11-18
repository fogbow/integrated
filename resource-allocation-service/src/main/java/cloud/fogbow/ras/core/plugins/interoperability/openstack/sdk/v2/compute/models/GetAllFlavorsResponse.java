package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.compute.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Documentation: https://developer.openstack.org/api-ref/compute/
 * <p>
 * Response Example:
 * {
 * "flavors":[
 * {
 * "id":"1"
 * },
 * {
 * "id":"2"
 * },
 * {
 * "id":"3"
 * },
 * {
 * "id":"4"
 * }
 * ]
 * }
 * <p>
 * We use the @SerializedName annotation to specify that the request parameter is not equal to the class field.
 */
public class GetAllFlavorsResponse {
    @SerializedName("flavors")
    private List<Flavor> flavors;

    public class Flavor {
        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }
    }

    public List<Flavor> getFlavors() {
        return flavors;
    }

    public static GetAllFlavorsResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetAllFlavorsResponse.class);
    }
}
