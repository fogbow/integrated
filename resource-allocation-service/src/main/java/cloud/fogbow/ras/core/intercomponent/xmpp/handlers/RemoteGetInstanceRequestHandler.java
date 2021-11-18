package cloud.fogbow.ras.core.intercomponent.xmpp.handlers;

import cloud.fogbow.common.models.SystemUser;
import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.intercomponent.RemoteFacade;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.api.http.response.Instance;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class RemoteGetInstanceRequestHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteGetInstanceRequestHandler.class);

    private static final String REMOTE_GET_INSTANCE = RemoteMethod.REMOTE_GET_INSTANCE.toString();

    public RemoteGetInstanceRequestHandler() {
        super(REMOTE_GET_INSTANCE);
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));
        String orderId = unmarshalOrderId(iq);
        ResourceType resourceType = unmarshalResourceType(iq);
        SystemUser systemUser = unmarshalFederationUser(iq);

        IQ response = IQ.createResultIQ(iq);
        try {
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            Instance instance = RemoteFacade.getInstance().getResourceInstance(senderId, orderId, systemUser, resourceType);
            //on success, update response with instance data
            updateResponse(response, instance);
        } catch (Exception e) {
            //on error, update response with exception data
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }
        return response;
    }

    private void updateResponse(IQ response, Instance instance) {
        Element queryElement =
                response.getElement().addElement(IqElement.QUERY.toString(), REMOTE_GET_INSTANCE);

        Element instanceElement = queryElement.addElement(IqElement.INSTANCE.toString());

        Element instanceClassNameElement =
                queryElement.addElement(IqElement.INSTANCE_CLASS_NAME.toString());

        instanceClassNameElement.setText(instance.getClass().getName());

        instanceElement.setText(new Gson().toJson(instance));
    }

    private SystemUser unmarshalFederationUser(IQ iq) {
        Element systemUserElement = iq.getElement().element(IqElement.SYSTEM_USER.toString());
        SystemUser systemUser = new Gson().fromJson(systemUserElement.getText(), SystemUser.class);
        return systemUser;
    }

    private ResourceType unmarshalResourceType(IQ iq) {
        Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
        Element orderTypeElementRequest = queryElement.element(IqElement.INSTANCE_TYPE.toString());

        ResourceType resourceType =
                new Gson().fromJson(orderTypeElementRequest.getText(), ResourceType.class);

        return resourceType;
    }

    private String unmarshalOrderId(IQ iq) {
        Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
        Element orderIdElement = queryElement.element(IqElement.ORDER_ID.toString());
        String orderId = orderIdElement.getText();
        return orderId;
    }
}
