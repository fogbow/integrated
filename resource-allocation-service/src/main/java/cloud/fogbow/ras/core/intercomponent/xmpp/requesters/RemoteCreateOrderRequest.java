package cloud.fogbow.ras.core.intercomponent.xmpp.requesters;

import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.constants.SystemConstants;
import cloud.fogbow.ras.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.ras.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.ras.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.ras.core.intercomponent.xmpp.XmppErrorConditionToExceptionTranslator;
import cloud.fogbow.ras.core.models.orders.Order;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

public class RemoteCreateOrderRequest implements RemoteRequest<Void> {
    private static final Logger LOGGER = Logger.getLogger(RemoteCreateOrderRequest.class);

    private Order order;

    public RemoteCreateOrderRequest(Order order) {
        this.order = order;
    }

    @Override
    public Void send() throws Exception {
        IQ iq = RemoteCreateOrderRequest.marshal(this.order);
        LOGGER.debug(String.format(Messages.Log.SENDING_MSG_S, iq.getID()));
        IQ response = (IQ) PacketSenderHolder.getPacketSender().syncSendPacket(iq);

        XmppErrorConditionToExceptionTranslator.handleError(response, this.order.getProvider());
        LOGGER.debug(Messages.Log.SUCCESS);
        return null;
    }

    public static IQ marshal(Order order) {
        IQ iq = new IQ(IQ.Type.set);
        iq.setTo(SystemConstants.JID_SERVICE_NAME + SystemConstants.JID_CONNECTOR + SystemConstants.XMPP_SERVER_NAME_PREFIX + order.getProvider());
        iq.setID(order.getId());

        //marshalling the order parcel of the IQ. It seems ok to not have another method to do so
        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_CREATE_ORDER.toString());

        Element orderElement = queryElement.addElement(IqElement.ORDER.toString());

        Element orderClassNameElement =
                queryElement.addElement(IqElement.ORDER_CLASS_NAME.toString());
        orderClassNameElement.setText(order.getClass().getName());

        String orderJson = new Gson().toJson(order);
        orderElement.setText(orderJson);

        return iq;
    }
}
