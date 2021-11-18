package cloud.fogbow.ras.core.plugins.interoperability.cloudstack.sdk.v4_9.attachment.model;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackRequest;

import static cloud.fogbow.common.constants.CloudStackConstants.Attachment.*;

public class AttachVolumeRequest extends CloudStackRequest {
    
    protected AttachVolumeRequest(Builder builder) throws InternalServerErrorException {
        super(builder.cloudStackUrl);
        addParameter(ID_KEY_JSON, builder.id);
        addParameter(VIRTUAL_MACHINE_ID_KEY_JSON, builder.virtualMachineId);
    }
    
    @Override
    public String getCommand() {
        return ATTACH_VOLUME_COMMAND;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
    public static class Builder {
        private String cloudStackUrl;
        private String id;
        private String virtualMachineId;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder virtualMachineId(String virtualMachineId) {
            this.virtualMachineId = virtualMachineId;
            return this;
        }

        public AttachVolumeRequest build(String cloudStackUrl) throws InternalServerErrorException {
            this.cloudStackUrl = cloudStackUrl;
            return new AttachVolumeRequest(this);
        }
        
    }
    
}