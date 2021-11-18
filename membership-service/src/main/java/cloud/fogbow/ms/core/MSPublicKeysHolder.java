package cloud.fogbow.ms.core;

import java.security.interfaces.RSAPublicKey;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.util.PublicKeysHolder;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;

public class MSPublicKeysHolder {
    private RSAPublicKey asPublicKey;
    private RSAPublicKey rasPublicKey;
    
    private static MSPublicKeysHolder instance;

    private MSPublicKeysHolder() {
    }

    public static synchronized MSPublicKeysHolder getInstance() {
        if (instance == null) {
            instance = new MSPublicKeysHolder();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }
    
    public RSAPublicKey getAsPublicKey() throws FogbowException {
        if (this.asPublicKey == null) {
            String asAddress = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_URL_KEY);
            String asPort = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AS_PORT_KEY);
            this.asPublicKey = PublicKeysHolder.getPublicKey(asAddress, asPort, cloud.fogbow.as.api.http.request.PublicKey.PUBLIC_KEY_ENDPOINT);
        }
        return this.asPublicKey;
    }
    
    public RSAPublicKey getRASPublicKey() throws FogbowException {
        if (this.rasPublicKey == null) {
            String rasAddress = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.RAS_URL_KEY);
            String rasPort = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.RAS_PORT_KEY);
            this.rasPublicKey = PublicKeysHolder.getPublicKey(rasAddress, rasPort, "ras/publicKey");
        }
        return this.rasPublicKey;
    }
}
