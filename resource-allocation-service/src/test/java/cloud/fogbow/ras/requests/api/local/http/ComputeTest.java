package cloud.fogbow.ras.requests.api.local.http;

import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.ras.api.http.CommonKeys;
import cloud.fogbow.ras.api.http.request.Compute;
import cloud.fogbow.ras.core.ApplicationFacade;
import cloud.fogbow.ras.api.http.response.InstanceStatus;
import cloud.fogbow.ras.core.models.ResourceType;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import cloud.fogbow.ras.api.http.response.quotas.ComputeQuota;
import cloud.fogbow.ras.api.http.response.quotas.allocation.ComputeAllocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@WebMvcTest(value = Compute.class, secure = false)
@PrepareForTest(ApplicationFacade.class)
public class ComputeTest {

    private static final String ENDPOINT_SUFFIX = "/cloudName";

	private static final String CORRECT_BODY =
            "{\"requestingProvider\":\"req-provider\", \"providingMember\":\"prov-provider\", "
                    + "\"publicKey\":\"pub-key\", \"vCPU\":\"2\", \"memory\":\"1024\", \"disk\":\"20\", "
                    + "\"imageName\":\"ubuntu\"}";

    private static final String WRONG_BODY = "";
    private static final String FAKE_ORDER_ID = "fake-order-id";

    @Autowired
    private MockMvc mockMvc;

    private ApplicationFacade facade;

    private static final String COMPUTE_ENDPOINT = "/" + Compute.COMPUTE_ENDPOINT;

    @Before
    public void setUp() {
        this.facade = Mockito.spy(ApplicationFacade.class);
        PowerMockito.mockStatic(ApplicationFacade.class);
        BDDMockito.given(ApplicationFacade.getInstance()).willReturn(this.facade);
    }

    // test case: Request a compute creation and test successfully return. Check the response of request
    // and the call of facade for create the compute.
    @Test
    public void testCreateCompute() throws Exception {

        // set up
        Mockito.doReturn(FAKE_ORDER_ID).when(this.facade).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, COMPUTE_ENDPOINT, getHttpHeaders(), CORRECT_BODY);

        // exercise: Make the request
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.CREATED.value();
        String expectedResponse = String.format("{\"id\":\"%s\"}", FAKE_ORDER_ID);
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Assert.assertEquals(expectedResponse, result.getResponse().getContentAsString());

