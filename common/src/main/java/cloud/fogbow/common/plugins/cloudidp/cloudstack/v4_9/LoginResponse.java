package cloud.fogbow.common.plugins.cloudidp.cloudstack.v4_9;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import static cloud.fogbow.common.constants.CloudStackConstants.Identity.*;

/**
 * Documentation: https://cloudstack.apache.org/api/apidocs-4.9/apis/login.html
 * {
 * "loginresponse": {
 * "username": "user@myaddr.com",
 * "userid": "userid",
 * "domainid": "domainid",
 * "timeout": 1800,
 * "account": "user@myaddr.com",
 * "firstname": "Jon",
 * "lastname": "Doe",
 * "type": "2",
 * "registered": "false",
 * "sessionkey": "DC_08S8ALd85JixeRU4as5jHxLE"
 * }
 * }
 */
public class LoginResponse {
    @SerializedName(LOGIN_KEY_JSON)
    private Login response;

    public Login getLoginResponse() {
        return this.response;
    }

    public String getSessionKey() {
        return this.response.sessionKey;
    }

    public static LoginResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, LoginResponse.class);
    }

    private class Login {
        @SerializedName(SESSION_KEY_JSON)
        private String sessionKey;
        @SerializedName(TIMEOUT_KEY_JSON)
        private String timeout;

        public String getSessionKey() {
            return sessionKey;
        }

        public String getTimeout() {
            return timeout;
        }
    }
}
