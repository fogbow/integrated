package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.publicip.model;

public class AsyncRequestInstanceState {

    private StateType state;
    private String orderInstanceId;
    private String currentJobId;
    private String ip;
    private String ipInstanceId;
    private String computeInstanceId;

    public AsyncRequestInstanceState(StateType state, String currentJobId, String computeInstanceId) {
        this.state = state;
        this.currentJobId = currentJobId;
        this.computeInstanceId = computeInstanceId;
    }

    public boolean isReady() {
        return this.state.equals(StateType.READY);
    }

    public StateType getState() {
        return state;
    }

    public String getCurrentJobId() {
        return currentJobId;
    }

    public String getComputeInstanceId() {
        return computeInstanceId;
    }

    public void setState(StateType state) {
        this.state = state;
    }

    public void setCurrentJobId(String currentJobId) {
        this.currentJobId = currentJobId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpInstanceId() {
        return ipInstanceId;
    }

    public void setIpInstanceId(String ipInstanceId) {
        this.ipInstanceId = ipInstanceId;
    }

    public void setOrderInstanceId(String orderInstanceId) {
        this.orderInstanceId = orderInstanceId;
    }

    public String getOrderInstanceId() {
        return orderInstanceId;
    }

    public enum StateType {
        ASSOCIATING_IP_ADDRESS, CREATING_FIREWALL_RULE, READY
    }

}
