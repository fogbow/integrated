package cloud.fogbow.rcs.core.intercomponent.xmpp.requesters;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.rcs.core.models.ServiceType;

public class RemoteGetServiceRequest {
    
    private static final Logger LOGGER = Logger.getLogger(RemoteGetServiceRequest.class);
    private static final String FORMAT_MEMBER_SERVICE_KEY = "%s-%s";
    
    private String member;
    private ServiceType serviceType;
    
    public String member() {
        return member;
    }
    
    public ServiceType serviceType() {
        return serviceType;
    }
    
    public static Builder builder() {
        return new RemoteGetServiceRequest.Builder();
    }
    
    public static class Builder {
        private String member;
        private ServiceType serviceType;
        
        public Builder member(String member) {
            this.member = member;
            return this;
        }
        
        public Builder serviceType(ServiceType serviceType) {
            this.serviceType = serviceType;
            return this;
        }
        
        public RemoteGetServiceRequest build() {
            return new RemoteGetServiceRequest(this);
        }
    }
    
    public RemoteGetServiceRequest(Builder builder) {
        this.member = builder.member;
        this.serviceType = builder.serviceType;
    }
    
    public void send() throws FogbowException {
        String senderId = SystemConstants.XMPP_SERVER_NAME_PREFIX.concat(this.member);
        IQ iq = marshal(senderId);
        
        LOGGER.info(String.format(Messages.Info.SENDING_REMOTE_REQUEST_FROM_MEMBER_S, this.member));
        IQ response = (IQ) PacketSenderHolder.getPacketSender().syncSendPacket(iq);
        
        XmppErrorConditionToExceptionTranslator.handleError(response, this.member);
        LOGGER.info(Messages.Info.SEND_SUCCESSFULLY);
        
        String key = String.format(FORMAT_MEMBER_SERVICE_KEY, this.member, this.serviceType.getName());
        String content = unmarshal(response);
        RemoteFacade.getInstance().cacheSave(key, content);
    }

    @VisibleForTesting
    IQ marshal(String senderId) {
        IQ iq = new IQ(IQ.Type.get);
        iq.setTo(senderId);

        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_SERVICE.toString());
        
        Element memberElement = queryElement.addElement(IqElement.MEMBER.toString());
        memberElement.setText(this.member);
        
        Element serviceElement = queryElement.addElement(IqElement.SERVICE.toString());
        serviceElement.setText(this.serviceType.getName());

        return iq;
    }

    @VisibleForTesting
    String unmarshal(IQ response) {
        Element queryElement = response.getElement().element(IqElement.QUERY.toString());
        Element contentElement = queryElement.element(IqElement.CONTENT.toString());
        return contentElement.getText();
    }
    
}
