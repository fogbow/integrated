package cloud.fogbow.common.constants;

public class CloudStackConstants {
    public static final String KEY_VALUE_SEPARATOR = ":";
    public static final String ERROR_CODE_KEY_JSON = "errorcode";
    public static final String ERROR_TEXT_KEY_JSON = "errortext";
    public static final String JOB_ID_KEY_JSON = "jobid";
    public static final String X_DESCRIPTION_KEY = "X-Description";
    
    public static class Compute {
        public static final String VIRTUAL_MACHINES_KEY_JSON = "listvirtualmachinesresponse";
        public static final String VIRTUAL_MACHINE_KEY_JSON = "virtualmachine";
        public static final String ID_KEY_JSON = "id";
        public static final String NAME_KEY_JSON = "name";
        public static final String STATE_KEY_JSON = "state";
        public static final String CPU_NUMBER_KEY_JSON = "cpunumber";
        public static final String MEMORY_KEY_JSON = "memory";
        public static final String TAGS_KEY_JSON = "tags";
        public static final String NIC_KEY_JSON = "nic";
        public static final String IP_ADDRESS_KEY_JSON = "ipaddress";
        public static final String LIST_SERVICE_OFFERINGS_KEY_JSON = "listserviceofferingsresponse";
        public static final String SERVICE_OFFERING_KEY_JSON = "serviceoffering";
        public static final String DEPLOY_VIRTUAL_MACHINE = "deployvirtualmachineresponse";
        public static final String REGISTER_KEYPAIR_KEY_JSON = "registersshkeypairresponse";
        public static final String KEYPAIR_KEY_JSON = "keypair";
        public static final String DELETE_KEYPAIR_KEY_JSON = "deletesshkeypairresponse";
        public static final String SUCCESS_KEY_JSON = "success";
        public static final String LIST_VIRTUAL_MACHINES_COMMAND = "listVirtualMachines";
    }

    public static class Volume {
        public static final String LIST_DISK_OFFERINGS_COMMAND = "listDiskOfferings";
        public static final String CREATE_VOLUME_COMMAND = "createVolume";
        public static final String DELETE_VOLUME_COMMAND = "deleteVolume";
        public static final String VOLUMES_KEY_JSON = "listvolumesresponse";
        public static final String VOLUME_KEY_JSON = "volume";
        public static final String DISK_KEY_JSON = "disksize";
        public static final String CREATE_VOLUME_KEY_JSON = "createvolumeresponse";
        public static final String CUSTOMIZED_KEY_JSON = "iscustomized";
        public static final String DELETE_VOLUME_KEY_JSON = "deletevolumeresponse";
        public static final String DISK_OFFERING_KEY_JSON = "diskoffering";
        public static final String DISK_OFFERING_ID_KEY_JSON = "diskofferingid";
        public static final String DISK_OFFERINGS_KEY_JSON = "listdiskofferingsresponse";
        public static final String DISPLAY_TEXT_KEY_JSON = "displaytext";
        public static final String ZONE_ID_KEY_JSON = "zoneid";
        public static final String TAGS_KEY_JSON = "tags";
        public static final String ID_KEY_JSON = "id";
        public static final String JOB_ID_KEY_JSON = "jobid";
        public static final String NAME_KEY_JSON = "name";
        public static final String SIZE_KEY_JSON = "size";
        public static final String STATE_KEY_JSON = "state";
        public static final String SUCCESS_KEY_JSON = "success";
    }

    public static class Network {
        public static final String NETWORKS_KEY_JSON = "listnetworksresponse";
        public static final String CREATE_NETWORK_RESPONSE_KEY_JSON = "createnetworkresponse";
        public static final String NETWORK_KEY_JSON = "network";
        public static final String ID_KEY_JSON = "id";
        public static final String SECURITY_GROUPS_ENABLED_KEY_JSON = "securitygroupsenabled";
        public static final String NETWORK_TYPE_KEY_JSON = "networktype";
        public static final String LIST_ZONES_RESPONSE_KEY_JSON = "listzonesresponse";
        public static final String TAGS_KEY_JSON = "tags";
        public static final String CREATE_NETWORK_COMMAND = "createNetwork";
        public static final String DELETE_NETWORK_COMMAND = "deleteNetwork";
        public static final String LIST_NETWORKS_COMMAND = "listNetworks";
        public static final String NAME_KEY_JSON = "name";
        public static final String NETWORK_OFFERING_ID_KEY_JSON = "networkofferingid";
        public static final String ZONE_ID_KEY_JSON = "zoneid";
        public static final String STARTING_IP_KEY_JSON = "startip";
        public static final String ENDING_IP_KEY_JSON = "endip";
        public static final String GATEWAY_KEY_JSON = "gateway";
        public static final String NETMASK_KEY_JSON = "netmask";
        public static final String DISPLAY_TEXT_KEY_JSON = "displaytext";        
    }

