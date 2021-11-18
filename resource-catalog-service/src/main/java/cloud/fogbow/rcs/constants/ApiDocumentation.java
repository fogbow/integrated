package cloud.fogbow.rcs.constants;

public class ApiDocumentation {
    
    public static class ApiInfo {
        public static final String API_TITLE = "Fogbow Resource Catalog Service (RCS) API";
        public static final String API_DESCRIPTION = "Describe this API here."; // TODO add API description
    }
    
    public static class Model {
        public static final String MEMBERS_LIST = "[\n" + 
                "    \"services-atm-prod\",\n" + 
                "    \"member2\",\n" + 
                "    \"member3\"\n" + 
                "]";
        
        public static final String SERVICES_LIST = "[\n" + 
                "    {\n" + 
                "        \"serviceType\": \"LOCAL\",\n" + 
                "        \"location\": \"https://member1.org/doc\"\n" + 
                "    }\n" + 
                "]";

        public static final String EXPIRATION_TIME = "10";
    }

    public static class Catalog {
        public static final String API = "Provides a service catalog of the federation members";
        public static final String GET_ALL_MEMBERS_OPERATION = "Return a list of members.";
        public static final String GET_ALL_SERVICES_BY_MEMBER_OPERATION = "List a catalog of services available by member.";
        public static final String GET_SERVICE_OPERATION = "Return the specified service catalog page for a given member.";
        public static final String MEMBER = "The provider member of the federation.";
        public static final String SERVICE = "The Fogbow service the catalog refers to.";
        public static final String DELETE_SERVICE_OPERATION = "Delete the specified service from the system's cache. It will trigger a refresh when the service get requested again.";
    }

    public static class Config {
        public static final String API = "Provides a way to setup the system in runtime";
        public static final String UPDATE_EXPIRATION_TIME = "Changes the cache's expiration time.";
        public static final String EXPIRATION_TIME = "The cache's expiration time in minutes";
    }
}
