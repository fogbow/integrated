package cloud.fogbow.common.plugins.cloudidp.cloudstack.v4_9;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.util.connectivity.cloud.cloudstack.CloudStackRequest;

public class LoginRequest extends CloudStackRequest {
    public static final String LOGIN_COMMAND = "login";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String DOMAIN_KEY = "domain";

    private LoginRequest(Builder builder) throws InternalServerErrorException {
        super(builder.cloudStackUrl);
        addParameter(USERNAME_KEY, builder.username);
        addParameter(PASSWORD_KEY, builder.password);
        addParameter(DOMAIN_KEY, builder.domain);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getCommand() {
        return LOGIN_COMMAND;
    }

    public static class Builder {
        private String cloudStackUrl;
        private String username;
        private String password;
        private String domain;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public LoginRequest build(String cloudStackUrl) throws UnauthenticatedUserException {
            this.cloudStackUrl = cloudStackUrl;
            try {
                return new LoginRequest(this);
            } catch (InternalServerErrorException e) {
                throw new UnauthenticatedUserException(e.getMessage());
            }
        }
    }
}
