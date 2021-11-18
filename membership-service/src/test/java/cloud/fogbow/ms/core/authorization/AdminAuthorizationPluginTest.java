package cloud.fogbow.ms.core.authorization;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.core.PropertiesHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PropertiesHolder.class})
public class AdminAuthorizationPluginTest {

	private AuthorizationPlugin<MsOperation> plugin;
	private final String userIdAdmin1 = "userIdAdmin1";
	private final String userNameAdmin1 = "userNameAdmin1";
	private final String userIdAdmin2 = "userIdAdmin2";
	private final String userNameAdmin2 = "userNameAdmin2";
	private String userIdNotAdmin = "userIdNotAdmin";
	private String userNameNotAdmin = "userNameNotAdmin";
	private String identityProviderId = "providerId";
	private String adminIDsString = String.format("%s,%s", userIdAdmin1, userIdAdmin2);
	
	// test case: When invoking the isAuthorized method with an admin user, 
	// it must return true
	@Test
	public void testIsAuthorizedUserIsAdmin() throws UnauthorizedRequestException, ConfigurationErrorException {
        setUpConfiguration();
		
		SystemUser admin1 = new SystemUser(userIdAdmin1, userNameAdmin1, identityProviderId);
		SystemUser admin2 = new SystemUser(userIdAdmin2, userNameAdmin2, identityProviderId);
		MsOperation operation = new MsOperation();
		
		plugin = new AdminAuthorizationPlugin();
		
		assertTrue(plugin.isAuthorized(admin1, operation));
		assertTrue(plugin.isAuthorized(admin2, operation));
	}
	
	// test case: When invoking the isAuthorized method with a non-admin user,
	// it must throw an UnauthorizedRequestException
	@Test(expected = UnauthorizedRequestException.class)
	public void testIsAuthorizedUserIsNotAdmin() throws UnauthorizedRequestException, ConfigurationErrorException {
        setUpConfiguration();
		
		SystemUser notAdmin = new SystemUser(userIdNotAdmin, userNameNotAdmin, identityProviderId);
		MsOperation operation = new MsOperation();
		
		plugin = new AdminAuthorizationPlugin();
		
		plugin.isAuthorized(notAdmin, operation);
	}

	// test case: When attempting to create an instance of 
	// AdminAuthorizationPlugin using a configuration file 
	// with no admins listed, it must throw a ConfigurationErrorException
	@Test(expected = ConfigurationErrorException.class)
	public void testConfigurationMustSetAtLeastOneAdmin() throws ConfigurationErrorException {
		String emptyAdminIdsString = "";
		PowerMockito.mockStatic(PropertiesHolder.class);
        PropertiesHolder propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.doReturn(emptyAdminIdsString).when(propertiesHolder).getProperty(ConfigurationPropertyKeys.ADMINS_IDS);
        
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
        
        new AdminAuthorizationPlugin();
	}
	
	private void setUpConfiguration() {
		PowerMockito.mockStatic(PropertiesHolder.class);
        PropertiesHolder propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.doReturn(adminIDsString).when(propertiesHolder).getProperty(ConfigurationPropertyKeys.ADMINS_IDS);
        
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(propertiesHolder);
	}

}
