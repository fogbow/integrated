package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackUrlUtil;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.CloudstackTestUtils;
import cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.securityrule.model.ListFirewallRulesRequest;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import static cloud.fogbow.common.constants.CloudStackConstants.PublicIp.IP_ADDRESS_ID_KEY_JSON;

public class ListFirewallRulesRequestTest {

    // test case: When calling the build method, it must verify if It generates the right URL.
    @Test
    public void testBuildSuccessfully() throws InternalServerErrorException {
        // set up
        URIBuilder uriBuilder = CloudStackUrlUtil.createURIBuilder(
                CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT,
                CloudStackConstants.SecurityGroup.LIST_FIREWALL_RULES_COMMAND);
        String urlBaseExpected = uriBuilder.toString();
        String ipAddressId = "ipAddressId";

        String ipAddressIdStructureUrl = String.format(
                "%s=%s", IP_ADDRESS_ID_KEY_JSON, ipAddressId);

        String[] urlStructure = new String[] {
                urlBaseExpected,
                ipAddressIdStructureUrl,
        };
        String urlExpectedStr = String.join(
                CloudstackTestUtils.AND_OPERATION_URL_PARAMETER, urlStructure);

        // exercise
        ListFirewallRulesRequest listFirewallRulesRequest = new ListFirewallRulesRequest.Builder()
                .ipAddressId(ipAddressId)
                .build(CloudstackTestUtils.CLOUDSTACK_URL_DEFAULT);
        String listFireWallRequestUrl = listFirewallRulesRequest.getUriBuilder().toString();

        // verify
        Assert.assertEquals(urlExpectedStr, listFireWallRequestUrl);
    }

    // test case: When calling the build method with a null parameter,
    // it must verify if It throws an InternalServerErrorException.
    @Test(expected = InternalServerErrorException.class)
    public void testBuildFail() throws InternalServerErrorException {
        // exercise and verify
        new ListFirewallRulesRequest.Builder().build(null);
    }

}
