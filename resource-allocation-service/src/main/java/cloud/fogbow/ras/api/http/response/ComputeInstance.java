package cloud.fogbow.ras.api.http.response;

import cloud.fogbow.ras.constants.ApiDocumentation;
import cloud.fogbow.ras.core.models.UserData;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ComputeInstance extends OrderInstance {
    @ApiModelProperty(position = 8, example = ApiDocumentation.Model.COMPUTE_NAME)
    private String name;
    @ApiModelProperty(position = 9, example = "1")
    private int vCPU;
    /**
     * Memory attribute, must be set in MB.
     */
    @ApiModelProperty(position = 10, example = "1024", notes = ApiDocumentation.Model.VOLUME_SIZE_NOTE)
    private int ram;
    /**
     * Disk attribute, must be set in GB.
     */
    @ApiModelProperty(position = 11, example = "30", notes = ApiDocumentation.Model.DISK_NOTE)
    private int disk;
    @ApiModelProperty(position = 12, example = ApiDocumentation.Model.IP_ADDRESSES)
    private List<String> ipAddresses;
    /**
     * Order-related properties
     */
    @ApiModelProperty(position = 13, example = ApiDocumentation.Model.NETWORKS)
    private List<NetworkSummary> networks;
    @ApiModelProperty(position = 14, example = ApiDocumentation.Model.IMAGE_ID)
    private String imageId;
    @ApiModelProperty(position = 15, example = ApiDocumentation.Model.SSH_PUBLIC_KEY, notes = ApiDocumentation.Model.SSH_PUBLIC_KEY_NOTE)
    private String publicKey;
    @ApiModelProperty(position = 16, example = ApiDocumentation.Model.USER_DATA, notes = ApiDocumentation.Model.USER_DATA_NOTE)
    private List<UserData> userData;

    /**
     * Compute status
     */
    @ApiModelProperty(position = 17)
    private boolean isPaused;
    @ApiModelProperty(position = 18)
    private boolean isHibernated;
    @ApiModelProperty(position = 19)
    private boolean isStopped;
    @ApiModelProperty(position = 20)
    private boolean isPausing;
    @ApiModelProperty(position = 21)
    private boolean isHibernating;
    @ApiModelProperty(position = 22)
    private boolean isStopping;
    @ApiModelProperty(position = 23)
    private boolean isResuming;

    public ComputeInstance(String id, String cloudState, String name, List<String> ipAddresses, String faultMessage) {
        super(id, cloudState, faultMessage);
        this.name = name;
        this.ipAddresses = ipAddresses;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopped = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isStopping = false;
        this.isResuming = false;
    }

    @Deprecated
    public ComputeInstance(String id, String cloudState, String name, int vCPU, int ram, int disk,
                           List<String> ipAddresses) {
        super(id, cloudState);
        this.name = name;
        this.vCPU = vCPU;
        this.ram = ram;
        this.disk = disk;
        this.ipAddresses = ipAddresses;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopped = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isStopping = false;
        this.isResuming = false;
    }

    public ComputeInstance(String id, String cloudState, String name, int vCPU, int ram, int disk,
                           List<String> ipAddresses, String faultMessage) {
        super(id, cloudState, faultMessage);
        this.name = name;
        this.vCPU = vCPU;
        this.ram = ram;
        this.disk = disk;
        this.ipAddresses = ipAddresses;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopped = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isStopping = false;
        this.isResuming = false;
    }

    public ComputeInstance(String id, String cloudState, String name, int vCPU, int ram, int disk,
                           List<String> ipAddresses, String imageId, String publicKey, List<UserData> userData) {
        super(id, cloudState);
        this.name = name;
        this.vCPU = vCPU;
        this.ram = ram;
        this.disk = disk;
        this.ipAddresses = ipAddresses;
        this.imageId = imageId;
        this.publicKey = publicKey;
        this.userData = userData;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopped = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isStopping = false;
        this.isResuming = false;
    }

    public ComputeInstance(String id) {
        super(id);
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopped = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isStopping = false;
        this.isResuming = false;
    }

    public int getDisk() {
        return this.disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIpAddresses() {
        return this.ipAddresses;
    }

    public int getRam() {
        return this.ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getvCPU() {
        return this.vCPU;
    }

    public void setvCPU(int vCPU) {
        this.vCPU = vCPU;
    }

    public List<NetworkSummary> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NetworkSummary> networks) {
        this.networks = networks;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<UserData> getUserData() {
        return userData;
    }

    public void setUserData(List<UserData> userData) {
        this.userData = userData;
    }

    public void setPausing() {
        this.isHibernated = false;
        this.isPaused = false;
        this.isStopped = false;
        this.isStopping = false;
        this.isPausing = true;
        this.isHibernating = false;
        this.isResuming = false;
    }

    public boolean isPausing() {
        return isPausing;
    }
    
    public void setPaused() {
        this.isPaused = true;
        this.isHibernated = false;
        this.isStopped = false;
        this.isStopping = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isResuming = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setHibernating() {
        this.isHibernated = false;
        this.isPaused = false;
        this.isStopped = false;
        this.isStopping = false;
        this.isPausing = false;
        this.isHibernating = true;
        this.isResuming = false;
    }
    
    public boolean isHibernating() {
        return isHibernating;
    }

    public void setHibernated() {
        this.isHibernated = true;
        this.isPaused = false;
        this.isStopped = false;
        this.isStopping = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isResuming = false;
    }

    public boolean isHibernated() {
        return isHibernated;
    }

    public void setStopping() {
        this.isStopped = false;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopping = true;
        this.isPausing = false;
        this.isHibernating = false;
        this.isResuming = false;
    }
    
    public boolean isStopping() {
        return isStopping;
    }
    
    public void setStopped() {
        this.isStopped = true;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopping = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isResuming = false;
    }

    public boolean isStopped() {
        return isStopped;
    }
    
    public void setResuming() {
        this.isStopped = false;
        this.isPaused = false;
        this.isHibernated = false;
        this.isStopping = false;
        this.isPausing = false;
        this.isHibernating = false;
        this.isResuming = true;
    }
    
    public boolean isResuming() {
        return isResuming;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ComputeInstance that = (ComputeInstance) o;

        if (vCPU != that.vCPU) return false;
        if (ram != that.ram) return false;
        if (disk != that.disk) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return ipAddresses != null ? ipAddresses.equals(that.ipAddresses) : that.ipAddresses == null;
    }
}
