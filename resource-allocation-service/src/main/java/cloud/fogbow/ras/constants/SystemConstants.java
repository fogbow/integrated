package cloud.fogbow.ras.constants;

public class SystemConstants {
    public static final String SERVICE_BASE_ENDPOINT = "ras/";
    public static final String API_VERSION_NUMBER = "3.0.0";

    // CONFIGURATION FILES NAMES
    public static final String RAS_CONF_FILE_NAME = "ras.conf";

    // INTEROPERABILITY PLUGINS CONF FILES DIRECTORY AND NAMES
    public static final String CLOUDS_CONFIGURATION_DIRECTORY_NAME = "clouds";
    public static final String INTEROPERABILITY_CONF_FILE_NAME = "plugins.conf";
    public static final String CLOUD_SPECIFICITY_CONF_FILE_NAME = "cloud.conf";
    public static final String MAPPER_CONF_FILE_NAME = "mapper.conf";

    // DEFAULT INSTANCE NAME PREFIX AND OTHER DEFAULT CONSTANTS
    public static final String FOGBOW_INSTANCE_NAME_PREFIX = "fogbow-";
    public static final String PN_SECURITY_GROUP_PREFIX = FOGBOW_INSTANCE_NAME_PREFIX + "sg-pn-";
    public static final String PIP_SECURITY_GROUP_PREFIX = FOGBOW_INSTANCE_NAME_PREFIX + "sg-pip-";
    public static final String DEFAULT_NETWORK_NAME = "default";

    // SERVICE XMPP NAME
    public static final String JID_SERVICE_NAME = "";
    public static final String XMPP_SERVER_NAME_PREFIX = "ras-";
    public static final String JID_CONNECTOR = "";
    
    // AUTHORIZATION
    public static final String OPERATIONS_LIST_KEY_SUFFIX = "_operations";
    public static final String OPERATION_NAME_SEPARATOR = ",";
    public static final String ROLE_NAMES_SEPARATOR = ",";
    public static final String USER_NAME_SEPARATOR = ",";
    public static final String USER_ROLES_SEPARATOR = ",";
    
    // CONFIGURATION
    public static final String XMPP_IS_ENABLED = "true";
}
