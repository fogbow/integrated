package cloud.fogbow.ras.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.ras.api.http.response.quotas.Quota;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

public class RemoteGetUserQuotaRequest implements RemoteRequest<Quota> {
    private static final Logger LOGGER = Logger.getLogger(RemoteGetUserQuotaRequest.class);

    private String provider;
    private String cloudName;
    private SystemUser systemUser;

    public RemoteGetUserQuotaRequest(String provider, String cloudName, SystemUser systemUser) {
        this.provider = provider;
        this.cloudName = cloudName;
        this.systemUser = systemUser;
    }

    @Override
    public Quota send() throws Exception {
        IQ iq = marshal(this.provider, this.cloudName, this.systemUser);
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) PacketSenderHolder.getPacketSender().syncSendPacket(iq);

        XmppErrorConditionToExceptionTranslator.handleError(response, this.provider);
        Quota quota = unmarshalUserQuota(response);
        LOGGER.debug(Messages.Log.SUCCESS);
        return quota;
    }

    public static IQ marshal(String provider, String cloudName, SystemUser systemUser) {
        IQ iq = new IQ(IQ.Type.get);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + provider);

        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_USER_QUOTA.toString());

        Element cloudNameElement = queryElement.addElement(IqElement.CLOUD_NAME.toString());
        cloudNameElement.setText(cloudName);

        Element userElement = queryElement.addElement(IqElement.SYSTEM_USER.toString());
        userElement.setText(new Gson().toJson(systemUser));

        return iq;
    }

    private Quota unmarshalUserQuota(IQ response) throws InternalServerErrorException {
        Element queryElement = response.getElement().element(IqElement.QUERY.toString());
        String quotaStr = queryElement.element(IqElement.USER_QUOTA.toString()).getText();

        String instanceClassName = queryElement.element(IqElement.USER_QUOTA_CLASS_NAME.toString()).getText();

        Quota quota = null;
        try {
            quota = (Quota) new Gson().fromJson(quotaStr, Class.forName(instanceClassName));
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        return quota;
    }
}