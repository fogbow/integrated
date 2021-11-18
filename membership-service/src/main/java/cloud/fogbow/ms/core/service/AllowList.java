package cloud.fogbow.ms.core.service;

import java.util.List;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.ms.core.MembershipService;

public class AllowList extends MembershipListService implements MembershipService  {
    
    public AllowList() throws ConfigurationErrorException {
        this.membersList = readMembers();
        this.targetMembers = readTargetMembers();
        this.requesterMembers = readRequesterMembers();
        
        validateMembersList();
    }

    /**
     * Read list of XMPP members ID from membership config file.
     */
    @Override
    public List<String> listMembers() {
        return this.membersList;
    }

    @Override
    public boolean isMember(String provider) {
        return this.membersList.contains(provider);
    }

    @Override
    public boolean isTargetAuthorized(String provider) {
        return this.targetMembers.contains(provider);
    }

    @Override
    public boolean isRequesterAuthorized(String provider) {
        return this.requesterMembers.contains(provider);
    }
}
