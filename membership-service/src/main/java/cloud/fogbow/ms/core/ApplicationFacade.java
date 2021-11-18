package cloud.fogbow.ms.core;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.apache.log4j.Logger;

import cloud.fogbow.as.core.util.AuthenticationUtil;
import cloud.fogbow.common.constants.FogbowConstants;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.plugins.authorization.AuthorizationPlugin;
import cloud.fogbow.common.util.CryptoUtil;
import cloud.fogbow.common.util.ServiceAsymmetricKeysHolder;
import cloud.fogbow.ms.api.parameters.ProviderPermission;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.constants.Messages;
import cloud.fogbow.ms.core.authorization.MsOperation;

public class ApplicationFacade {

    private static final Logger LOGGER = Logger.getLogger(ApplicationFacade.class);

    private static ApplicationFacade instance;
    private MembershipService membershipService;
    private AuthorizationPlugin<MsOperation> authorizationPlugin;
	private long onGoingRequests;
	private boolean reloading;
    
    public static ApplicationFacade getInstance() {
        if (instance == null) {
            instance = new ApplicationFacade();
        }
        return instance;
    }

    private ApplicationFacade() {
        this.onGoingRequests = 0;
        this.reloading = false;
    }
    
    public List<String> listMembers() throws Exception {
    	startOperation();
    	try {
    		return this.membershipService.listMembers();    		
    	} finally {
    		finishOperation();
    	}
    }

    public String getPublicKey() throws InternalServerErrorException {
    	startOperation();
        try {
            return CryptoUtil.toBase64(ServiceAsymmetricKeysHolder.getInstance().getPublicKey());
        } catch (GeneralSecurityException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (InternalServerErrorException e) {
            throw new InternalServerErrorException(e.getMessage());
        } finally {
        	finishOperation();
        }
    }

    public boolean isTargetAuthorized(String provider) {
    	startOperation();
    	try {
    		return this.membershipService.isTargetAuthorized(provider);    		
    	} finally {
    		finishOperation();
    	}
    }

    public boolean isRequesterAuthorized(String provider) {
    	startOperation();
    	try {
    		return this.membershipService.isRequesterAuthorized(provider);    		
    	} finally {
    		finishOperation();
    	}
    }

    public void setMembershipService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }
    
    public MembershipService getMembershipService() {
        return membershipService;
    }
    
    public void setAuthorizationPlugin(AuthorizationPlugin<MsOperation> authorizationPlugin) {
    	this.authorizationPlugin = authorizationPlugin;
    }

    public void addProvider(String userToken, ProviderPermission permission) throws FogbowException {
        LOGGER.info(String.format(Messages.Log.REMOVING_PROVIDER, permission.getProvider()));
        
        RSAPublicKey asPublicKey = MSPublicKeysHolder.getInstance().getAsPublicKey();
        SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, userToken);
        this.authorizationPlugin.isAuthorized(systemUser, new MsOperation());
        
        setAsReloading();        
        
        try {
            this.membershipService.addMember(permission);
        } finally {
            finishReloading();
        }
    }
    
	public void removeProvider(String userToken, String provider) throws FogbowException {
		LOGGER.info(String.format(Messages.Log.REMOVING_PROVIDER, provider));
		
    	RSAPublicKey asPublicKey = MSPublicKeysHolder.getInstance().getAsPublicKey();
    	SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, userToken);
    	this.authorizationPlugin.isAuthorized(systemUser, new MsOperation());
    	
    	setAsReloading();
    	
    	try {
    		this.membershipService.removeMember(provider);
    	} finally {
    		finishReloading();    		
    	}
	}
	
    public void updateProvider(String userToken, ProviderPermission provider) throws FogbowException {
        LOGGER.info(String.format(Messages.Log.REMOVING_PROVIDER, provider));
        
        RSAPublicKey asPublicKey = MSPublicKeysHolder.getInstance().getAsPublicKey();
        SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, userToken);
        this.authorizationPlugin.isAuthorized(systemUser, new MsOperation());
        
        setAsReloading();
        
        try {
            this.membershipService.updateMember(provider);
        } finally {
            finishReloading();
        }
    }
    
    public void reload(String userToken) throws FogbowException {
    	LOGGER.info(Messages.Log.RELOADING_CONFIGURATION);
    	
    	RSAPublicKey asPublicKey = MSPublicKeysHolder.getInstance().getAsPublicKey();
    	SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, userToken);
    	this.authorizationPlugin.isAuthorized(systemUser, new MsOperation());
    	
    	doReload();
    }

	private void doReload() throws ConfigurationErrorException {
		setAsReloading();
		
		try {
	        while (this.onGoingRequests != 0) {
	            try {
	                Thread.sleep(10);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
			
	        LOGGER.info(Messages.Log.RELOADING_PROPERTIES_HOLDER);
			PropertiesHolder.reset();
			
			LOGGER.info(Messages.Log.RELOADING_PUBLIC_KEYS_HOLDER);
	        MSPublicKeysHolder.reset();
	        
	        LOGGER.info(Messages.Log.RELOADING_MS_KEYS_HOLDER);
	        String publicKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PUBLIC_KEY_FILE_PATH);
	        String privateKeyFilePath = PropertiesHolder.getInstance().getProperty(FogbowConstants.PRIVATE_KEY_FILE_PATH);
	        ServiceAsymmetricKeysHolder.reset(publicKeyFilePath, privateKeyFilePath);
			
	        LOGGER.info(Messages.Log.RELOADING_AUTHORIZATION_PLUGIN);
			this.authorizationPlugin = PluginInstantiator.getAuthorizationPlugin();
			
			LOGGER.info(Messages.Log.RELOADING_MEMBERSHIP_PLUGIN);
			this.membershipService = PluginInstantiator.getMembershipService();
		} finally {
			finishReloading();
		}
	}
	
    public void updateMembershipService(String userToken, String className) throws FogbowException {
        RSAPublicKey asPublicKey = MSPublicKeysHolder.getInstance().getAsPublicKey();
        SystemUser systemUser = AuthenticationUtil.authenticate(asPublicKey, userToken);
        this.authorizationPlugin.isAuthorized(systemUser, new MsOperation());
        
        setAsReloading();
        
        try {
            LOGGER.info(String.format(Messages.Log.CHANGING_MEMBERSHIP_PLUGIN, className));
            MembershipService membershipService = PluginInstantiator.getMembershipService(className);
            setMembershipService(membershipService);
            
            PropertiesHolder.getInstance().setProperty(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_CLASS_KEY, className);
            PropertiesHolder.getInstance().updatePropertiesFile();
        } finally {
            finishReloading();
        }
    }
	
    private void setAsReloading() {
        this.reloading = true;
    }
    
    private void finishReloading() {
    	this.reloading = false;
    }
	
    private void startOperation() {
        while (reloading)
            ;
        synchronized (this) {
            this.onGoingRequests++;
        }
    }
    
    private synchronized void finishOperation() {
        this.onGoingRequests--;
    }
}
