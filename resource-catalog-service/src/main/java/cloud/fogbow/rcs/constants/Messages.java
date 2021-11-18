package cloud.fogbow.rcs.constants;

public class Messages {
    
    public static class Exception {
        public static final String GENERIC_EXCEPTION = "Operation returned error: %s."; // FIXME change this constant to GENERIC_EXCEPTION_S
        public static final String UNABLE_TO_RETRIEVE_RESPONSE_FROM_MEMBER_S = "Unable to retrieve response from member: %s.";
        public static final String NO_SUCH_MEMBER = "There is no member with id: %s.";
    }

    public static class Info {
        public static final String GET_PUBLIC_KEY = "Get public key received.";
        public static final String GET_VERSION = "Get version request received.";
        public static final String GETTING_ALL_MEMBERS = "Getting all members.";
        public static final String GETTING_ALL_SERVICES = "Getting all member services.";
        public static final String GETTING_SERVICE = "Getting service catalog page.";
        public static final String NO_REMOTE_COMMUNICATION_CONFIGURED = "No remote communication configured.";
        public static final String RECEIVING_REMOTE_REQUEST_FROM_S = "Received remote request from member: %s.";
        public static final String SEND_SUCCESSFULLY = "Send operation executed successfully.";
        public static final String SENDING_REMOTE_REQUEST_FROM_MEMBER_S = "Sending remote request from member: %s.";
        public static final String UPDATING_EXPIRATION_TIME = "Updating the cache's expiration time";
        public static final String DELETING_SERVICE = "Deleting service.";
    }

    public static class Error {
        public static final String ERROR_WHILE_GETTING_SERVICE_S_FROM_MEMBER_S = "Error while getting %s from member: %s.";
        public static final String ERROR_TRYING_TO_SAVE = "Error trying to save data to cache service";
        public static final String NO_PACKET_SENDER = "No packet sender.";
    }
}
