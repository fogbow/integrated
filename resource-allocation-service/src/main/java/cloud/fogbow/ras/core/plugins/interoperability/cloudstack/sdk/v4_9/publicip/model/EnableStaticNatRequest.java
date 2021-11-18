package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.publicip.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackRequest;

import static cloud.fogbow.common.constants.CloudStackConstants.PublicIp.*;

/**
 * Documentation : https://cloudstack.apache.org/api/apidocs-4.9/apis/enableStaticNat.html
 * <p>
 * Request Example: {url_cloudstack}?command=enableStaticNat&virtualmachineid={virtualmachineid} /
 * &ipaddressid={ipaddressid}&apikey={apiKey}&secret_key={secretKey}
 */
public class EnableStaticNatRequest extends CloudStackRequest {

    protected EnableStaticNatRequest(Builder builder) throws InternalServerErrorException {
        super(builder.cloudStackUrl);

        addParameter(VM_ID_KEY_JSON, builder.virtualMachineId);
        addParameter(IP_ADDRESS_ID_KEY_JSON, builder.ipAddressId);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getCommand() {
        return ENABLE_STATIC_NAT_COMMAND;
    }

    public static class Builder {
        private String cloudStackUrl;
        private String virtualMachineId;
        private String ipAddressId;

        public Builder virtualMachineId(String virtualMachineId) {
            this.virtualMachineId = virtualMachineId;
            return this;
        }

        public Builder ipAddressId(String ipAddressId) {
            this.ipAddressId = ipAddressId;
            return this;
        }

        public EnableStaticNatRequest build(String cloudStackUrl) throws InternalServerErrorException {
            this.cloudStackUrl = cloudStackUrl;
            return new EnableStaticNatRequest(this);
        }
    }

}
