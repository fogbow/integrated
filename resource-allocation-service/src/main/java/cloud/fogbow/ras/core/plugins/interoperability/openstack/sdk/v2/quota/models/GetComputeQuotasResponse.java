package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.quota.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import static cloud.fogbow.common.constants.OpenStackConstants.Quota.*;

/**
 * Documentation: https://developer.openstack.org/api-ref/compute/#limits-limits
 * <p>
 * Response Example:
 * {
 * "limits":{
 * "absolute":{
 * "maxTotalCores":20,
 * "maxTotalInstances":10,
 * "maxTotalRAMSize":51200,
 * "totalCoresUsed":0,
 * "totalInstancesUsed":0,
 * "totalRAMUsed":0
 * },
 * "rate":[
 * <p>
 * ]
 * }
 * }
 */
public class GetComputeQuotasResponse {
    @SerializedName(LIMITS_KEY_JSON)
    private Limits limits;

    public static GetComputeQuotasResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetComputeQuotasResponse.class);
    }

    public class Limits {
        @SerializedName(ABSOLUTE_KEY_JSON)
        private Absolute absolute;

        public class Absolute {
            @SerializedName(MAX_TOTAL_CORES_KEY_JSON)
            private int maxTotalCores;
            @SerializedName(MAX_TOTAL_RAM_SIZE_KEY_JSON)
            private int maxTotalRAMSize;
            @SerializedName(MAX_TOTAL_INSTANCES_KEY_JSON)
            private int maxTotalInstances;
            @SerializedName(TOTAL_CORES_USED_KEY_JSON)
            private int totalCoresUsed;
            @SerializedName(TOTAL_RAM_USED_KEY_JSON)
            private int totalRAMUsed;
            @SerializedName(TOTAL_INSTANCES_USED_KEY_JSON)
            private int totalInstancesUsed;
        }
    }

    public int getMaxTotalCores() {
        return limits.absolute.maxTotalCores;
    }

    public int getMaxTotalRamSize() {
        return limits.absolute.maxTotalRAMSize;
    }

    public int getMaxTotalInstances() {
        return limits.absolute.maxTotalInstances;
    }

    public int getTotalCoresUsed() {
        return limits.absolute.totalCoresUsed;
    }

    public int getTotalRamUsed() {
        return limits.absolute.totalRAMUsed;
    }

    public int getTotalInstancesUsed() {
        return limits.absolute.totalInstancesUsed;
    }
}
