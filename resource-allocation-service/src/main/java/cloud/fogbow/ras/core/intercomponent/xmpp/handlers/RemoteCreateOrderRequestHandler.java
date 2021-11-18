package cloud.fogbow.ras.core.intercomponent.xmpp.handlers;

import cloud.fogbow.common.util.IntercomponentUtil;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.intercomponent.RemoteFacade;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.intercomponent.xmpp.XmppExceptionToErrorConditionTranslator;
import cloud.fogbow.ras.core.models.orders.Order;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

public class RemoteCreateOrderRequestHandler extends AbstractQueryHandler {
    private static final Logger LOGGER = Logger.getLogger(RemoteCreateOrderRequestHandler.class);

    private static final String REMOTE_CREATE_ORDER = RemoteMethod.REMOTE_CREATE_ORDER.toString();

    public RemoteCreateOrderRequestHandler() {
        super(REMOTE_CREATE_ORDER);
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.debug(String.format(Messages.Log.RECEIVING_REMOTE_REQUEST_S, iq.getID()));
        String orderJsonStr = unmarshalOrder(iq);
        String className = unmarshalClassName(iq);

        IQ response = IQ.createResultIQ(iq);
        Gson gson = new Gson();
        Order order = null;
        try {
            order = (Order) gson.fromJson(orderJsonStr, Class.forName(className));
            String senderId = IntercomponentUtil.getSender(iq.getFrom().toBareJID(), SystemConstants.XMPP_SERVER_NAME_PREFIX);
            RemoteFacade.getInstance().activateOrder(senderId, order);
        } catch (Throwable e) {
            XmppExceptionToErrorConditionTranslator.updateErrorCondition(response, e);
        }
        return response;
    }

    private String unmarshalOrder(IQ iq) {
        Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
        Element orderElement = queryElement.element(IqElement.ORDER.toString());
        String orderJsonStr = orderElement.getText();
        return orderJsonStr;
    }

    private String unmarshalClassName(IQ iq) {
        Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
        Element orderClassNameElement = queryElement.element(IqElement.ORDER_CLASS_NAME.toString());
        String className = orderClassNameElement.getText();
        return className;
    }
}
