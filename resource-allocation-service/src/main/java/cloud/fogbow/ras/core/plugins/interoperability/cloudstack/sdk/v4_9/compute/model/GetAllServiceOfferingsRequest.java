package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.compute.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackRequest;

public class GetAllServiceOfferingsRequest extends CloudStackRequest {
    protected static final String LIST_SERVICE_OFFERINGS_COMMAND = "listServiceOfferings";

    protected GetAllServiceOfferingsRequest(Builder builder) throws InternalServerErrorException {
        super(builder.cloudStackUrl);
    }

    @Override
    public String getCommand() {
        return LIST_SERVICE_OFFERINGS_COMMAND;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class Builder {
        private String cloudStackUrl;

        public GetAllServiceOfferingsRequest build(String cloudStackUrl) throws InternalServerErrorException {
            this.cloudStackUrl = cloudStackUrl;
            return new GetAllServiceOfferingsRequest(this);
        }

    }
}
