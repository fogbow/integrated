package cloud.fogbow.ms.api.parameters;

public class ProviderPermission {
    private String provider;
    private boolean target;
    private boolean requester;
    
    public ProviderPermission() {
        
    }
    
    public ProviderPermission(String provider, boolean target, boolean requester) {
        this.provider = provider;
        this.target = target;
        this.requester = requester;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public boolean isTarget() {
        return target;
    }
    
    public boolean isRequester() {
        return requester;
    }
}
