package cloud.fogbow.common.util.connectivity.cloud.cloudstack;

import cloud.fogbow.common.constants.CloudStackConstants;
import cloud.fogbow.common.constants.Messages;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CloudStackUrlUtil {
    private static final Logger LOGGER = Logger.getLogger(CloudStackUrlUtil.class);

    private static final String COMMAND = "command";
    private static final String JSON = "json";
    private static final String RESPONSE_FORMAT = "response";
    private static final String SIGNATURE = "signature";
    private static final String API_KEY = "apikey";

    public static void sign(URIBuilder requestEndpoint, String tokenValue) throws UnauthorizedRequestException {
        String[] tokenValueSplit = tokenValue.split(CloudStackConstants.KEY_VALUE_SEPARATOR);
        String apiKey = tokenValueSplit[0];
        String secretKey = tokenValueSplit[1];

        requestEndpoint.addParameter(API_KEY, apiKey);

        String query = null;
        try {
            query = requestEndpoint.toString().substring(
                    requestEndpoint.toString().indexOf("?") + 1);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.info(Messages.Log.UNABLE_TO_GENERATE_SIGNATURE, e);
            throw new UnauthorizedRequestException();
        }

        String[] querySplit = query.split("&");
        TreeMap<String, String> queryParts = new TreeMap<String, String>();
        for (String queryPart : querySplit) {
            String[] queryPartSplit = queryPart.split("=");
            queryParts.put(queryPartSplit[0].toLowerCase(),
                    queryPartSplit[1].toLowerCase());
        }

        StringBuilder orderedQuery = new StringBuilder();
        for (Entry<String, String> queryPartEntry : queryParts.entrySet()) {
            if (orderedQuery.length() > 0) {
                orderedQuery.append("&");
            }
            orderedQuery.append(queryPartEntry.getKey()).append("=")
                    .append(queryPartEntry.getValue());
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            byte[] secretKeyBytes = secretKey.getBytes(Charsets.UTF_8);
            Key key = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "HmacSHA1");
            mac.init(key);
            String signature = Base64.encodeBase64String(
                    mac.doFinal(orderedQuery.toString().getBytes(Charsets.UTF_8)));

            requestEndpoint.addParameter(SIGNATURE, signature);
        } catch (Exception e) {
            LOGGER.warn(Messages.Log.UNABLE_TO_GENERATE_SIGNATURE, e);
            throw new UnauthorizedRequestException();
        }
    }


    public static URIBuilder createURIBuilder(String endpoint, String command) throws InternalServerErrorException {
        try {
            URIBuilder uriBuilder = new URIBuilder(endpoint);
            uriBuilder.addParameter(COMMAND, command);
            uriBuilder.addParameter(RESPONSE_FORMAT, JSON);
            return uriBuilder;
        } catch (Exception e) {
            throw new InternalServerErrorException(String.format(Messages.Exception.WRONG_SYNTAX_FOR_ENDPOINT_S, endpoint));
        }
    }
}
