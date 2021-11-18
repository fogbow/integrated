package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.image.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackRequest;

import static cloud.fogbow.common.constants.CloudStackConstants.Image.*;

public class GetAllImagesRequest extends CloudStackRequest {

    protected GetAllImagesRequest(Builder builder) throws InternalServerErrorException {
        super(builder.cloudStackUrl);
        addParameter(TEMPLATE_FILTER_KEY_JSON, EXECUTABLE_TEMPLATES_VALUE);
        addParameter(ID_KEY_JSON, builder.id);
    }

    @Override
    public String getCommand() {
        return LIST_TEMPLATES_COMMAND;
    }

    public static class Builder {
        private String cloudStackUrl;
        private String id;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public GetAllImagesRequest build(String cloudStackUrl) throws InternalServerErrorException {
            this.cloudStackUrl = cloudStackUrl;
            return new GetAllImagesRequest(this);
        }
    }

}