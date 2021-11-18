package cloud.fogbow.ras.core.plugins.interoperability.cloudstack;

import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.attachment.v4_9.CloudStackAttachmentPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.compute.v4_9.CloudStackComputePlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.network.v4_9.CloudStackNetworkPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.publicip.v4_9.CloudStackPublicIpPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.volume.v4_9.CloudStackVolumePlugin;
import org.apache.log4j.Logger;

/**
 * documentation: https://cwiki.apache.org/confluence/display/CLOUDSTACK/CloudStack+objects+states
 */

public class CloudStackStateMapper {
    private static final Logger LOGGER = Logger.getLogger(CloudStackStateMapper.class);

    public static final String COMPUTE_PLUGIN = CloudStackComputePlugin.class.getSimpleName();
    public static final String VOLUME_PLUGIN = CloudStackVolumePlugin.class.getSimpleName();
    public static final String ATTACHMENT_PLUGIN = CloudStackAttachmentPlugin.class.getSimpleName();
    public static final String NETWORK_PLUGIN = CloudStackNetworkPlugin.class.getSimpleName();
    public static final String PUBLIC_IP_PLUGIN = CloudStackPublicIpPlugin.class.getSimpleName();

    public static final String ASSOCIATING_IP_ADDRESS_STATUS = "associating IP address";
    public static final String CREATING_FIREWALL_RULE_STATUS = "creating firewall rule";
    public static final String PROCESSING_STATUS = "processing";
    public static final String READY_STATUS = "ready";
    public static final String FAILURE_STATUS = "failure";

    private static final String CREATING_STATUS = "creating";
    private static final String STARTING_STATUS = "starting";
    private static final String ALLOCATED_STATUS = "allocated";
    private static final String IMPLEMENTING_STATUS = "implementing";
    private static final String IMPLEMENTED_STATUS = "implemented";
    private static final String RUNNING_STATUS = "running";
    private static final String DOWN_STATUS = "shutdowned";
    private static final String SETUP_STATUS = "setup";
    private static final String STOPPED_STATUS = "stopped";
    private static final String STOPPING_STATUS = "stopping";
    private static final String EXPUNGING_STATUS = "expunging";
    private static final String ERROR_STATUS = "error";
    public static final String PENDING_STATUS = "pending";
    private static final String MIGRATING_STATUS = "migrating";

    public static InstanceState map(ResourceType type, String cloudStackState) {

        cloudStackState = cloudStackState.toLowerCase();

        switch (type) {
            case COMPUTE:
                switch(cloudStackState) {
                    case RUNNING_STATUS:
                        return InstanceState.READY;
                    case DOWN_STATUS:
                    case STOPPED_STATUS:
                    case STOPPING_STATUS:
                    case EXPUNGING_STATUS:
                    case MIGRATING_STATUS:
                        return InstanceState.BUSY;
                    case STARTING_STATUS:
                        return InstanceState.CREATING;
                    case ERROR_STATUS:
                        return InstanceState.FAILED;
                    default:
                        LOGGER.error(String.format(Messages.Log.UNDEFINED_INSTANCE_STATE_MAPPING_S_S, cloudStackState,
                                COMPUTE_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            case VOLUME:
                switch(cloudStackState) {
                    case READY_STATUS:
                    case ALLOCATED_STATUS:
                        return InstanceState.READY;
                    case CREATING_STATUS:
                        return InstanceState.CREATING;
                    case ERROR_STATUS:
                    case FAILURE_STATUS:
                        return InstanceState.FAILED;
                    default:
                        LOGGER.error(String.format(Messages.Log.UNDEFINED_INSTANCE_STATE_MAPPING_S_S, cloudStackState,
                                VOLUME_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            case ATTACHMENT:
                switch(cloudStackState) {
                    case READY_STATUS:
                        return InstanceState.READY;
                    case PENDING_STATUS:
                        return InstanceState.CREATING;
                    case ERROR_STATUS:
                    case FAILURE_STATUS:
                        return InstanceState.FAILED;
                    default:
                        LOGGER.error(String.format(Messages.Log.UNDEFINED_INSTANCE_STATE_MAPPING_S_S, cloudStackState,
                                ATTACHMENT_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            case NETWORK:
                switch (cloudStackState) {
                    case IMPLEMENTING_STATUS:
                        return InstanceState.CREATING;
                    case ALLOCATED_STATUS:
                    case SETUP_STATUS:
                    case IMPLEMENTED_STATUS:
                        return InstanceState.READY;
                    case DOWN_STATUS:
                        return InstanceState.BUSY;
                    default:
                        LOGGER.error(String.format(Messages.Log.UNDEFINED_INSTANCE_STATE_MAPPING_S_S, cloudStackState,
                                NETWORK_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            case PUBLIC_IP:
                switch (cloudStackState) {
                    case PROCESSING_STATUS:
                    case ASSOCIATING_IP_ADDRESS_STATUS:
                    case CREATING_FIREWALL_RULE_STATUS:
                        return InstanceState.CREATING;
                    case READY_STATUS:
                        return InstanceState.READY;
                    case FAILURE_STATUS:
                        return InstanceState.FAILED;
                    default:
                        LOGGER.error(String.format(Messages.Log.UNDEFINED_INSTANCE_STATE_MAPPING_S_S, cloudStackState,
                                PUBLIC_IP_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            default:
                LOGGER.error(Messages.Log.INSTANCE_TYPE_NOT_DEFINED);
                return InstanceState.INCONSISTENT;
        }
    }
}
