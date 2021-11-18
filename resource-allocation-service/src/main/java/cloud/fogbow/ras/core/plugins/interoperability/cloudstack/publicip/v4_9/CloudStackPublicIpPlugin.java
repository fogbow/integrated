package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.publicip.v4_9;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.CloudStackUser;
import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.*;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.api.http.response.PublicIpInstance;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.core.models.orders.PublicIpOrder;
import cloud.fogbow.ras.core.plugins.interoperability.PublicIpPlugin;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudStackCloudUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudStackStateMapper;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.publicip.model.*;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.quota.model.ListPublicIpAddressRequest;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.quota.model.ListPublicIpAddressResponse;

import com.google.common.annotations.VisibleForTesting;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CloudStackPublicIpPlugin implements PublicIpPlugin<CloudStackUser> {
    private static final Logger LOGGER = Logger.getLogger(CloudStackPublicIpPlugin.class);

    static final String DEFAULT_SSH_PORT = "22";
    static final String DEFAULT_PROTOCOL = "TCP";
    static final String PUBLIC_IP_RESOURCE = "Public ip";

    // Since the ip creation and association involves multiple asynchronous requests instance,
    // we need to keep track of where we are in the process in order to fulfill the operation.
    private static Map<String, AsyncRequestInstanceState> asyncRequestInstanceStateMap = new HashMap<>();

    private final String defaultNetworkId;
    private CloudStackHttpClient client;
    private String cloudStackUrl;

    public CloudStackPublicIpPlugin(String confFilePath) {
        Properties properties = PropertiesUtil.readProperties(confFilePath);
        this.cloudStackUrl = properties.getProperty(CloudStackCloudUtils.CLOUDSTACK_URL_CONFIG);
        this.defaultNetworkId = properties.getProperty(CloudStackCloudUtils.DEFAULT_NETWORK_ID_KEY);
        this.client = new CloudStackHttpClient();
    }

    @Override
    public boolean isReady(String cloudState) {
        return CloudStackStateMapper.map(ResourceType.PUBLIC_IP, cloudState).equals(InstanceState.READY);
    }

    @Override
    public boolean hasFailed(String cloudState) {
        return CloudStackStateMapper.map(ResourceType.PUBLIC_IP, cloudState).equals(InstanceState.FAILED);
    }

    @Override
    public String requestInstance(PublicIpOrder publicIpOrder,
                                  CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(Messages.Log.REQUESTING_INSTANCE_FROM_PROVIDER);
        String instanceId = doRequestInstance(publicIpOrder, cloudStackUser);
        return instanceId;
    }

    @Override
    public PublicIpInstance getInstance(PublicIpOrder publicIpOrder,
                                        CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Log.GETTING_INSTANCE_S, publicIpOrder.getInstanceId()));
        return doGetInstance(publicIpOrder, cloudStackUser);
    }

    @Override
    public void deleteInstance(PublicIpOrder publicIpOrder, CloudStackUser cloudStackUser)
            throws FogbowException {

        LOGGER.info(String.format(Messages.Log.DELETING_INSTANCE_S, publicIpOrder.getInstanceId()));
        doDeleteInstance(publicIpOrder, cloudStackUser);
    }

    @VisibleForTesting
    public String doRequestInstance(PublicIpOrder publicIpOrder,
                                    CloudStackUser cloudStackUser)
            throws FogbowException {

        AssociateIpAddressRequest request = new AssociateIpAddressRequest.Builder()
                .networkId(this.defaultNetworkId)
                .build(this.cloudStackUrl);

        String jobId = requestIpAddressAssociation(request, cloudStackUser);
        setAsyncRequestInstanceFirstStep(jobId, publicIpOrder);
        return getInstanceId(publicIpOrder);
    }

    @VisibleForTesting
    void doDeleteInstance(PublicIpOrder publicIpOrder, CloudStackUser cloudStackUser)
            throws FogbowException {

        String instanceId = getInstanceId(publicIpOrder);
        AsyncRequestInstanceState asyncRequestInstanceState = this.asyncRequestInstanceStateMap.get(instanceId);
        if (asyncRequestInstanceState == null) {
            throw new InstanceNotFoundException();
        }
        String ipAddressId = asyncRequestInstanceState.getIpInstanceId();

        DisassociateIpAddressRequest request = new DisassociateIpAddressRequest.Builder()
                .id(ipAddressId)
                .build(this.cloudStackUrl);

        requestDisassociateIpAddress(request, cloudStackUser);
    }

    @VisibleForTesting
    void requestDisassociateIpAddress(DisassociateIpAddressRequest request,
                                      CloudStackUser cloudStackUser)
            throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
    }

    @VisibleForTesting
    PublicIpInstance doGetInstance(PublicIpOrder publicIpOrder,
                                   CloudStackUser cloudStackUser)
            throws FogbowException {

        String instanceId = getInstanceId(publicIpOrder);
        AsyncRequestInstanceState asyncRequestInstanceState = this.asyncRequestInstanceStateMap.get(instanceId);

        boolean isAOperationalFailure = asyncRequestInstanceState == null;
        if (isAOperationalFailure) {
            // This may happen due to a failure in the RAS while this operation was being carried out; since the
            // order was still spawning, the spawning processor will start monitoring this order after the RAS
            // is restarted. Unfortunately, even if the operation succeeded, we cannot retrieve this information
            // and will have to signal that the order has failed.
            LOGGER.error(Messages.Log.INSTANCE_S_OPERATIONAL_LOST_MEMORY_FAILURE);
            return createFailedPublicIpInstance();
        }

        if (asyncRequestInstanceState.isReady()) {
            return createReadyPublicIpInstance(asyncRequestInstanceState, cloudStackUser);
        } else {
            return createCurrentPublicIpInstance(asyncRequestInstanceState, publicIpOrder, cloudStackUser);
        }
    }

    /**
     * Retrieve the current Cloudstack asynchronous job and treat the next operation of the
     * asynchronous request instance flow.
     */
    @VisibleForTesting
    PublicIpInstance createCurrentPublicIpInstance(AsyncRequestInstanceState asyncRequestInstanceState,
                                                  PublicIpOrder publicIpOrder,
                                                  CloudStackUser cloudStackUser)
            throws FogbowException {

        String currentJobId = asyncRequestInstanceState.getCurrentJobId();
        String jsonResponse = CloudStackQueryJobResult.getQueryJobResult(
                this.client, this.cloudStackUrl, currentJobId, cloudStackUser);
        CloudStackQueryAsyncJobResponse response = CloudStackQueryAsyncJobResponse.fromJson(jsonResponse);

        switch (response.getJobStatus()) {
            case CloudStackQueryJobResult.PROCESSING:
                return createProcessingPublicIpInstance();
            case CloudStackQueryJobResult.SUCCESS:
                try {
                    return createNextOperationPublicIpInstance(
                            asyncRequestInstanceState, cloudStackUser, jsonResponse);
                } catch (FogbowException e) {
                    LOGGER.error(Messages.Log.ERROR_WHILE_PROCESSING_ASYNCHRONOUS_REQUEST_INSTANCE_STEP, e);
                    return createFailedPublicIpInstance();
                }
            case CloudStackQueryJobResult.FAILURE:
                try {
                    doDeleteInstance(publicIpOrder, cloudStackUser);
                } catch (FogbowException e) {
                    LOGGER.error(String.format(Messages.Log.ERROR_WHILE_REMOVING_RESOURCE_S_S,
                                                PUBLIC_IP_RESOURCE, publicIpOrder.getInstanceId()));
                }
                return createFailedPublicIpInstance();
            default:
                LOGGER.error(Messages.Log.UNEXPECTED_JOB_STATUS);
                return null;
        }
    }

    /**
     * Execute the next operation of the asynchronous request instance flow.
     */
    @VisibleForTesting
    PublicIpInstance createNextOperationPublicIpInstance(AsyncRequestInstanceState asyncRequestInstanceState,
                                                        CloudStackUser cloudStackUser,
                                                        String jsonResponse)
            throws FogbowException {

        AsyncRequestInstanceState.StateType currentInstanceState = asyncRequestInstanceState.getState();
        switch (currentInstanceState) {
            case ASSOCIATING_IP_ADDRESS:
                doCreatingFirewallOperation(asyncRequestInstanceState, cloudStackUser, jsonResponse);
                return createPublicIpInstance(asyncRequestInstanceState, CloudStackStateMapper.CREATING_FIREWALL_RULE_STATUS);
            case CREATING_FIREWALL_RULE:
                finishAsyncRequestInstanceSteps(asyncRequestInstanceState);
                return createPublicIpInstance(asyncRequestInstanceState, CloudStackStateMapper.READY_STATUS);
            default:
                LOGGER.error(Messages.Log.UNEXPECTED_ERROR);
                return null;
        }
    }

    @VisibleForTesting
    void doCreatingFirewallOperation(AsyncRequestInstanceState asyncRequestInstanceState,
                                     CloudStackUser cloudStackUser,
                                     String jsonResponse)
            throws FogbowException {

        SuccessfulAssociateIpAddressResponse response = SuccessfulAssociateIpAddressResponse.fromJson(jsonResponse);

        doEnableStaticNat(response, asyncRequestInstanceState, cloudStackUser);

        String createFirewallRuleJobId = doCreateFirewallRule(response, cloudStackUser);

        setAsyncRequestInstanceSecondStep(response, asyncRequestInstanceState, createFirewallRuleJobId);
    }

    /**
     * Set the asynchronous request instance to the first step; This step consist of
     * wait the asynchronous Associating Ip Address Operation finishes in the Cloudstack.
     */
    @VisibleForTesting
    void setAsyncRequestInstanceFirstStep(String jobId, PublicIpOrder publicIpOrder) {
        String computeId = publicIpOrder.getComputeId();
        String instanceId = getInstanceId(publicIpOrder);
        AsyncRequestInstanceState asyncRequestInstanceState = new AsyncRequestInstanceState(
                AsyncRequestInstanceState.StateType.ASSOCIATING_IP_ADDRESS, jobId, computeId);
        asyncRequestInstanceState.setOrderInstanceId(instanceId);
        this.asyncRequestInstanceStateMap.put(instanceId, asyncRequestInstanceState);
        LOGGER.info(String.format(Messages.Log.ASYNCHRONOUS_PUBLIC_IP_STATE_S,
                instanceId, AsyncRequestInstanceState.StateType.ASSOCIATING_IP_ADDRESS));
    }

    /**
     * Set the asynchronous request instance to the second step; This step consist of
     * wait the asynchronous Create Firewall Operation finishes in the Cloudstack.
     */
    @VisibleForTesting
    void setAsyncRequestInstanceSecondStep(SuccessfulAssociateIpAddressResponse response,
                                           AsyncRequestInstanceState asyncRequestInstanceState,
                                           String createFirewallRuleJobId) {

        SuccessfulAssociateIpAddressResponse.IpAddress ipAddress = response.getIpAddress();
        String ipAddressId = ipAddress.getId();
        String ip = ipAddress.getIpAddress();

        asyncRequestInstanceState.setIpInstanceId(ipAddressId);
        asyncRequestInstanceState.setIp(ip);
        asyncRequestInstanceState.setCurrentJobId(createFirewallRuleJobId);
        asyncRequestInstanceState.setState(AsyncRequestInstanceState.StateType.CREATING_FIREWALL_RULE);
        LOGGER.info(String.format(Messages.Log.ASYNCHRONOUS_PUBLIC_IP_STATE_S,
                asyncRequestInstanceState.getOrderInstanceId(),
                AsyncRequestInstanceState.StateType.CREATING_FIREWALL_RULE));
    }

    /**
     * Finish the cycle and set as Ready the asynchronous request instance.
     */
    @VisibleForTesting
    void finishAsyncRequestInstanceSteps(AsyncRequestInstanceState asyncRequestInstanceState) {
        asyncRequestInstanceState.setState(AsyncRequestInstanceState.StateType.READY);
        LOGGER.info(String.format(Messages.Log.ASYNCHRONOUS_PUBLIC_IP_STATE_S,
                asyncRequestInstanceState.getOrderInstanceId(), AsyncRequestInstanceState.StateType.READY));
    }

    @VisibleForTesting
    String doCreateFirewallRule(SuccessfulAssociateIpAddressResponse response,
                                CloudStackUser cloudStackUser) throws FogbowException {

        String ipAddressId = response.getIpAddress().getId();
        CreateFirewallRuleRequest request = new CreateFirewallRuleRequest.Builder()
                .protocol(DEFAULT_PROTOCOL)
                .startPort(DEFAULT_SSH_PORT)
                .endPort(DEFAULT_SSH_PORT)
                .ipAddressId(ipAddressId)
                .build(this.cloudStackUrl);

        return requestCreateFirewallRule(request, cloudStackUser);
    }

    @VisibleForTesting
    void doEnableStaticNat(SuccessfulAssociateIpAddressResponse response,
                      AsyncRequestInstanceState asyncRequestInstanceState,
                      CloudStackUser cloudStackUser) throws FogbowException {

        String ipAddressId = response.getIpAddress().getId();
        String computeInstanceId = asyncRequestInstanceState.getComputeInstanceId();

        EnableStaticNatRequest request = new EnableStaticNatRequest.Builder()
                .ipAddressId(ipAddressId)
                .virtualMachineId(computeInstanceId)
                .build(this.cloudStackUrl);

        requestEnableStaticNat(request, cloudStackUser);
    }

    @VisibleForTesting
    PublicIpInstance createReadyPublicIpInstance(
            AsyncRequestInstanceState asyncRequestInstanceState,
            CloudStackUser cloudStackUser) throws FogbowException {

        /*
         * The jobId used as an identifier in the verification of the status of
         * completion of the processing of the creation and association of the
         * resources does not include in the response content, data that confirm
         * the dissociation and removal of the address, being necessary to check
         * with the cloud if the resource remains active.
         */
        checkIpAddressExist(asyncRequestInstanceState, cloudStackUser);
        return createPublicIpInstance(asyncRequestInstanceState, CloudStackStateMapper.READY_STATUS);
    }

    @VisibleForTesting
    void checkIpAddressExist(
            AsyncRequestInstanceState asyncRequestInstanceState,
            CloudStackUser cloudStackUser) throws FogbowException {

        String publicIpAddressId = asyncRequestInstanceState.getIpInstanceId();
        ListPublicIpAddressRequest request = buildPublicIpAddressRequest(publicIpAddressId);

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        String jsonResponse = CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
        ListPublicIpAddressResponse response = ListPublicIpAddressResponse.fromJson(jsonResponse);
        if (response.getPublicIpAddresses() == null) {
            throw new InstanceNotFoundException(Messages.Exception.INSTANCE_NOT_FOUND);
        }
    }

    @VisibleForTesting
    ListPublicIpAddressRequest buildPublicIpAddressRequest(String publicIpAddressId)
            throws InternalServerErrorException {

        ListPublicIpAddressRequest request = new ListPublicIpAddressRequest.Builder()
                .id(publicIpAddressId)
                .build(this.cloudStackUrl);

        return request;
    }

    @VisibleForTesting
    PublicIpInstance createFailedPublicIpInstance() {
        return createPublicIpInstance(null, CloudStackStateMapper.FAILURE_STATUS);
    }

    @VisibleForTesting
    PublicIpInstance createProcessingPublicIpInstance() {
        return createPublicIpInstance(null, CloudStackStateMapper.PROCESSING_STATUS);
    }

    @VisibleForTesting
    PublicIpInstance createPublicIpInstance(AsyncRequestInstanceState asyncRequestInstanceState,
                                           String state) {
        String id = null;
        String ip = null;
        if (asyncRequestInstanceState != null) {
            id = asyncRequestInstanceState.getIpInstanceId();
            ip = asyncRequestInstanceState.getIp();
        }
        return new PublicIpInstance(id, state, ip);
    }

    /**
     * We don't have the id of the ip address yet, but since the instance id is only used
     * by the plugin, we can return an orderId as an instanceId in the plugin
     */
    private String getInstanceId(PublicIpOrder publicIpOrder) {
        return publicIpOrder.getId();
    }

    @VisibleForTesting
    String requestIpAddressAssociation(AssociateIpAddressRequest request,
                                       CloudStackUser cloudStackUser)
            throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        String jsonResponse = CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
        AssociateIpAddressAsyncJobIdResponse response = AssociateIpAddressAsyncJobIdResponse.fromJson(jsonResponse);
        return response.getJobId();
    }

    @VisibleForTesting
    void requestEnableStaticNat(EnableStaticNatRequest request,
                                CloudStackUser cloudStackUser)
            throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudStackUser.getToken());

        CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudStackUser);
    }

    @VisibleForTesting
    String requestCreateFirewallRule(CreateFirewallRuleRequest request,
                                     CloudStackUser cloudUser)
            throws FogbowException {

        URIBuilder uriRequest = request.getUriBuilder();
        CloudStackUrlUtil.sign(uriRequest, cloudUser.getToken());

        String jsonResponse = CloudStackCloudUtils.doRequest(this.client, uriRequest.toString(), cloudUser);
        CreateFirewallRuleAsyncResponse response = CreateFirewallRuleAsyncResponse.fromJson(jsonResponse);
        return response.getJobId();
    }

    @VisibleForTesting
    void setClient(CloudStackHttpClient client) {
        this.client = client;
    }

    @VisibleForTesting
    void setAsyncRequestInstanceStateMap(Map<String, AsyncRequestInstanceState> asyncRequestInstanceStateMap) {

        this.asyncRequestInstanceStateMap = asyncRequestInstanceStateMap;
    }

    // TODO(chico) - This method will be removed after the Cloudstack Security Rule PR is accepted.
    public static String getPublicIpId(String orderId) {
        return asyncRequestInstanceStateMap.get(orderId).getIpInstanceId();
    }
}
