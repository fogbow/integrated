package cloud.fogbow.ms.constants;

public class ApiDocumentation {

    public static class ApiInfo {
        public static final String API_TITLE = "Fogbow Membership Service API";
        public static final String API_DESCRIPTION = "This API introduces readers to Fogbow MS REST API, "
                + "provides guidelines on how to use it, and describes the available features accessible from it.";
    }

    public static class Model {
        public static final String MEMBERS_LIST = "[\n" +
                "    \"provider1.domain1\",\n" +
                "    \"provider2.domain2\"\n" +
                "  ]\n";
    }

    public static class Membership {
        public static final String API = "Queries the members currently belonging to the federation";
        public static final String DESCRIPTION = "Lists the member ids in the federation.";
    }

    public static class Authorization {
        public static final String API = "States whether the provider is a member or not.";
        public static final String DESCRIPTION = "States whether the provider is a member or not.";
        public static final String REQUESTER_OPERATION = "States whether the provider is allowed to perform operations in the local provider.";
        public static final String TARGET_OPERATION = "States whether the local provider is allowed to perform operations in the given provider.";
    }

    public static class Admin {
    	public static final String API = "Manages admin-only operations";
		public static final String RELOAD = "Reloads service configuration.";
		public static final String ADD_PROVIDER = "Adds given provider to the list of known providers.";
		public static final String REMOVE_PROVIDER = "Removes given provider from all the lists of providers kept by the service.";
		public static final String UPDATE_PROVIDER = "Updates permission information for the given provider.";
		public static final String SERVICE = "Changes membership service plugin to the given class name.";
    }
}
