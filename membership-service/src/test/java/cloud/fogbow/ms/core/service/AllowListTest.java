package cloud.fogbow.ms.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.ms.api.parameters.ProviderPermission;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.core.MembershipService;
import cloud.fogbow.ms.core.PropertiesHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PropertiesHolder.class})
public class AllowListTest {

    private MembershipService service;

    private String memberAuthorizedAsRequesterAndTarget = "requesterAndTarget";
    private String memberAuthorizedAsRequester = "requester";
    private String memberAuthorizedAsTarget = "target";
    private String notMember1 = "notMember1";
    private String newTargetMember = "newTargetMember";
    private String newRequesterMember = "newRequesterMember";
    private String newTargetAndRequesterMember = "newTargetAndRequesterMember";
    
    private List<String> membersList = Arrays.asList(memberAuthorizedAsRequesterAndTarget, 
            memberAuthorizedAsRequester, memberAuthorizedAsTarget);
    private List<String> allowedRequesters = Arrays.asList(memberAuthorizedAsRequesterAndTarget, 
                                        memberAuthorizedAsRequester);
    private List<String> allowedTargets = Arrays.asList(memberAuthorizedAsRequesterAndTarget, 
                                        memberAuthorizedAsTarget);
    
    private String membersListString = String.join(",", membersList);
    private String allowedRequestersList = String.join(",", allowedRequesters);
    private String allowedTargetsList = String.join(",", allowedTargets);

    private String emptyAllowedTargetsList = "";
    private String emptyAllowedRequestersList = "";
    
    private PropertiesHolder propertiesHolder;