    public static class PublicIp {
        public static final String CREATE_FIREWALL_RULE_COMMAND = "createFirewallRule";
        public static final String DISASSOCIATE_IP_ADDRESS_COMMAND = "disassociateIpAddress";
        public static final String ASSOCIATE_IP_ADDRESS_COMMAND = "associateIpAddress";
        public static final String QUERY_ASYNC_JOB_RESULT = "queryAsyncJobResult";
        public static final String ENABLE_STATIC_NAT_COMMAND = "enableStaticNat";
        public static final String ASSOCIATE_IP_ADDRESS_RESPONSE_KEY_JSON = "associateipaddressresponse";
        public static final String QUERY_ASYNC_JOB_RESULT_KEY_JSON = "queryasyncjobresultresponse";
        public static final String VM_ID_KEY_JSON = "virtualmachineid";
        public static final String NETWORK_ID_KEY_JSON = "networkid";
        public static final String IP_ADDRESS_KEY_JSON = "ipaddress";
        public static final String IP_ADDRESS_ID_KEY_JSON = "ipaddressid";
        public static final String CIDR_LIST_KEY_JSON = "cidrlist";
        public static final String PROTOCOL_KEY_JSON = "protocol";
        public static final String STARTPORT_KEY_JSON = "startport";
        public static final String ENDPORT_KEY_JSON = "endport";
        public static final String ID_KEY_JSON = "id";
        public static final String JOB_ID_KEY_JSON = "jobid";
        public static final String JOB_RESULT_KEY_JSON = "jobresult";
        public static final String JOB_STATUS_KEY_JSON = "jobstatus";
        public static final String JOB_INSTANCE_ID_KEY_JSON = "jobinstanceid";
        public static final String SUCCESS_KEY_JSON = "success";
        public static final String DISPLAY_TEXT_KEY_JSON = "displaytext";
    }

    public static class Attachment {
        public static final String ATTACH_VOLUME_KEY_JSON = "attachvolumeresponse";
        public static final String DETACH_VOLUME_KEY_JSON = "detachvolumeresponse";
        public static final String JOB_ID_KEY_JSON = "jobid";
        public static final String QUERY_ASYNC_JOB_RESULT_KEY_JSON = "queryasyncjobresultresponse";
        public static final String JOB_STATUS_KEY_JSON = "jobstatus";
        public static final String JOB_RESULT_KEY_JSON = "jobresult";
        public static final String VOLUME_KEY_JSON = "volume";
        public static final String ID_KEY_JSON = "id";
        public static final String DEVICE_ID_KEY_JSON = "deviceid";
        public static final String VIRTUAL_MACHINE_ID_KEY_JSON = "virtualmachineid";
        public static final String STATE_KEY_JSON = "state";
        public static final String ATTACH_VOLUME_COMMAND = "attachVolume";
        public static final String DETACH_VOLUME_COMMAND = "detachVolume";
        public static final String QUERY_ASYNC_JOB_RESULT_COMMAND = "queryAsyncJobResult";

    }

    public static class Quota {
        public static final String LIST_RESOURCE_LIMITS_KEY_JSON = "listresourcelimitsresponse";
        public static final String RESOURCE_LIMIT_KEY_JSON = "resourcelimit";
        public static final String RESOURCE_TYPE_KEY_JSON = "resourcetype";
        public static final String MAX_KEY_JSON = "max";
        public static final String DOMAIN_ID_KEY_JSON = "domainid";
        public static final String LIST_RESOURCE_LIMITS_COMMAND = "listResourceLimits";
    }

    public static class Identity {
        public static final String PASSWORD_KEY_JSON = "password";
        public static final String DOMAIN_KEY_JSON = "domain";
        public static final String LOGIN_KEY_JSON = "loginresponse";
        public static final String LIST_ACCOUNTS_KEY_JSON = "listaccountsresponse";
        public static final String ACCOUNT_KEY_JSON = "account";
        public static final String USER_KEY_JSON = "user";
        public static final String USER_ID_KEY_JSON = "id";
        public static final String USERNAME_KEY_JSON = "username";
        public static final String FIRST_NAME_KEY_JSON = "firstname";
        public static final String LAST_NAME_KEY_JSON = "lastname";
        public static final String SESSION_KEY_JSON = "sessionkey";
        public static final String TIMEOUT_KEY_JSON = "timeout";
        public static final String API_KEY_JSON = "apikey";
        public static final String SECRET_KEY_JSON = "secretkey";
    }
    
    public static class SecurityGroup {
        public static final String CREATE_FIREWALL_RULE_RESPONSE = "createfirewallruleresponse";
        public static final String DELETE_FIREWALL_RULE_RESPONSE = "deletefirewallruleresponse";
        public static final String LIST_FIREWALL_RULES_COMMAND = "listFirewallRules";
        public static final String CREATE_FIREWALL_RULE_COMMAND = "createFirewallRule";
        public static final String DELETE_FIREWALL_RULE_COMMAND = "deleteFirewallRule";
        public static final String LIST_FIREWALL_RULES_KEY_JSON = "listfirewallrulesresponse";
        public static final String IP_ADDRESS_ID_KEY_JSON = "ipaddressid";
        public static final String FIREWALL_RULE_KEY_JSON = "firewallrule";
        public static final String ID_KEY_JSON = "id";
        public static final String CIDR_LIST_KEY_JSON = "cidrlist";
        public static final String START_PORT_KEY_JSON = "startport";
        public static final String END_PORT_KEY_JSON = "endport";
        public static final String PROPOCOL_KEY_JSON = "protocol";
        public static final String IP_ADDRESS_KEY_JSON = "ipaddress";
        public static final String TCP_VALUE_PROTOCOL = "tcp";
        public static final String UDP_VALUE_PROTOCOL = "udp";
        public static final String ICMP_VALUE_PROTOCOL = "icmp";
        public static final String ALL_VALUE_PROTOCOL = "all";
    }
    
    public static class Image {
        public static final String LIST_TEMPLATES_COMMAND = "listTemplates";
        public static final String TEMPLATE_FILTER_KEY_JSON = "templatefilter";
        public static final String LIST_TEMPLATES_KEY_JSON = "listtemplatesresponse";
        public static final String TEMPLATE_KEY_JSON = "template";
        public static final String NAME_KEY_JSON = "name";
        public static final String SIZE_KEY_JSON = "size";
        public static final String ID_KEY_JSON = "id";
        public static final String EXECUTABLE_TEMPLATES_VALUE = "executable";
    }
}