package cloud.fogbow.ms.core;

import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.ms.api.parameters.ProviderPermission;

public interface MembershipService {

    /**
     * This method returns a list of XMPP members ID.
     *
     * @return List of string with XMPP members ID.
     * @throws Exception
     */
    public List<String> listMembers() throws Exception;
    
    /**
     * Returns whether or not the given provider is in 
     * the list of known providers.
     * 
     * @param provider - the name of the provider to check membership.
     * @return a boolean value stating whether the given 
     * provider is a member or not.
     */
    public boolean isMember(String provider);

    /**
     * Returns whether or not the local provider is allowed 
     * to perform operations in the given provider
     * 
     * @param provider - the name of the provider to authorize.
     * @return a boolean value stating whether or not the operation
     * is authorized.
     */
    public boolean isTargetAuthorized(String provider);

    /**
     * Returns whether or not the given provider is allowed
     * to perform operations in the local provider.
     * 
     * @param provider - the name of the provider to authorize.
     * @return a boolean value stating whether or not the operation
     * is authorized.
     */
    public boolean isRequesterAuthorized(String provider);

    /**
     * Adds given provider to the list of members, using the
     * given permission information.
     * 
     * @param permission - the permissions to use when authorizing
     * operations related to the new provider.
     * @throws ConfigurationErrorException - If the given provider
     * is already in the members list or the permissions are invalid.
     */
    public void addMember(ProviderPermission permission) throws ConfigurationErrorException;
    
    /**
     * Updates permissions for the given provider.
     * 
     * @param provider - the new permissions to use.
     * @throws ConfigurationErrorException - If the given provider is
     * not a member or the permissions are invalid.
     */
    public void updateMember(ProviderPermission provider) throws ConfigurationErrorException;
   
    /**
     * Removes given provider from all the list of members.
     * 
     * @param provider - the name of the provider to remove.
     * @throws ConfigurationErrorException - If the given provider
     * is not a member.
     */
	public void removeMember(String provider) throws ConfigurationErrorException;
}
