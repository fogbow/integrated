package cloud.fogbow.ms.api.parameters;

import java.util.HashMap;
import java.util.Map;

public class Provider {
    public static final String PROVIDER_KEY = "provider";
    private String provider;

    public Provider() {
        
    }
    
    public Provider(String provider) {
        this.provider = provider;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public Map<String, String> asRequestBody() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put(PROVIDER_KEY, this.provider);
        return body;
    }
}
