package cloud.fogbow.ms.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.PublicKeysHolder;
import cloud.fogbow.ms.api.parameters.ProviderPermission;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.core.authorization.AdminAuthorizationPlugin;
import cloud.fogbow.ms.core.authorization.MsOperation;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationUtil.class, MSPublicKeysHolder.class, 
	PropertiesHolder.class, PublicKeysHolder.class,
	PluginInstantiator.class })
public class ApplicationFacadeTest {

	private ApplicationFacade facade;
	private MembershipService membershipService;
	private AuthorizationPlugin<MsOperation> authorizationPlugin;
	private String member1 = "member1";
	private String member2 = "member2";
	private List<String> members = Arrays.asList(member1, member2);
	
    private String token = "userToken";
    private String userId = "userId";
    private String userName = "userName";
    private String provider = "provider";
    
    private String newMembershipServiceClassName = "newclass";
	
	private SystemUser systemUser; 
	private RSAPublicKey key;
	private MsOperation operation;
    private PropertiesHolder propertiesHolder;

    @Before
    public void setUp() throws FogbowException {
		this.operation = new MsOperation();
		this.systemUser = new SystemUser(userId, userName, provider);
		
		// authentication
		this.key = Mockito.mock(RSAPublicKey.class);

		PowerMockito.mockStatic(MSPublicKeysHolder.class);
		MSPublicKeysHolder keysHolder = Mockito.mock(MSPublicKeysHolder.class);
		Mockito.doReturn(key).when(keysHolder).getAsPublicKey();
		BDDMockito.given(MSPublicKeysHolder.getInstance()).willReturn(keysHolder);
		
		PowerMockito.mockStatic(AuthenticationUtil.class);
		BDDMockito.given(AuthenticationUtil.authenticate(key, token)).willReturn(systemUser);
		
		// authorization
		this.authorizationPlugin = Mockito.mock(AdminAuthorizationPlugin.class);
		Mockito.doReturn(true).when(this.authorizationPlugin).isAuthorized(systemUser, operation);
		
		this.membershipService = Mockito.mock(MembershipService.class);
		
		this.facade = ApplicationFacade.getInstance();
		
		this.facade.setMembershipService(membershipService);
		this.facade.setAuthorizationPlugin(authorizationPlugin);
    }
    