    // test case: When invoking the listMembers method from an instance created with
    // the MembershipService class constructor with a valid parameter, it must list
    // the configured membership in the file passed by parameter.
    @Test
    public void testListMembers() throws Exception {
        setUpAllowListWithDefaultLists();
        
        List<String> membersId = this.service.listMembers();

        // verify
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsTarget));
    }
    
    // test case: When invoking the isMember method, it must return whether or 
    // not the provider passed as argument is member, based on the configuration file.
    @Test
    public void testIsMember() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsRequester));
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isMember(notMember1));
    }
    
    // test case: When invoking the isTargetAuthorized method, it must return 
    // whether or not local users are allowed to perform operations in the 
    // remote provider passed as argument. In the case of AllowList implementation,
    // it must return whether or not the provider is in the "allowed" list.
    @Test
    public void testIsTargetAuthorized() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isTargetAuthorized(notMember1));
        Assert.assertFalse(this.service.isTargetAuthorized(""));
    }
    
    // test case: When invoking the isRequesterAuthorized method, it must return
    // whether or not the remote provider passed as argument is allowed to 
    // perform local operations. In the case of AllowList implementation,
    // it must return whether or not the provider is in the "allowed" list.
    @Test
    public void testIsRequesterAuthorized() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(notMember1));
        Assert.assertFalse(this.service.isTargetAuthorized(""));
    }
    
    // test case: When invoking the isTargetAuthorized method on an AllowList instance with empty
    // allowed target lists, it must return false for all known members and false to unknown providers.
    @Test
    public void testIsTargetAuthorizedEmptyNotAllowedTargetsList() throws ConfigurationErrorException {
        setUpAllowListWithEmptyAllowedTargetsList();
        
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isTargetAuthorized(notMember1));
        Assert.assertFalse(this.service.isTargetAuthorized(""));
    }
    
    // test case: When invoking the isRequesterAuthorized method on an AllowList instance with empty
    // allowed requester lists, it must return false for all known members and false to unknown providers.
    @Test
    public void testIsTargetAuthorizedEmptyNotAllowedRequestersList() throws ConfigurationErrorException {
        setUpAllowListWithEmptyAllowedRequestersList();
        
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(notMember1));
        Assert.assertFalse(this.service.isRequesterAuthorized(""));
    }

    // test case: When invoking the addMember method, it must add the given provider correctly, 
    // so that the change is reflected on the return value of the methods listMembers, isMember, 
    // isTargetAuthorized and isRequesterAuthorized. Also, the configuration file must be updated.
    @Test
    public void testAddMember() throws Exception {
    	setUpAllowListWithDefaultLists();
    	List<String> expectedMembersList = new ArrayList<String>(membersList);
    	List<String> expectedTargetsList = new ArrayList<String>(allowedTargets);
    	List<String> expectedRequestersList = new ArrayList<String>(allowedRequesters);    	
    	
        // verify before adding member
    	List<String> membersId = this.service.listMembers();
        Assert.assertEquals(3, membersId.size());
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsTarget));
        Assert.assertFalse(membersId.contains(newTargetMember));
        Assert.assertFalse(membersId.contains(newRequesterMember));
        Assert.assertFalse(membersId.contains(newTargetAndRequesterMember));
    	
        // add first member
    	this.service.addMember(new ProviderPermission(newTargetMember, true, false));
    
    	expectedMembersList.add(newTargetMember);
    	expectedTargetsList.add(newTargetMember);

        // verify configuration is updated
        checkUpdatedConfiguration(String.join(",", expectedMembersList), String.join(",", expectedTargetsList), 
                String.join(",", expectedRequestersList));
        Mockito.verify(propertiesHolder, Mockito.times(1)).updatePropertiesFile();
        
    	// add second member
        this.service.addMember(new ProviderPermission(newRequesterMember, false, true));

        expectedMembersList.add(newRequesterMember);
        expectedRequestersList.add(newRequesterMember);
        
        // verify configuration is updated
        checkUpdatedConfiguration(String.join(",", expectedMembersList), String.join(",", expectedTargetsList), 
                String.join(",", expectedRequestersList));
        Mockito.verify(propertiesHolder, Mockito.times(2)).updatePropertiesFile();

        // add third member
    	this.service.addMember(new ProviderPermission(newTargetAndRequesterMember, true, true));
    	
        expectedMembersList.add(newTargetAndRequesterMember);
        expectedTargetsList.add(newTargetAndRequesterMember);
        expectedRequestersList.add(newTargetAndRequesterMember);

        // verify configuration is updated
        checkUpdatedConfiguration(String.join(",", expectedMembersList), String.join(",", expectedTargetsList), 
                String.join(",", expectedRequestersList));
        Mockito.verify(propertiesHolder, Mockito.times(3)).updatePropertiesFile();
                
        // verify after adding member
        List<String> updateMembersId = this.service.listMembers();

        Assert.assertEquals(6, updateMembersId.size());
        Assert.assertTrue(updateMembersId.contains(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(updateMembersId.contains(memberAuthorizedAsRequester));
        Assert.assertTrue(updateMembersId.contains(memberAuthorizedAsTarget));
        Assert.assertTrue(updateMembersId.contains(newTargetMember));
        Assert.assertTrue(updateMembersId.contains(newRequesterMember));
        Assert.assertTrue(updateMembersId.contains(newTargetAndRequesterMember));
        
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsRequester));
        Assert.assertTrue(this.service.isMember(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isMember(newTargetMember));
        Assert.assertTrue(this.service.isMember(newRequesterMember));
        Assert.assertTrue(this.service.isMember(newTargetAndRequesterMember));
        
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        Assert.assertTrue(this.service.isTargetAuthorized(newTargetMember));
        Assert.assertFalse(this.service.isTargetAuthorized(newRequesterMember));
        Assert.assertTrue(this.service.isTargetAuthorized(newTargetAndRequesterMember));
        
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(newTargetMember));
        Assert.assertTrue(this.service.isRequesterAuthorized(newRequesterMember));
        Assert.assertTrue(this.service.isRequesterAuthorized(newTargetAndRequesterMember));
    }

    // test case: When invoking the addMember method with a provider which is already a member, 
    // it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testAddingAlreadyKnownProviderMustFail() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        try {
            this.service.addMember(new ProviderPermission(newTargetMember, true, false));
        } catch (Exception e) {
            Assert.fail("This call should not fail.");
        }
        
        this.service.addMember(new ProviderPermission(newTargetMember, true, false));
    }
    
    // test case: When invoking the addMember method with a provider with no permissions,
    // it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testAddingNoTargetAndNoRequesterMemberMustFail() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        this.service.addMember(new ProviderPermission(newTargetMember, false, false));
    }
    
    // test case: When creating an AllowList instance using a configuration file 
    // containing a provider with no permissions, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorMustCheckIfAllMembersHavePermissions() throws ConfigurationErrorException {
        setUpAllowListWithProviderWithNoPermission();
    }
    
    // test case: When invoking the updateMember method, it must update the given provider 
    // permissions correctly, so that the change is reflected on the return value of the methods
    // isTargetAuthorized and isRequesterAuthorized. Also, the configuration file must be updated.
    @Test
    public void testUpdateMember() throws Exception {
        setUpAllowListWithDefaultLists();
        List<String> expectedMembersList = new ArrayList<String>(membersList);
        List<String> expectedTargetsList = new ArrayList<String>(allowedTargets);
        List<String> expectedRequestersList = new ArrayList<String>(allowedRequesters);  
        
        // verify before updating member
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
        
        // target only member is now a requester only member
        this.service.updateMember(new ProviderPermission(memberAuthorizedAsTarget, false, true));
        // requester only member is now a target only member
        this.service.updateMember(new ProviderPermission(memberAuthorizedAsRequester, true, false));
        
        expectedTargetsList.remove(memberAuthorizedAsTarget);
        expectedTargetsList.add(memberAuthorizedAsRequester);
        expectedRequestersList.remove(memberAuthorizedAsRequester);
        expectedRequestersList.add(memberAuthorizedAsTarget);
        
        // verify configuration is updated
        checkUpdatedConfiguration(String.join(",", expectedMembersList), String.join(",", expectedTargetsList), 
                String.join(",", expectedRequestersList));
        Mockito.verify(propertiesHolder, Mockito.times(2)).updatePropertiesFile();
        
        // verify after updating member
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
    }
    
    // test case: When invoking the updateMember method with a provider which is not on 
    // the memberList, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testUpdatingNotKnownMemberMustFail() throws ConfigurationErrorException {
        setUpAllowListWithDefaultLists();
        
        this.service.updateMember(new ProviderPermission(notMember1, false, true));
    }
    
    // test case: When invoking the removeMember method, it must remove the given provider correctly
    // from all internal lists, so that the change is reflected on the return value of the methods listMembers, 
    // isTargetAuthorized and isRequesterAuthorized. Also, the configuration file must be updated.
    @Test
    public void testRemoveMember() throws Exception {
    	setUpAllowListWithDefaultLists();
    	
    	List<String> membersId = this.service.listMembers();
    	
        // verify before removing member
        Assert.assertEquals(3, membersId.size());
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
    	
    	this.service.removeMember(memberAuthorizedAsRequesterAndTarget);
    	
    	// verify configuration is update
    	String updatedMembersListString = String.join(",", memberAuthorizedAsRequester, memberAuthorizedAsTarget);
    	String updatedRequestersList = memberAuthorizedAsRequester;
    	String updatedTargetsList = memberAuthorizedAsTarget;
    	
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY, updatedTargetsList);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY, 
    			updatedRequestersList);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY, updatedMembersListString);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).updatePropertiesFile();
    	
        List<String> updateMembersId = this.service.listMembers();

        // verify after removing member
        Assert.assertEquals(2, membersId.size());
        Assert.assertTrue(updateMembersId.contains(memberAuthorizedAsRequester));
        Assert.assertTrue(updateMembersId.contains(memberAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberAuthorizedAsTarget));
    }
    
    // test case: When invoking the removeMember method with a provider which is not on 
    // the memberList, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testRemovingNotKnownMemberMustFail() throws ConfigurationErrorException {
    	setUpAllowListWithDefaultLists();
    	
    	this.service.removeMember(notMember1);
    }
    
    private void setUpAllowList(String membersListString, String allowedRequestersListString, String allowedTargetsListString) throws ConfigurationErrorException {
        PowerMockito.mockStatic(PropertiesHolder.class);
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.doReturn(membersListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY);
        Mockito.doReturn(allowedRequestersListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY);
        Mockito.doReturn(allowedTargetsListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY);
        
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(this.propertiesHolder);
        
        this.service = new AllowList();
    }
    
    private void setUpAllowListWithDefaultLists() throws ConfigurationErrorException {
        setUpAllowList(membersListString, allowedRequestersList, allowedTargetsList);
    }
    
    private void setUpAllowListWithEmptyAllowedTargetsList() throws ConfigurationErrorException {
        // In this set up case, we want to keep the list of allowed targets empty, 
        // however we also want to assign at least one permission to all the providers. 
        // Thus, in this case, all the providers are requesters.
        setUpAllowList(membersListString, membersListString, emptyAllowedTargetsList);
    }
    
    private void setUpAllowListWithEmptyAllowedRequestersList() throws ConfigurationErrorException {
        // In this set up case, we want to keep the list of allowed requesters empty, 
        // however we also want to assign at least one permission to all the providers. 
        // Thus, in this case, all the providers are targets.
        setUpAllowList(membersListString, emptyAllowedRequestersList, membersListString);
    }
    
    private void setUpAllowListWithProviderWithNoPermission() throws ConfigurationErrorException {
        // In this set up case, we want to keep at least one provider without permission.  
        // In order to achieve this, we keep the list of requesters empty and 
        // assign the target permission to some providers, but not all.
        setUpAllowList(membersListString, emptyAllowedRequestersList, allowedTargetsList);
    }
    
    private void checkUpdatedConfiguration(String membersListString, String targetListString, String requesterListString) {
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY, membersListString);
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY, targetListString);
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY, requesterListString);
    }
}
