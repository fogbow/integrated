package cloud.fogbow.ras.core.plugins.interoperability.azure.quota;

import cloud.fogbow.common.constants.AzureConstants;
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.AzureUser;
import cloud.fogbow.common.util.BinaryUnit;
import cloud.fogbow.common.util.connectivity.cloud.azure.AzureClientCacheManager;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.ras.api.http.response.quotas.ResourceQuota;
import cloud.fogbow.ras.api.http.response.quotas.allocation.*;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.plugins.interoperability.QuotaPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.azure.sdk.quota.AzureQuotaSDK;
import cloud.fogbow.ras.core.plugins.interoperability.azure.util.AzureGeneralPolicy;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.ComputeUsage;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.VirtualMachineSize;
import com.microsoft.azure.management.network.NetworkUsage;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class AzureQuotaPlugin implements QuotaPlugin<AzureUser> {

    public static final Logger LOGGER = Logger.getLogger(AzureQuotaPlugin.class);

    @VisibleForTesting
    static final String QUOTA_VM_INSTANCES_KEY = "virtualMachines";

    @VisibleForTesting
    static final String QUOTA_VM_CORES_KEY = "cores";

    @VisibleForTesting
    static final String QUOTA_NETWORK_INSTANCES = "VirtualNetworks";

    @VisibleForTesting
    static final String QUOTA_PUBLIC_IP_ADDRESSES = "PublicIPAddresses";

    @VisibleForTesting
    static final int NO_USAGE = 0;

    /**
     * This value is hardcoded because at the time this plugin was developed, a value for maximum storage capacity was
     * not provided by the SDK. The current value is informed in the documentation.
     */
    @VisibleForTesting
    static final int MAXIMUM_STORAGE_ACCOUNT_CAPACITY = (int) BinaryUnit.petabytes(50).asGigabytes();

    private final String defaultRegionName;

    public AzureQuotaPlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);
        this.defaultRegionName = properties.getProperty(AzureConstants.DEFAULT_REGION_NAME_KEY);
        AzureGeneralPolicy.checkRegionName(this.defaultRegionName);
    }

    @Override
    public ResourceQuota getUserQuota(AzureUser cloudUser) throws FogbowException {
        LOGGER.info(Messages.Log.GETTING_QUOTA);
        Azure azure = AzureClientCacheManager.getAzure(cloudUser);

        Map<String, ComputeUsage> computeUsages = this.getComputeUsageMap(azure);
        Map<String, NetworkUsage> networkUsages = this.getNetworkUsageMap(azure);
        PagedList<Disk> disks = AzureQuotaSDK.getDisks(azure);

        ResourceAllocation totalQuota = this.getTotalQuota(computeUsages, networkUsages);
        ResourceAllocation usedQuota = this.getUsedQuota(computeUsages, networkUsages, disks, azure);
        return new ResourceQuota(totalQuota, usedQuota);
    }

    @VisibleForTesting
    ResourceAllocation getUsedQuota(Map<String, ComputeUsage> computeUsages, Map<String, NetworkUsage> networkUsages, PagedList<Disk> disks, Azure azure) {
        ComputeAllocation computeAllocation = this.getUsedComputeAllocation(computeUsages, azure);
        NetworkAllocation networkAllocation = this.getUsedNetworkAllocation(networkUsages);
        PublicIpAllocation publicIpAllocation = this.getUsedPublicIpAllocation(networkUsages);
        VolumeAllocation volumeAllocation = this.getUsedVolumeAllocation(disks);
        return this.buildQuota(computeAllocation, networkAllocation, publicIpAllocation, volumeAllocation);
    }

    @VisibleForTesting
    VolumeAllocation getUsedVolumeAllocation(PagedList<Disk> disks) {
        int volumes = disks.size();
        int storage = this.getStorageUsage(disks);
        return new VolumeAllocation(volumes, storage);
    }

    @VisibleForTesting
    int getStorageUsage(PagedList<Disk> disks) {
        int initialValue = NO_USAGE;
        return disks.stream()
                .map(disk -> disk.sizeInGB())
                .reduce(initialValue, Integer::sum);
    }

    @VisibleForTesting
    NetworkAllocation getUsedNetworkAllocation(Map<String, NetworkUsage> networkUsages) {
        NetworkUsage networkUsage = networkUsages.get(QUOTA_NETWORK_INSTANCES);
        int instances = networkUsage == null ? NO_USAGE : (int) networkUsage.currentValue();
        return new NetworkAllocation(instances);
    }

    @VisibleForTesting
    ComputeAllocation getUsedComputeAllocation(Map<String, ComputeUsage> computeUsages, Azure azure) {
        ComputeUsage vmUsage = computeUsages.get(QUOTA_VM_INSTANCES_KEY);
        ComputeUsage coreUsage = computeUsages.get(QUOTA_VM_CORES_KEY);

        int instances = vmUsage == null ? NO_USAGE : vmUsage.currentValue();
        int cores = coreUsage == null ? NO_USAGE : coreUsage.currentValue();
        int ram = this.getMemoryUsage(azure);

        return new ComputeAllocation(instances, cores, ram);
    }

    @VisibleForTesting
    PublicIpAllocation getUsedPublicIpAllocation(Map<String, NetworkUsage> networkUsages) {
        NetworkUsage publicIpUsage = networkUsages.get(QUOTA_PUBLIC_IP_ADDRESSES);
        int instances = publicIpUsage == null ? NO_USAGE : (int) publicIpUsage.currentValue();
        return new PublicIpAllocation(instances);
    }

    @VisibleForTesting
    ResourceAllocation getTotalQuota(Map<String, ComputeUsage> computeUsages,
                                     Map<String, NetworkUsage> networkUsages) {
        ComputeAllocation computeAllocation = this.getTotalComputeAllocation(computeUsages);
        NetworkAllocation networkAllocation = this.getTotalNetworkAllocation(networkUsages);
        PublicIpAllocation publicIpAllocation = this.getTotalPublicIpAllocation(networkUsages);
        VolumeAllocation volumeAllocation = this.getTotalVolumeAllocation();
        return this.buildQuota(computeAllocation, networkAllocation, publicIpAllocation, volumeAllocation);
    }

    @VisibleForTesting
    VolumeAllocation getTotalVolumeAllocation() {
        int volumes = FogbowConstants.UNLIMITED_RESOURCE;
        int storage = MAXIMUM_STORAGE_ACCOUNT_CAPACITY;
        return new VolumeAllocation(volumes, storage);
    }

    @VisibleForTesting
    PublicIpAllocation getTotalPublicIpAllocation(Map<String, NetworkUsage> networkUsages) {
        NetworkUsage publicIpUsage = networkUsages.get(QUOTA_PUBLIC_IP_ADDRESSES);
        int instances = publicIpUsage == null ? NO_USAGE : (int) publicIpUsage.limit();
        return new PublicIpAllocation(instances);
    }

    @VisibleForTesting
    NetworkAllocation getTotalNetworkAllocation(Map<String, NetworkUsage> networkUsages) {
        NetworkUsage networkUsage = networkUsages.get(QUOTA_NETWORK_INSTANCES);
        int instances = networkUsage == null ? NO_USAGE : (int) networkUsage.limit();
        return new NetworkAllocation(instances);
    }

    @VisibleForTesting
    ComputeAllocation getTotalComputeAllocation(Map<String, ComputeUsage> computeUsages) {
        ComputeUsage vmUsage = computeUsages.get(QUOTA_VM_INSTANCES_KEY);
        ComputeUsage coreUsage = computeUsages.get(QUOTA_VM_CORES_KEY);

        int instances = vmUsage == null ? NO_USAGE : (int) vmUsage.limit();
        int cores = coreUsage == null ? NO_USAGE : (int) coreUsage.limit();
        int ram = FogbowConstants.UNLIMITED_RESOURCE;

        return new ComputeAllocation(instances, cores, ram);
    }

    @VisibleForTesting
    ResourceAllocation buildQuota(ComputeAllocation computeAllocation,
                                  NetworkAllocation networkAllocation,
                                  PublicIpAllocation publicIpAllocation,
                                  VolumeAllocation volumeAllocation) {
        int instances = computeAllocation.getInstances();
        int ram = computeAllocation.getRam();
        int vCPU = computeAllocation.getvCPU();
        int volumes = volumeAllocation.getInstances();
        int storage = volumeAllocation.getStorage();
        int networks = networkAllocation.getInstances();
        int publicIps = publicIpAllocation.getInstances();

        return ResourceAllocation.builder()
                .instances(instances)
                .vCPU(vCPU)
                .ram(ram)
                .volumes(volumes)
                .storage(storage)
                .networks(networks)
                .publicIps(publicIps)
                .build();
    }

    @VisibleForTesting
    Map<String, ComputeUsage> getComputeUsageMap(Azure azure) {
        Map<String, ComputeUsage> computeUsageMap = new HashMap<>();
        List<String> validComputeUsages = Arrays.asList(QUOTA_VM_INSTANCES_KEY, QUOTA_VM_CORES_KEY);

        AzureQuotaSDK.getComputeUsageByRegion(azure, this.defaultRegionName).stream()
                .filter(computeUsage -> validComputeUsages.contains(computeUsage.name().value()))
                .forEach(computeUsage -> computeUsageMap.put(computeUsage.name().value(), computeUsage));

        return computeUsageMap;
    }

    @VisibleForTesting
    Map<String, NetworkUsage> getNetworkUsageMap(Azure azure) {
        Map<String, NetworkUsage> networkUsageMap = new HashMap<>();
        List<String> validNetworkUsages = Arrays.asList(QUOTA_NETWORK_INSTANCES, QUOTA_PUBLIC_IP_ADDRESSES);

        AzureQuotaSDK.getNetworkUsageByRegion(azure, this.defaultRegionName).stream()
                .filter(networkUsage -> validNetworkUsages.contains(networkUsage.name().value()))
                .forEach(networkUsage -> networkUsageMap.put(networkUsage.name().value(), networkUsage));

        return networkUsageMap;
    }

    @VisibleForTesting
    int getMemoryUsage(Azure azure) {
        List<String> sizeNamesInUse = this.getVirtualMachineSizeNamesInUse(azure);
        Map<String, VirtualMachineSize> virtualMachineSizes = this.getVirtualMachineSizesInUse(sizeNamesInUse, azure);
        return this.doGetMemoryUsage(sizeNamesInUse, virtualMachineSizes);
    }

    @VisibleForTesting
    int doGetMemoryUsage(List<String> sizeNamesInUse, Map<String, VirtualMachineSize> virtalMachineSizes) {
        Integer initialValue = NO_USAGE;
        return sizeNamesInUse.stream()
                .map(sizeName -> virtalMachineSizes.get(sizeName))
                .map(virtualMachineSize -> virtualMachineSize.memoryInMB())
                .reduce(initialValue, Integer::sum);
    }

    @VisibleForTesting
    Map<String, VirtualMachineSize> getVirtualMachineSizesInUse(List<String> sizeNames, Azure azure) {
        Map<String, VirtualMachineSize> sizes = new HashMap<>();
        AzureQuotaSDK.getVirtualMachineSizesByRegion(azure, this.defaultRegionName).stream()
                .filter(virtualMachineSize -> sizeNames.contains(virtualMachineSize.name()))
                .forEach(virtualMachineSize -> sizes.put(virtualMachineSize.name(), virtualMachineSize));
        return sizes;
    }

    @VisibleForTesting
    List<String> getVirtualMachineSizeNamesInUse(Azure azure) {
        return AzureQuotaSDK.getVirtualMachines(azure).stream()
                .map(virtualMachine -> virtualMachine.size().toString())
                .collect(Collectors.toList());
    }
}
