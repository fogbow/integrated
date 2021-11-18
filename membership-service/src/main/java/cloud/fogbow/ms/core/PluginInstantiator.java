package cloud.fogbow.ms.core;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.core.authorization.AdminAuthorizationPlugin;
import cloud.fogbow.ms.core.authorization.MsOperation;
import cloud.fogbow.ms.core.service.AllowList;

public class PluginInstantiator {
    private static ClassFactory classFactory = new ClassFactory();
    
    public static MembershipService getMembershipService() throws ConfigurationErrorException {
        if (PropertiesHolder.getInstance().getProperties().containsKey(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_CLASS_KEY)) {
            String className = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_CLASS_KEY);
            return getMembershipService(className);
        } else {
            return new AllowList();
        }
    }

	public static AuthorizationPlugin<MsOperation> getAuthorizationPlugin() throws ConfigurationErrorException {
        if (PropertiesHolder.getInstance().getProperties().containsKey(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY)) {
            String className = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.AUTHORIZATION_PLUGIN_CLASS_KEY);
            return getAuthorizationPlugin(className);
        } else {
            return new AdminAuthorizationPlugin();
        }
	}
	
	public static AuthorizationPlugin<MsOperation> getAuthorizationPlugin(String className) {
        return (AuthorizationPlugin<MsOperation>) PluginInstantiator.classFactory.createPluginInstance(className);
	}
	
	public static MembershipService getMembershipService(String className) {
	    return (MembershipService) PluginInstantiator.classFactory.createPluginInstance(className);
	}
        
}
