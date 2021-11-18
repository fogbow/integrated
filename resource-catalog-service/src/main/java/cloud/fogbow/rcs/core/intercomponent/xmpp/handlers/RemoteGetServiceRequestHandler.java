package cloud.fogbow.rcs.core.intercomponent.xmpp.handlers;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jamppa.component.handler.AbstractQueryHandler;
import org.xmpp.packet.IQ;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.models.ServiceType;

public class RemoteGetServiceRequestHandler extends AbstractQueryHandler {

    private static final Logger LOGGER = Logger.getLogger(RemoteGetServiceRequestHandler.class);
    private static final String REMOTE_GET_SERVICE = RemoteMethod.REMOTE_GET_SERVICE.toString();
    
    public RemoteGetServiceRequestHandler() {
        super(REMOTE_GET_SERVICE);
    }

    @Override
    public IQ handle(IQ iq) {
        LOGGER.info(String.format(Messages.Info.RECEIVING_REMOTE_REQUEST_FROM_S, iq.getFrom()));
        IQ iqResponse = getResultiIQ(iq);
        
        String member = unmarshal(iq, IqElement.MEMBER);
        String service = unmarshal(iq, IqElement.SERVICE).toUpperCase();        
        HttpResponse httpResponse = RemoteFacade.getInstance().requestService(member, ServiceType.valueOf(service));
        
        marshal(iqResponse, httpResponse.getContent());
        return iqResponse;
    }

    @VisibleForTesting
    void marshal(IQ iq, String content) {
        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString());
        Element contentElement = queryElement.addElement(IqElement.CONTENT.toString());
        contentElement.setText(content);
    }

    @VisibleForTesting
    String unmarshal(IQ iq, IqElement iqElement) {
        Element queryElement = iq.getElement().element(IqElement.QUERY.toString());
        Element contentElement = queryElement.element(iqElement.toString());
        return contentElement.getText();
    }
    
    @VisibleForTesting
    IQ getResultiIQ(IQ iq) {
        return IQ.createResultIQ(iq);
    }

}
