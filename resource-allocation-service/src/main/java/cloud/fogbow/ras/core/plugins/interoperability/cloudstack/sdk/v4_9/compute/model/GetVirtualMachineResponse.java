package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.GsonHolder;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudStackErrorResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.util.List;

import static cloud.fogbow.common.constants.CloudStackConstants.Compute.*;

/**
 * Documentation: https://cloudstack.apache.org/api/apidocs-4.9/apis/listVirtualMachines.html
 * Response example:
 * {
 * "listvirtualmachinesresponse": {
 * "count": 1,
 * "virtualmachine": [{
 * "id": "97637962-2244-4159-b72c-120834757514",
 * "name": "PreprocessingProducao",
 * "state": "Running",
 * "cpunumber": 4,
 * "memory": 6144,
 * "nic": [{
 * "ipaddress": "10.1.1.146",
 * }]
 * }]
 * }
 * }
 */
public class GetVirtualMachineResponse {
    @SerializedName(VIRTUAL_MACHINES_KEY_JSON)
    private ListVirtualMachinesResponse virtualMachinesResponse;

    @NotNull
    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachinesResponse.virtualMachines;
    }

    public static GetVirtualMachineResponse fromJson(String json) throws FogbowException {
        GetVirtualMachineResponse getVirtualMachineResponse =
                GsonHolder.getInstance().fromJson(json, GetVirtualMachineResponse.class);
        getVirtualMachineResponse.virtualMachinesResponse.checkErrorExistence();
        return getVirtualMachineResponse;
    }

    public class ListVirtualMachinesResponse extends CloudStackErrorResponse {
        @SerializedName(VIRTUAL_MACHINE_KEY_JSON)
        private List<VirtualMachine> virtualMachines;
    }

    public class VirtualMachine {
        @SerializedName(ID_KEY_JSON)
        private String id;
        @SerializedName(NAME_KEY_JSON)
        private String name;
        @SerializedName(STATE_KEY_JSON)
        private String state;
        @SerializedName(CPU_NUMBER_KEY_JSON)
        private int cpuNumber;
        @SerializedName(MEMORY_KEY_JSON)
        private int memory;
        @SerializedName(NIC_KEY_JSON)
        private Nic[] nic;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public int getCpuNumber() {
            return cpuNumber;
        }

        public int getMemory() {
            return memory;
        }

        public Nic[] getNic() {
            return nic;
        }
    }

    public class Nic {

        @VisibleForTesting
        Nic (String ipAddress) {
            this.ipAddress = ipAddress;
        }

        @SerializedName(IP_ADDRESS_KEY_JSON)
        private String ipAddress;

        public String getIpAddress() {
            return ipAddress;
        }
    }

}
