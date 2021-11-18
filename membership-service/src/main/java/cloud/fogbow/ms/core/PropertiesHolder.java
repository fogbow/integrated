package cloud.fogbow.ms.core;

import cloud.fogbow.common.util.PropertiesUtil;
import cloud.fogbow.common.exceptions.FatalErrorException;
import cloud.fogbow.common.util.HomeDir;
import cloud.fogbow.ms.constants.SystemConstants;

import java.util.Properties;

public class PropertiesHolder {
    private Properties properties;
    private static PropertiesHolder instance;

    private PropertiesHolder() throws FatalErrorException {
        String path = HomeDir.getPath();
        String configFileName = path + SystemConstants.CONF_FILE_NAME;
        this.properties = PropertiesUtil.readProperties(configFileName);
    }

    public static synchronized PropertiesHolder getInstance() throws FatalErrorException {
        if (instance == null) {
            instance = new PropertiesHolder();
        }
        return instance;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
    
    public void setProperty(String propertyName, String propertyValue) {
    	properties.setProperty(propertyName, propertyValue);
    }

    public String getProperty(String propertyName, String defaultPropertyValue) {
        String propertyValue = this.properties.getProperty(propertyName, defaultPropertyValue);
        if (propertyValue.trim().isEmpty()) {
            propertyValue = defaultPropertyValue;
        }
        return propertyValue;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public static synchronized void reset() {
        instance = null;
    }
    
    public void updatePropertiesFile() {
        String path = HomeDir.getPath();
        String configFileName = path + SystemConstants.CONF_FILE_NAME;
    	PropertiesUtil.writeProperties(properties, configFileName);
    }
}
