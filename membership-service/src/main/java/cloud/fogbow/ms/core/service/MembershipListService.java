package cloud.fogbow.ms.core.service;

import java.util.ArrayList;
import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.ms.api.parameters.ProviderPermission;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.constants.Messages;
import cloud.fogbow.ms.core.MembershipService;
import cloud.fogbow.ms.core.PropertiesHolder;

public abstract class MembershipListService implements MembershipService {

    protected static final String SEPARATOR = ",";

    protected List<String> membersList;
    protected List<String> targetMembers;
    protected List<String> requesterMembers;

    public abstract List<String> listMembers() throws Exception;
    public abstract boolean isMember(String provider);
    public abstract boolean isTargetAuthorized(String provider);
    public abstract boolean isRequesterAuthorized(String provider);
    
    protected List<String> readMembers() {
        List<String> membersList = new ArrayList<>();

        String membersListStr = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY);
        for (String member : membersListStr.split(SEPARATOR)) {
            member = member.trim();
            membersList.add(member);
        }

        return membersList;
    }
    
    protected List<String> readTargetMembers() throws ConfigurationErrorException {
        List<String> authorizedTargetMembers = new ArrayList<String>();
        
        String authorizedTargetMembersListStr = PropertiesHolder.getInstance().
        		getProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY);
        
        if (!authorizedTargetMembersListStr.isEmpty()) {
            for (String member : authorizedTargetMembersListStr.split(SEPARATOR)) {
                member = member.trim();
                
                checkProviderIsMember(member);
                authorizedTargetMembers.add(member);
            }
        }
        
        return authorizedTargetMembers;
    }
    
    protected List<String> readRequesterMembers() throws ConfigurationErrorException {
        List<String> authorizedRequesterMembers = new ArrayList<String>();
        
        String authorizedRequesterMembersListStr = PropertiesHolder.getInstance().
        		getProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY);
        
        if (!authorizedRequesterMembersListStr.isEmpty()) {
            for (String member : authorizedRequesterMembersListStr.split(SEPARATOR)) {
                member = member.trim();
                
                checkProviderIsMember(member);
                authorizedRequesterMembers.add(member);
            }
        }
        
        return authorizedRequesterMembers;
    }

	private void checkProviderIsMember(String member) throws ConfigurationErrorException {
		if (!isMember(member)) {
		    throw new ConfigurationErrorException(Messages.Exception.INVALID_MEMBER_NAME);
		}
	}
	
	@Override
	public void addMember(ProviderPermission permission) throws ConfigurationErrorException {
	    String provider = permission.getProvider();
	    boolean isTarget = permission.isTarget();
	    boolean isRequester = permission.isRequester();
	    
        if (isMember(permission.getProvider())) {
            throw new ConfigurationErrorException(Messages.Exception.PROVIDER_IS_ALREADY_A_MEMBER);
        }
        
        validateProviderProperties(isTarget, isRequester);

        membersList.add(provider);
        
        updateProviderList(targetMembers, provider, isTarget);
        updateProviderList(requesterMembers, provider, isRequester);
        
        updateConfigurationFile();
	}
    
	@Override
	public void removeMember(String provider) throws ConfigurationErrorException {
		checkProviderIsMember(provider);
		
		targetMembers.remove(provider);
		requesterMembers.remove(provider);
		membersList.remove(provider);
		
		updateConfigurationFile();
	}

	@Override
	public void updateMember(ProviderPermission permission) throws ConfigurationErrorException {
        String provider = permission.getProvider();
        boolean isTarget = permission.isTarget();
        boolean isRequester = permission.isRequester();
 
	    checkProviderIsMember(provider);
	    validateProviderProperties(isTarget, isRequester);
	    
	    updateProviderList(targetMembers, provider, isTarget);
	    updateProviderList(requesterMembers, provider, isRequester);
	    
	    updateConfigurationFile();
	}
	
	protected void validateMembersList() throws ConfigurationErrorException {
	    for (String provider : membersList) {
	        validateProviderProperties(targetMembers.contains(provider), requesterMembers.contains(provider));
	    }
	}
	
	private void validateProviderProperties(boolean target, boolean requester) throws ConfigurationErrorException {
        if (!target && !requester) {
            throw new ConfigurationErrorException(Messages.Exception.PROVIDER_MUST_BE_TARGET_REQUESTER_OR_BOTH);
        }
    }
	
    private void updateProviderList(List<String> providerList, String provider, boolean shouldContain) {
	    if (shouldContain) {
	        if (!providerList.contains(provider)) {
	            providerList.add(provider);
	        }
	    } else {
	        if (providerList.contains(provider)) {
	            providerList.remove(provider);
	        }
	    }
	}
    
    private void updateConfigurationFile() {
        String newMembersString = String.join(SEPARATOR, membersList);
        PropertiesHolder.getInstance().setProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY, 
                newMembersString);
        
        String newTargetMembersList = String.join(SEPARATOR, targetMembers);
        PropertiesHolder.getInstance().setProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY, 
                newTargetMembersList);
        
        String newRequesterMembersList = String.join(SEPARATOR, requesterMembers);
        PropertiesHolder.getInstance().setProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY, 
                newRequesterMembersList);
        
        PropertiesHolder.getInstance().updatePropertiesFile();
    }
}
