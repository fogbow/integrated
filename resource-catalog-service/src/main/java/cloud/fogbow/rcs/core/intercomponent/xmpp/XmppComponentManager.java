package cloud.fogbow.rcs.core.intercomponent.xmpp;

import cloud.fogbow.rcs.core.intercomponent.xmpp.handlers.RemoteGetAllServicesRequestHandler;
import org.jamppa.component.XMPPComponent;

import cloud.fogbow.rcs.core.intercomponent.xmpp.handlers.RemoteGetServiceRequestHandler;

public class XmppComponentManager extends XMPPComponent {

    private String jid;
    private String password;
    private String server;
    private int port;
    private long timeout;
    
    public String getJid() {
        return jid;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }
    
    public long getTimeout() {
        return timeout;
    }

    public static Builder builder() {
        return new XmppComponentManager.Builder();
    }
    
    public static class Builder {
        private String jid;
        private String password;
        private String server;
        private int port;
        private long timeout;
        
        public Builder jid(String jid) {
            this.jid = jid;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder server(String server) {
            this.server = server;
            return this;
        }
        
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public XmppComponentManager build() {
            return new XmppComponentManager(this);
        }
    }
    
    private XmppComponentManager(Builder builder) {
        super(builder.jid, builder.password, builder.server, builder.port, builder.timeout);
        
        // instantiate get handlers here
        addGetHandler(new RemoteGetServiceRequestHandler());
        addGetHandler(new RemoteGetAllServicesRequestHandler());
    }
    
}
