package cloud.fogbow.common.util.connectivity.cloud.cloudstack;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.models.CloudStackUser;

public class CloudStackQueryJobResult {

    public static final int PROCESSING = 0;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;

    public static String getQueryJobResult(CloudStackHttpClient client, String cloudStackUrl, String jobId, CloudStackUser cloudStackUser)
            throws FogbowException {
        QueryAsyncJobResultRequest queryAsyncJobResultRequest = new QueryAsyncJobResultRequest.Builder()
                .jobId(jobId)
                .build(cloudStackUrl);

        CloudStackUrlUtil
                .sign(queryAsyncJobResultRequest.getUriBuilder(), cloudStackUser.getToken());

        String jsonResponse = null;
        String requestUrl = queryAsyncJobResultRequest.getUriBuilder().toString();

        jsonResponse = client.doGetRequest(requestUrl, cloudStackUser);

        return jsonResponse;
    }
}
