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
public class BlockListTest {

    private MembershipService service;

    private String memberNotAuthorizedAsRequesterAndTarget = "requesterAndTarget";
    private String memberNotAuthorizedAsRequester = "requester";
    private String memberNotAuthorizedAsTarget = "target";
    private String notMember1 = "notMember1";
    private String newTargetMember = "newTargetMember";
    private String newRequesterMember = "newRequesterMember";
    private String newTargetAndRequesterMember = "newTargetAndRequesterMember";
    
    
    private List<String> membersList = Arrays.asList(memberNotAuthorizedAsRequesterAndTarget, 
            memberNotAuthorizedAsRequester, memberNotAuthorizedAsTarget);
    private List<String> notAllowedRequesters = Arrays.asList(memberNotAuthorizedAsRequesterAndTarget, 
                                        memberNotAuthorizedAsRequester);
    private List<String> notAllowedTargets = Arrays.asList(memberNotAuthorizedAsRequesterAndTarget, 
                                        memberNotAuthorizedAsTarget);
    
    private String membersListString = String.join(",", membersList);
    private String notAllowedRequestersString = String.join(",", notAllowedRequesters);
    private String notAllowedTargetsString = String.join(",", notAllowedTargets);
    
    private String emptyNotAllowedTargetsList = "";
    private String emptyNotAllowedRequestersList = "";

    private PropertiesHolder propertiesHolder;
    
