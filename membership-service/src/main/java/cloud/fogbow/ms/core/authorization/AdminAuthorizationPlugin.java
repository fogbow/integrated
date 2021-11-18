package cloud.fogbow.ms.core.authorization;

import java.util.HashSet;
import java.util.Set;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.constants.Messages;
import cloud.fogbow.ms.core.PropertiesHolder;

public class AdminAuthorizationPlugin implements AuthorizationPlugin<MsOperation> {

	private static final String SEPARATOR = ",";
	private Set<String> adminsIds;
	
	public AdminAuthorizationPlugin() throws ConfigurationErrorException {
		adminsIds = new HashSet<String>();
		String adminsIdsString = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.ADMINS_IDS);
		
		if (adminsIdsString.isEmpty()) {
			throw new ConfigurationErrorException(Messages.Exception.NO_ADMIN_SPECIFIED);
		}
		
		for (String adminId : adminsIdsString.split(SEPARATOR)) {
			adminsIds.add(adminId);
		}
	}
	
	
	@Override
	public boolean isAuthorized(SystemUser systemUser, MsOperation operation) throws UnauthorizedRequestException {
		String userId = systemUser.getId();
		
		if (!adminsIds.contains(userId)) {
			throw new UnauthorizedRequestException(Messages.Exception.USER_IS_NOT_ADMIN);
		}
		
		return true;
	}

	@Override
	public void setPolicy(String policy) {
		// Currently there is no implementation 
		// for this version of AuthorizationPlugin
	}

	@Override
	public void updatePolicy(String policy) {
		// Currently there is no implementation 
		// for this version of AuthorizationPlugin
	}
}