        Mockito.verify(this.facade, Mockito.times(1)).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
    }

    // test case: Request a compute creation and test bad request return. Check the response of request
    // and the call of facade for create the compute.
    @Test
    public void testCreateComputeBadRequest() throws Exception {

        //set up
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, COMPUTE_ENDPOINT, getHttpHeaders(), WRONG_BODY);

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.BAD_REQUEST.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Assert.assertEquals("", result.getResponse().getContentAsString());

        // The request has problems, so the call to Facade is not executed.
        Mockito.verify(this.facade, Mockito.times(0)).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
    }

    // test case: Request a compute creation with an unauthorized user. Check the response of request
    // and the call of facade for create the compute.
    @Test
    public void testCreateComputeUnauthorizedException() throws Exception {

        // set up
        Mockito.doThrow(new UnauthorizedRequestException()).when(this.facade).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, COMPUTE_ENDPOINT, getHttpHeaders(), CORRECT_BODY);

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.FORBIDDEN.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        Mockito.verify(this.facade, Mockito.times(1)).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
    }

    // test case: Request a compute creation with an unauthenticated user. Check the response of request
    // and the call of facade for create the compute.
    @Test
    public void testCreateComputeUnauthenticatedException() throws Exception {

        // set up
        Mockito.doThrow(new UnauthenticatedUserException()).when(this.facade).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.POST, COMPUTE_ENDPOINT, getHttpHeaders(), CORRECT_BODY);

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.UNAUTHORIZED.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        Mockito.verify(this.facade, Mockito.times(1)).createCompute(Mockito.any(ComputeOrder.class), Mockito.anyString());
    }

    // test case: Request the list of all computes status when the facade returns an empty list. 
    // Check the response of request and the call of facade for get the computes status.
    @Test
    public void testGetAllComputeStatusEmptyList() throws Exception {

        // set up
        Mockito.doReturn(new ArrayList<InstanceState>()).when(this.facade).getAllInstancesStatus(Mockito.anyString(), Mockito.any(ResourceType.class));
        String COMPUTE_STATUS_ENDPOINT = COMPUTE_ENDPOINT + "/" + Compute.STATUS_SUFFIX_ENDPOINT;
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, COMPUTE_STATUS_ENDPOINT, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.OK.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        String expectedResult = "[]";
        Assert.assertEquals(expectedResult, result.getResponse().getContentAsString());

        Mockito.verify(this.facade, Mockito.times(1)).getAllInstancesStatus(Mockito.anyString(), Mockito.any(ResourceType.class));

    }

    // test case: Request the list of all computes status when the facade returns a non-empty list. 
    // Check the response of request and the call of facade for get the computes status.
    @Test
    public void testGetAllComputeStatusWhenHasData() throws Exception {

        // set up
        final String FAKE_ID_1 = "fake-Id-1";
        final String FAKE_ID_2 = "fake-Id-2";
        final String FAKE_ID_3 = "fake-Id-3";
        final String FAKE_PROVIDER = "fake-provider";
        final String FAKE_CLOUD_NAME = "fake-cloud-name";

        InstanceStatus instanceStatus1 = new InstanceStatus(FAKE_ID_1, FAKE_PROVIDER, FAKE_CLOUD_NAME, InstanceState.READY);
        InstanceStatus instanceStatus2 = new InstanceStatus(FAKE_ID_2, FAKE_PROVIDER, FAKE_CLOUD_NAME, InstanceState.READY);
        InstanceStatus instanceStatus3 = new InstanceStatus(FAKE_ID_3, FAKE_PROVIDER, FAKE_CLOUD_NAME, InstanceState.READY);

        List<InstanceStatus> computeStatusList = Arrays.asList(new InstanceStatus[]{instanceStatus1, instanceStatus2, instanceStatus3});
        Mockito.doReturn(computeStatusList).when(this.facade).getAllInstancesStatus(Mockito.anyString(), Mockito.any(ResourceType.class));

        String COMPUTE_STATUS_ENDPOINT = COMPUTE_ENDPOINT + "/" + Compute.STATUS_SUFFIX_ENDPOINT;
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, COMPUTE_STATUS_ENDPOINT, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.OK.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        TypeToken<List<InstanceStatus>> token = new TypeToken<List<InstanceStatus>>() {
        };
        List<InstanceStatus> resultList = new Gson().fromJson(result.getResponse().getContentAsString(), token.getType());
        Assert.assertEquals(3, resultList.size());
        Assert.assertEquals(FAKE_ID_1, resultList.get(0).getInstanceId());
        Assert.assertEquals(FAKE_ID_2, resultList.get(1).getInstanceId());
        Assert.assertEquals(FAKE_ID_3, resultList.get(2).getInstanceId());

        Mockito.verify(this.facade, Mockito.times(1)).getAllInstancesStatus(Mockito.anyString(), Mockito.any(ResourceType.class));
    }

    // test case: Request a compute by its id with an unauthenticated user. Check the response of request
    // and the call of facade for get the compute.
    @Test
    public void testGetComputeByIdUnauthenticatedException() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new UnauthenticatedUserException()).when(this.facade).getCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.UNAUTHORIZED.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        Mockito.verify(this.facade, Mockito.times(1)).getCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request a compute by its id with an unauthorized user. Check the response of request
    // and the call of facade for get the compute.
    @Test
    public void testGetComputeByIdUnauthorizedException() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new UnauthorizedRequestException()).when(this.facade).getCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();


        // verify
        int expectedStatus = HttpStatus.FORBIDDEN.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        Mockito.verify(this.facade, Mockito.times(1)).getCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request a compute by its id when the instance is not found. Check the response of request
    // and the call of facade for get the compute.
    @Test
    public void testGetNotFoundComputeById() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new InstanceNotFoundException()).when(this.facade).getCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.NOT_FOUND.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        Mockito.verify(this.facade, Mockito.times(1)).getCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request a compute by its id and test successfully return. Check the response of request
    // and the call of facade for get the compute.
    @Test
    public void testGetComputeById() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";

        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        ComputeInstance computeInstance = new ComputeInstance(FAKE_ID);
        Mockito.doReturn(computeInstance).when(this.facade).getCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.OK.value();
        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());

        ComputeInstance resultComputeInstance = new Gson().fromJson(result.getResponse().getContentAsString(), ComputeInstance.class);
        Assert.assertNotNull(resultComputeInstance);
        Assert.assertEquals(computeInstance.getId(), resultComputeInstance.getId());

        Mockito.verify(this.facade, Mockito.times(1)).getCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Delete a compute by its id and test successfully return. Check the response of request
    // and the call of facade for delete the compute.
    @Test
    public void testDeleteExistingCompute() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doNothing().when(this.facade).deleteCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.DELETE, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.OK.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).deleteCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Delete a compute with unauthenticated user. Check the response of request
    // and the call of facade for delete the compute.
    @Test
    public void testDeleteUnauthenticatedException() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new UnauthenticatedUserException()).when(this.facade).deleteCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.DELETE, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.UNAUTHORIZED.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).deleteCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Delete a compute with unauthorized user. Check the response of request
    // and the call of facade for delete the compute.
    @Test
    public void testDeleteUnauthorizedException() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new UnauthorizedRequestException()).when(this.facade).deleteCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.DELETE, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.FORBIDDEN.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).deleteCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Delete a compute not found. Check the response of request
    // and the call of facade for delete the compute.
    @Test
    public void testDeleteNotFoundCompute() throws Exception {

        // set up
        final String FAKE_ID = "fake-Id-1";
        String computeIdEndpoint = COMPUTE_ENDPOINT + "/" + FAKE_ID;
        Mockito.doThrow(new InstanceNotFoundException()).when(this.facade).deleteCompute(Mockito.anyString(), Mockito.anyString());
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.DELETE, computeIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.NOT_FOUND.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).deleteCompute(Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request the user allocation with unauthenticated user. Check the response of request
    // and the call of facade for get the user allocation.
    @Test
    public void testGetUserAllocationUnauthenticatedException() throws Exception {

        // set up
        final String FAKE_PROVIDER_ID = "fake-provider-id";
        Mockito.doThrow(new UnauthenticatedUserException()).when(this.facade).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        final String ALLOCATION_ENDPOINT = COMPUTE_ENDPOINT + "/" + Compute.ALLOCATION_SUFFIX_ENDPOINT;
        final String providerIdEndpoint = ALLOCATION_ENDPOINT + "/" + FAKE_PROVIDER_ID + ENDPOINT_SUFFIX;
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, providerIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.UNAUTHORIZED.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request the user allocation with unauthorized user. Check the response of request
    // and the call of facade for get the user allocation.
    @Test
    public void testGetUserAllocationUnauthorizedException() throws Exception {


        // set up
        final String FAKE_PROVIDER_ID = "fake-provider-id";
        Mockito.doThrow(new UnauthorizedRequestException()).when(this.facade).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        final String ALLOCATION_ENDPOINT = COMPUTE_ENDPOINT + "/" + Compute.ALLOCATION_SUFFIX_ENDPOINT;
        final String providerIdEndpoint = ALLOCATION_ENDPOINT + "/" + FAKE_PROVIDER_ID + ENDPOINT_SUFFIX;
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, providerIdEndpoint, getHttpHeaders(), "");

        // exercise
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.FORBIDDEN.value();

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Mockito.verify(this.facade, Mockito.times(1)).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    // test case: Request the user allocation and test successfully return. Check the response of request
    // and the call of facade for get the user allocation.
    @Test
    public void testGetUserAllocation() throws Exception {

        // set up
        final String FAKE_PROVIDER_ID = "fake-provider-id";
        final int VCPU_TOTAL = 1;
        final int RAM_TOTAL = 1;
        final int INSTANCES_TOTAL = 1;

        ComputeAllocation fakeComputeAllocation = new ComputeAllocation(INSTANCES_TOTAL, VCPU_TOTAL, RAM_TOTAL);

        Mockito.doReturn(fakeComputeAllocation).when(this.facade).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        final String ALLOCATION_ENDPOINT = COMPUTE_ENDPOINT + "/" + Compute.ALLOCATION_SUFFIX_ENDPOINT;
        final String providerIdEndpoint = ALLOCATION_ENDPOINT + "/" + FAKE_PROVIDER_ID + ENDPOINT_SUFFIX;
        RequestBuilder requestBuilder = createRequestBuilder(HttpMethod.GET, providerIdEndpoint, getHttpHeaders(), "");

        // set up
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        // verify
        int expectedStatus = HttpStatus.OK.value();
        ComputeAllocation resultComputeAllocation = new Gson().fromJson(result.getResponse().getContentAsString(), ComputeAllocation.class);

        Assert.assertEquals(expectedStatus, result.getResponse().getStatus());
        Assert.assertEquals(fakeComputeAllocation.getInstances(), resultComputeAllocation.getInstances());
        Assert.assertEquals(fakeComputeAllocation.getRam(), resultComputeAllocation.getRam());
        Assert.assertEquals(fakeComputeAllocation.getvCPU(), resultComputeAllocation.getvCPU());

        Mockito.verify(this.facade, Mockito.times(1)).getComputeAllocation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    private RequestBuilder createRequestBuilder(HttpMethod method, String urlTemplate, HttpHeaders headers, String body) {
        switch (method) {
            case POST:
                return MockMvcRequestBuilders.post(urlTemplate)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON);
            case GET:
                return MockMvcRequestBuilders.get(urlTemplate)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON);
            case DELETE:
                return MockMvcRequestBuilders.delete(urlTemplate)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON);
            default:
                return null;
        }

    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String fakeUserToken = "fake-access-id";
        headers.set(CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY, fakeUserToken);
        return headers;
    }
}

