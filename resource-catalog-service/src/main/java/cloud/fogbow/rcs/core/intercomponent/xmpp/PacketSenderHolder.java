package cloud.fogbow.rcs.core.intercomponent.xmpp;

import org.apache.log4j.Logger;
import org.jamppa.component.PacketSender;
import org.jamppa.component.XMPPComponent;
import org.xmpp.component.ComponentException;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.rcs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.rcs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.PropertiesHolder;

public class PacketSenderHolder {

    private final static Logger LOGGER = Logger.getLogger(PacketSenderHolder.class);

    protected static PacketSender packetSender = null;

    public static void init() {
        if (packetSender == null) {
            PacketSenderHolder.packetSender = connectPacketSender();
        }
    }

    @VisibleForTesting
    static XMPPComponent connectPacketSender() {
        XMPPComponent component = buildXmppComponentManager();
        try {
            component.connect();
        } catch (ComponentException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return component;
    }

    @VisibleForTesting
    static XMPPComponent buildXmppComponentManager() {
        String jid = getXmppProviderId();
        String password = getXmppPassword();
        String server = getXmppServerIp();
        int port = getXmppServerPort();
        long timeout = getXmppTimeout();
        
        XMPPComponent component = XmppComponentManager.builder()
                .jid(jid)
                .password(password)
                .server(server)
                .port(port)
                .timeout(timeout)
                .build();
        
        return component;
    }

    @VisibleForTesting
    static long getXmppTimeout() {
        return Long.parseLong(PropertiesHolder.getInstance()
                .getProperty(ConfigurationPropertyKeys.XMPP_TIMEOUT_KEY, ConfigurationPropertyDefaults.XMPP_TIMEOUT));
    }

    @VisibleForTesting
    static int getXmppServerPort() {
        return Integer.parseInt(PropertiesHolder.getInstance()
                .getProperty(ConfigurationPropertyKeys.XMPP_C2C_PORT_KEY, ConfigurationPropertyDefaults.XMPP_CSC_PORT));
    }

    @VisibleForTesting
    static String getXmppServerIp() {
        String xmppServerIp = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_SERVER_IP_KEY);
        if (xmppServerIp == null || xmppServerIp.isEmpty()) {
            LOGGER.info(Messages.Info.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
        return xmppServerIp;
    }

    @VisibleForTesting
    static String getXmppPassword() {
        String xmppPassword = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.XMPP_PASSWORD_KEY);
        if (xmppPassword == null || xmppPassword.isEmpty()) {
            LOGGER.info(Messages.Info.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
        return xmppPassword;
    }

    @VisibleForTesting
    static String getXmppProviderId() {
        String providerId = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.LOCAL_MEMBER_ID_KEY);
        if (providerId == null || providerId.isEmpty()) {
            LOGGER.info(Messages.Info.NO_REMOTE_COMMUNICATION_CONFIGURED);
        }
        return SystemConstants.XMPP_SERVER_NAME_PREFIX.concat(providerId);
    }
    
    public static synchronized PacketSender getPacketSender() {
        init();
        return packetSender;
    }
    
    // Used in tests only
    public static void setPacketSender(PacketSender thePacketSender) {
        packetSender = thePacketSender;
    }
}
