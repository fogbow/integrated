package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.publicip.models.GetSecurityGroupsResponse;
import cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.publicip.models.GetSecurityGroupsResponse.SecurityGroup;
import org.apache.log4j.Logger;
import org.json.JSONException;

public class OpenStackCloudUtils {

    private static final Logger LOGGER = Logger.getLogger(OpenStackCloudUtils.class);

    public static String getSecurityGroupIdFromGetResponse(String json) throws InternalServerErrorException {
        try {
            SecurityGroup securityGroup = GetSecurityGroupsResponse.fromJson(json).getSecurityGroups().iterator().next();
            return securityGroup.getId();
        } catch (JSONException e) {
            LOGGER.error(String.format(Messages.Log.UNABLE_TO_RETRIEVE_NETWORK_ID_S, json), e);
            throw new InternalServerErrorException(String.format(Messages.Exception.UNABLE_TO_RETRIEVE_NETWORK_ID_S, json));
        }
    }

}
