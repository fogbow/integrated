package cloud.fogbow.common.util.connectivity.cloud.cloudstack;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.models.CloudStackUser;
import cloud.fogbow.common.util.connectivity.cloud.CloudHttpClient;
import cloud.fogbow.common.util.connectivity.HttpRequest;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class CloudStackHttpClient extends CloudHttpClient<CloudStackUser> {
    public CloudStackHttpClient() {
    }

    @Override
    public HttpRequest prepareRequest(HttpRequest genericRequest, CloudStackUser cloudUser) throws InternalServerErrorException,
            UnauthorizedRequestException {
        try {
            HttpRequest clonedRequest = new HttpRequest(
                genericRequest.getMethod(), genericRequest.getUrl(), genericRequest.getBody(), genericRequest.getHeaders());
            URIBuilder uriBuilder = new URIBuilder(clonedRequest.getUrl());
            CloudStackUrlUtil.sign(uriBuilder, cloudUser.getToken());

            clonedRequest.setUrl(uriBuilder.toString());
            clonedRequest.setHeaders(cloudUser.getCookieHeaders());
            return clonedRequest;
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (UnauthorizedRequestException e) {
            throw new UnauthorizedRequestException(e.getMessage());
        }
    }

}
