package cloud.fogbow.rcs.core.intercomponent.xmpp.handlers;

import cloud.fogbow.common.util.GsonHolder;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.models.ServiceType;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import java.util.List;

public class RemoteGetAllServicesRequestHandler extends AbstractQueryHandler {

    private static final Logger LOGGER = Logger.getLogger(RemoteGetServiceRequestHandler.class);
    private static final String REMOTE_GET_SERVICES = RemoteMethod.REMOTE_GET_ALL_SERVICES.toString();

    public RemoteGetAllServicesRequestHandler() {
        super(REMOTE_GET_SERVICES);
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.info(String.format(Messages.Info.RECEIVING_REMOTE_REQUEST_FROM_S, iq.getFrom()));
        IQ response = IQ.createResultIQ(iq);

        List<ServiceType> services = RemoteFacade.getInstance().getServices();

        marshal(response, services);
        return response;
    }

    @VisibleForTesting
    void marshal(IQ response, List<ServiceType> content) {
        Element queryElement = response.getElement().addElement(IqElement.QUERY.toString());
        Element contentElement = queryElement.addElement(IqElement.CONTENT.toString());
        contentElement.setText(GsonHolder.getInstance().toJson(content));
    }
}
