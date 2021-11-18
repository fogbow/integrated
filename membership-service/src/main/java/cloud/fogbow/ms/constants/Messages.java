package cloud.fogbow.ms.constants;

public class Messages {
    public static class Exception {
        public static final String DEFAULT_ROLE_NAME_IS_INVALID = "Default role name is invalid.";
        public static final String GENERIC_EXCEPTION_S = "Operation returned error: %s.";
        public static final String INVALID_MEMBER_NAME = "Invalid member name in configuration.";
        public static final String MEMBER_IS_NOT_REQUESTER = "Member is not requester.";
        public static final String MEMBER_IS_NOT_TARGET = "Member is not target.";
        public static final String NO_ADMIN_SPECIFIED = "No admin specified in the configuration file.";
        public static final String PROVIDER_IS_ALREADY_A_MEMBER = "Provider is already a member.";
        public static final String PROVIDER_IS_ALREADY_A_REQUESTER = "Provider is already a requester.";
        public static final String PROVIDER_IS_ALREADY_A_TARGET = "Provider is already a target.";
        public static final String PROVIDER_MUST_BE_TARGET_REQUESTER_OR_BOTH = "Provider must be target, requester, or both.";
        public static final String UNABLE_TO_FIND_CLASS_S = "Unable to find class %s.";
		public static final String USER_IS_NOT_ADMIN = "Not-admin user trying to perform admin-only operation.";
    }

    public static class Log {
    	public static final String ADDING_NEW_PROVIDER = "Adding provider: %s.";
    	public static final String ADDING_REQUESTER_PROVIDER = "Adding requester: %s.";
    	public static final String ADDING_TARGET_PROVIDER = "Adding target: %s.";
    	public static final String CHANGING_MEMBERSHIP_PLUGIN = "Changing membership plugin to: %s.";
    	public static final String GET_PUBLIC_KEY = "Get public key received.";
        public static final String INTERNAL_SERVER_ERROR = "Internal server error.";
        public static final String RELOADING_AUTHORIZATION_PLUGIN = "Reloading authorization plugin.";
        public static final String RELOADING_CONFIGURATION = "Reloading service configuration.";
        public static final String RELOADING_MEMBERSHIP_PLUGIN = "Reloading membership plugin.";
        public static final String RELOADING_MS_KEYS_HOLDER = "Reloading service keys.";
        public static final String RELOADING_PROPERTIES_HOLDER = "Reloading properties holder.";
        public static final String RELOADING_PUBLIC_KEYS_HOLDER = "Reloading public keys holder.";
		public static final String REMOVING_PROVIDER = "Removing provider: %s.";
		public static final String REMOVING_REQUESTER_PROVIDER = "Removing requester: %s.";
		public static final String REMOVING_TARGET_PROVIDER = "Removing target: %s.";
    }
}