    // test case: When invoking the listMembers method from an instance created with
    // the MembershipService class constructor with a valid parameter, it must list
    // the configured membership in the file passed by parameter.
    @Test
    public void testListMembers() throws Exception {
        setUpBlockListWithDefaultLists();
        
        List<String> membersId = this.service.listMembers();

        // verify
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsTarget));
    }
    
    // test case: When invoking the isMember method, it must return whether or 
    // not the provider passed as argument is member, based on the configuration file.
    @Test
    public void testIsMember() throws ConfigurationErrorException {
        setUpBlockListWithDefaultLists();
        
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isMember(notMember1));
        Assert.assertFalse(this.service.isMember(""));
    }
    
    // test case: When invoking the isTargetAuthorized method, it must return 
    // whether or not local users are allowed to perform operations in the 
    // remote provider passed as argument. In the case of BlockList implementation,
    // it must return whether or not the provider is in the "not-allowed" list.
    @Test
    public void testIsTargetAuthorized() throws ConfigurationErrorException {
        setUpBlockListWithDefaultLists();
        
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isTargetAuthorized(notMember1));
        Assert.assertFalse(this.service.isTargetAuthorized(""));
    }
    
    // test case: When invoking the isRequesterAuthorized method, it must return
    // whether or not the remote provider passed as argument is allowed to 
    // perform local operations. In the case of BlockList implementation,
    // it must return whether or not the provider is in the "not-allowed" list.
    @Test
    public void testIsRequesterAuthorized() throws ConfigurationErrorException {
        setUpBlockListWithDefaultLists();
        
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(notMember1));
        Assert.assertFalse(this.service.isRequesterAuthorized(""));
    }
    
    // test case: When invoking the isTargetAuthorized method on a BlockList instance with empty
    // not allowed target lists, it must return true for all known members and false to unknown providers.
    @Test
    public void testIsTargetAuthorizedEmptyNotAllowedTargetsList() throws ConfigurationErrorException {
        setUpBlockListWithEmptyNotAllowedTargetsList();
        
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isTargetAuthorized(notMember1));
        Assert.assertFalse(this.service.isTargetAuthorized(""));
    }
    
    // test case: When invoking the isRequesterAuthorized method on a BlockList instance with empty
    // not allowed requester lists, it must return true for all known members and false to unknown providers.
    @Test
    public void testIsTargetAuthorizedEmptyNotAllowedRequestersList() throws ConfigurationErrorException {
        setUpBlockListWithEmptyNotAllowedRequestersList();
        
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(notMember1));
        Assert.assertFalse(this.service.isRequesterAuthorized(""));
    }
    
    // test case: When invoking the addMember method, it must add the given provider correctly, 
    // so that the change is reflected on the return value of the method listMembers. Also, 
    // the configuration file must be updated.
    @Test
    public void testAddMember() throws Exception {
    	setUpBlockListWithDefaultLists();
        List<String> expectedMembersList = new ArrayList<String>(membersList);
        List<String> expectedTargetsList = new ArrayList<String>(notAllowedTargets);
        List<String> expectedRequestersList = new ArrayList<String>(notAllowedRequesters);  
    	
        List<String> membersId = this.service.listMembers();
        
        // verify before adding member
        Assert.assertEquals(3, membersId.size());
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsTarget));
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

        List<String> updateMembersId = this.service.listMembers();

        // verify after adding member
        Assert.assertEquals(6, updateMembersId.size());
        Assert.assertTrue(updateMembersId.contains(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(updateMembersId.contains(memberNotAuthorizedAsRequester));
        Assert.assertTrue(updateMembersId.contains(memberNotAuthorizedAsTarget));
        Assert.assertTrue(updateMembersId.contains(newTargetMember));
        Assert.assertTrue(updateMembersId.contains(newRequesterMember));
        Assert.assertTrue(updateMembersId.contains(newTargetAndRequesterMember));
        
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isMember(memberNotAuthorizedAsTarget));
        Assert.assertTrue(this.service.isMember(newTargetMember));
        Assert.assertTrue(this.service.isMember(newRequesterMember));
        Assert.assertTrue(this.service.isMember(newTargetAndRequesterMember));
        
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isTargetAuthorized(newTargetMember));
        Assert.assertTrue(this.service.isTargetAuthorized(newRequesterMember));
        Assert.assertFalse(this.service.isTargetAuthorized(newTargetAndRequesterMember));
        
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(newTargetMember));
        Assert.assertFalse(this.service.isRequesterAuthorized(newRequesterMember));
        Assert.assertFalse(this.service.isRequesterAuthorized(newTargetAndRequesterMember));
    }
    
    // test case: When invoking the addMember method with a provider which is already a member, 
    // it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testAddingAlreadyKnownProviderMustFail() throws ConfigurationErrorException {
        setUpBlockListWithDefaultLists();
        
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
        setUpBlockListWithDefaultLists();
        
        this.service.addMember(new ProviderPermission(newTargetMember, false, false));
    }
    
    // test case: When creating a BlockList instance using a configuration file 
    // containing a provider with no permissions, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testConstructorMustCheckIfAllMembersHavePermissions( ) throws ConfigurationErrorException {
        setUpBlockListWithProviderWithNoPermission();
    }
    
    // test case: When invoking the updateMember method, it must update the given provider 
    // permissions correctly, so that the change is reflected on the return value of the methods
    // isTargetAuthorized and isRequesterAuthorized. Also, the configuration file must be updated.
    @Test
    public void testUpdateMember() throws Exception {
        setUpBlockListWithDefaultLists();
        List<String> expectedMembersList = new ArrayList<String>(membersList);
        List<String> expectedTargetsList = new ArrayList<String>(notAllowedTargets);
        List<String> expectedRequestersList = new ArrayList<String>(notAllowedRequesters);  
        
        // verify before adding member
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
        
        // target only member is now a requester only member
        this.service.updateMember(new ProviderPermission(memberNotAuthorizedAsTarget, false, true));
        // requester only member is now a target only member
        this.service.updateMember(new ProviderPermission(memberNotAuthorizedAsRequester, true, false));
        
        expectedTargetsList.remove(memberNotAuthorizedAsTarget);
        expectedTargetsList.add(memberNotAuthorizedAsRequester);
        expectedRequestersList.remove(memberNotAuthorizedAsRequester);
        expectedRequestersList.add(memberNotAuthorizedAsTarget);
        
        // verify configuration is updated
        checkUpdatedConfiguration(String.join(",", expectedMembersList), String.join(",", expectedTargetsList), 
                String.join(",", expectedRequestersList));
        Mockito.verify(propertiesHolder, Mockito.times(2)).updatePropertiesFile();
        
        // verify after updating member
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
    }
    
    // test case: When invoking the updateMember method with a provider which is not on 
    // the memberList, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testUpdatingNotKnownMemberMustFail() throws ConfigurationErrorException {
        setUpBlockListWithDefaultLists();
        
        this.service.updateMember(new ProviderPermission(notMember1, false, true));
    }
    
    // test case: When invoking the removeMember method, it must remove the given provider correctly
    // from all internal lists, so that the change is reflected on the return value of the methods listMembers, 
    // isTargetAuthorized and isRequesterAuthorized. Also, the configuration file must be updated.
    @Test
    public void testRemoveMember() throws Exception {
    	setUpBlockListWithDefaultLists();
    	
        String updatedMembersListString = String.join(",", memberNotAuthorizedAsRequester, memberNotAuthorizedAsTarget);
        String updatedRequestersList = memberNotAuthorizedAsRequester;
        String updatedTargetsList = memberNotAuthorizedAsTarget;
    	
    	List<String> membersId = this.service.listMembers();
    	
        // verify before removing member
        Assert.assertEquals(3, membersId.size());
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsRequester));
        Assert.assertTrue(membersId.contains(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
    	
    	this.service.removeMember(memberNotAuthorizedAsRequesterAndTarget);
    	
    	// verify configuration is update
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY, updatedTargetsList);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY, 
    			updatedRequestersList);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).setProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY, updatedMembersListString);
    	Mockito.verify(propertiesHolder, Mockito.times(1)).updatePropertiesFile();
    	
        List<String> updateMembersId = this.service.listMembers();

        // verify after removing member
        Assert.assertEquals(2, membersId.size());
        Assert.assertTrue(updateMembersId.contains(memberNotAuthorizedAsRequester));
        Assert.assertTrue(updateMembersId.contains(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsTarget));
        Assert.assertFalse(this.service.isTargetAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertTrue(this.service.isTargetAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequesterAndTarget));
        Assert.assertFalse(this.service.isRequesterAuthorized(memberNotAuthorizedAsRequester));
        Assert.assertTrue(this.service.isRequesterAuthorized(memberNotAuthorizedAsTarget));
    }
    
    // test case: When invoking the removeMember method with a provider which is not on 
    // the memberList, it must throw a ConfigurationErrorException.
    @Test(expected = ConfigurationErrorException.class)
    public void testRemovingNotKnownMemberMustFail() throws ConfigurationErrorException {
    	setUpBlockListWithDefaultLists();
    	
    	this.service.removeMember(notMember1);
    }
    
    private void setUpBlockList(String membersListString, String notAllowedRequestersListString, String notAllowedTargetsListString) throws ConfigurationErrorException {
        PowerMockito.mockStatic(PropertiesHolder.class);
        this.propertiesHolder = Mockito.mock(PropertiesHolder.class);
        Mockito.doReturn(membersListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY);
        Mockito.doReturn(notAllowedRequestersListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY);
        Mockito.doReturn(notAllowedTargetsListString).when(this.propertiesHolder).getProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY);
        
        BDDMockito.given(PropertiesHolder.getInstance()).willReturn(this.propertiesHolder);
        
        this.service = new BlockList();
    }
    
    private void setUpBlockListWithDefaultLists() throws ConfigurationErrorException {
        setUpBlockList(membersListString, notAllowedRequestersString, notAllowedTargetsString);
    }
    
    private void setUpBlockListWithEmptyNotAllowedTargetsList() throws ConfigurationErrorException {
        // In this set up case, we want to keep the list of forbidden targets empty, 
        // however we also want to assign at least one permission to all the providers. 
        // Thus, in this case, all the providers are requesters.
        setUpBlockList(membersListString, membersListString, emptyNotAllowedTargetsList);
    }
    
    private void setUpBlockListWithEmptyNotAllowedRequestersList() throws ConfigurationErrorException {
        // In this set up case, we want to keep the list of forbidden requesters empty, 
        // however we also want to assign at least one permission to all the providers. 
        // Thus, in this case, all the providers are targets.
        setUpBlockList(membersListString, emptyNotAllowedRequestersList, membersListString);
    }
    
    private void setUpBlockListWithProviderWithNoPermission() throws ConfigurationErrorException {
        // In this set up case, we want to keep at least one provider without permission.  
        // In order to achieve this, we keep the list of requesters empty and 
        // assign the target permission to some providers, but not all.
        setUpBlockList(membersListString, emptyNotAllowedRequestersList, notAllowedTargetsString);
    }
    
    private void checkUpdatedConfiguration(String membersListString, String targetListString, String requesterListString) {
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.MEMBERS_LIST_KEY, membersListString);
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.TARGET_MEMBERS_LIST_KEY, targetListString);
        Mockito.verify(propertiesHolder, Mockito.atLeastOnce()).setProperty(ConfigurationPropertyKeys.REQUESTER_MEMBERS_LIST_KEY, requesterListString);
    }
}