    // test case: When invoking the updateMembershipService method, it must 
    // authorize the operation, call the PluginInstantiator to get a new 
    // MembershipService instance and set this instance as the one used 
    // by the facade. Also, the configuration file must be updated.
    @Test
    public void testUpdateMembershipService() throws FogbowException {
        // set up
        MembershipService membershipService = Mockito.mock(MembershipService.class);
        
        PowerMockito.mockStatic(PropertiesHolder.class);
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(this.propertiesHolder);
                
        PowerMockito.mockStatic(PluginInstantiator.class);
        BDDMockito.given(PluginInstantiator.getMembershipService(newMembershipServiceClassName)).willReturn(membershipService);
        
        
        this.facade.updateMembershipService(token, newMembershipServiceClassName);
        
        
        // verify membership service is updated
        assertEquals(this.facade.getMembershipService(), membershipService);
        
        // verify authorization
        Mockito.verify(authorizationPlugin, Mockito.times(1)).isAuthorized(systemUser, operation);
        
        // verify configuration is update
        Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_CLASS_KEY, 
                newMembershipServiceClassName);
        Mockito.verify(propertiesHolder, Mockito.times(1)).updatePropertiesFile();
    }
    
    // test case: When invoking the updateMembershipService and the operation
    // is not authorized, it must throw an UnauthorizedRequestException.
    @Test(expected = UnauthorizedRequestException.class)
    public void testUpdateMembershipServiceUnauthorizedOperation() throws FogbowException {
        Mockito.doThrow(new UnauthorizedRequestException()).when(this.authorizationPlugin).isAuthorized(systemUser, operation);
        
        this.facade.updateMembershipService(token, newMembershipServiceClassName);
    }
    
	// test case: When invoking the listMembers method, it
    // must call the listMembers method of the MembershipService
    // instance it holds and return a list containing the same
    // elements.
	@Test
	public void testListMembers() throws Exception {
		this.facade = ApplicationFacade.getInstance();
		
		Mockito.doReturn(members).when(this.membershipService).listMembers();
		
		List<String> returnedMembersList = facade.listMembers();
		
		assertEquals(returnedMembersList.size(), 2);
		assertTrue(returnedMembersList.contains(member1));
		assertTrue(returnedMembersList.contains(member2));
		
		Mockito.verify(this.membershipService, Mockito.times(1)).listMembers();
	}
	
	// test case: When invoking the isTargetAuthorized method, 
	// it must call the isTargetAuthorized method of the MembershipService
	// instance it holds.
    @Test
    public void testIsTargetAuthorized() {
        Mockito.doReturn(true).when(this.membershipService).isTargetAuthorized(provider);
        
        assertTrue(this.facade.isTargetAuthorized(provider));
        
        Mockito.verify(membershipService, Mockito.times(1)).isTargetAuthorized(provider);
    }
	
    // test case: When invoking the isRequesterAuthorized method, 
    // it must call the isRequesterAuthorized method of the MembershipService
    // instance it holds.
	@Test
	public void testIsRequesterAuthorized() {
	    Mockito.doReturn(true).when(this.membershipService).isRequesterAuthorized(provider);
	    
	    assertTrue(this.facade.isRequesterAuthorized(provider));
	    
	    Mockito.verify(membershipService, Mockito.times(1)).isRequesterAuthorized(provider);
	}
	
	// test case: When invoking the addProvider method, it must 
	// authorize the operation and call the addMember method of 
	// the MembershipService instance it holds.
	@Test
	public void testAddProvider() throws FogbowException {
	    boolean target = true;
	    boolean requester = true;
	    ProviderPermission permission = new ProviderPermission(provider, target, requester);
	    
		this.facade.addProvider(token, permission);
		
		Mockito.verify(authorizationPlugin, Mockito.times(1)).isAuthorized(systemUser, operation);
		Mockito.verify(membershipService, Mockito.times(1)).addMember(permission);
	}
	
	// test case: When invoking the addProvider method and 
	// the operation is not authorized, it must throw an
	// UnauthorizedRequestException
	@Test(expected = UnauthorizedRequestException.class)
	public void testAddProviderUnauthorizedOperation() throws FogbowException {
        boolean target = true;
        boolean requester = true;
        ProviderPermission permission = new ProviderPermission(provider, target, requester);

		Mockito.doThrow(new UnauthorizedRequestException()).when(this.authorizationPlugin).isAuthorized(systemUser, operation);
		
		this.facade.addProvider(token, permission);
	}
	
	// test case: When invoking the removeProvider method, it must 
	// authorize the operation and call the removeMember method of 
	// the MembershipService instance it holds.
	@Test
	public void testRemoveProvider() throws FogbowException {
		this.facade.removeProvider(token, provider);
		
		Mockito.verify(authorizationPlugin, Mockito.times(1)).isAuthorized(systemUser, operation);
		Mockito.verify(membershipService, Mockito.times(1)).removeMember(provider);
	}
	
	// test case: When invoking the updateProvider method, it must
	// authorize the operation and call the updateMember method of
	// the MembershipService instance it holds.
	@Test
	public void testUpdateProvider() throws FogbowException {
        boolean target = true;
        boolean requester = true;
        ProviderPermission permission = new ProviderPermission(provider, target, requester);

        this.facade.updateProvider(token, permission);

        Mockito.verify(authorizationPlugin, Mockito.times(1)).isAuthorized(systemUser, operation);
        Mockito.verify(membershipService, Mockito.times(1)).updateMember(permission);
	}

	// test case: When invoking the removeProvider method and 
	// the operation is not authorized, it must throw an
	// UnauthorizedRequestException
	@Test(expected = UnauthorizedRequestException.class)
	public void testRemoveProviderUnauthorizedOperation() throws FogbowException {
		Mockito.doThrow(new UnauthorizedRequestException()).when(this.authorizationPlugin).isAuthorized(systemUser, operation);
		
		this.facade.removeProvider(token, provider);
	}
}
