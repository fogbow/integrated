package cloud.fogbow.accs.constants;

public class Messages {

    public static class Warn {
        public static final String ERROR_READING_CONF_FILE = "Error trying to read configuration file: %s.";
        public static final String ERROR_CLOSING_CONF_FILE = "Error trying to close configuration file: %s.";
    }

    public static class Exception {
        public static final String TYPE_NOT_ALLOWED_YET = "This operation is not yet supported for the resource type %s.";
        public static final String UNAUTHORIZED_OPERATION = "The user is not authorized to perform this operation.";
        public static final String BILLING_PREDICTIONS = "Accounting predictions are not allowed.";
        public static final String START_TIME_GREATER_THAN_END_TIME = "Begin time must be smaller than end time.";
        public static final String GENERIC_EXCEPTION = "Operation returned error: %s.";
		public static final String INVALID_RECORD_TYPE = "Invalid record type: %s.";
		public static final String INVALID_RECORD_PROPERTY = "Invalid record property: %s.";
        public static final String CANNOT_EXTRACT_DATE_FROM_TIMESTAMP = "Cannot extract date from timestamp.";
    }

	public static class Info {
		public static final String RECEIVING_GET_VERSION_REQUEST = "Get request for version received.";
		public static final String UPDATING_TABLES = "Updating tables.";
		public static final String FINISHING_UPDATE = "Finishing update.";
		public static final String UPDATING_PROBLEMS = "Problem on updating %s.";
	}
	
	public static class Log {
        public static final String PROPERTY_FILE_S_NOT_FOUND = "Property file '%s' not found.";
        public static final String UNABLE_TO_CLOSE_FILE_S = "Unable to close file '%s'.";
	    
	}
}
