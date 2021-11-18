package cloud.fogbow.rcs.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import cloud.fogbow.common.util.GsonHolder;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.rcs.core.models.ServiceType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteGetAllServicesRequest {

    private static final Logger LOGGER = Logger.getLogger(RemoteGetAllServicesRequest.class);

    private String member;

    public static RemoteGetAllServicesRequest.Builder builder() {
        return new RemoteGetAllServicesRequest.Builder();
    }

    public static class Builder {
        private String member;

        public RemoteGetAllServicesRequest.Builder member(String member) {
            this.member = member;
            return this;
        }

        public RemoteGetAllServicesRequest build() {
            return new RemoteGetAllServicesRequest(this);
        }
    }

    public RemoteGetAllServicesRequest(RemoteGetAllServicesRequest.Builder builder) {
        this.member = builder.member;
    }

    public List<ServiceType> send() throws FogbowException {
        String senderId = SystemConstants.XMPP_SERVER_NAME_PREFIX.concat(this.member);
        IQ iq = marshal(senderId);

        LOGGER.info(String.format(Messages.Info.SENDING_REMOTE_REQUEST_FROM_MEMBER_S, this.member));
        IQ response = (IQ) PacketSenderHolder.getPacketSender().syncSendPacket(iq);

        XmppErrorConditionToExceptionTranslator.handleError(response, this.member);
        LOGGER.info(Messages.Info.SEND_SUCCESSFULLY);

        return unmarshal(response);
    }

    @VisibleForTesting
    IQ marshal(String senderId) {
        IQ iq = new IQ(IQ.Type.get);
        iq.setTo(senderId);

        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_ALL_SERVICES.toString());

        Element memberElement = queryElement.addElement(IqElement.MEMBER.toString());
        memberElement.setText(this.member);

        return iq;
    }

    @VisibleForTesting
    List<ServiceType> unmarshal(IQ response) throws UnexpectedException{
        Element queryElement = response.getElement().element(IqElement.QUERY.toString());
        Element contentElement = queryElement.element(IqElement.CONTENT.toString());

        try {
            Type type = new TypeToken<List<ServiceType>>() {}.getType();
            return (List<ServiceType>) GsonHolder.getInstance().
                    fromJson(contentElement.getText(), type);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage());
        }
    }
}
