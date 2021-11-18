package cloud.fogbow.rcs.api.http.request;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.rcs.core.ApplicationFacade;
import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@PrepareForTest({ ApplicationFacade.class })
@PowerMockRunnerDelegate(SpringRunner.class)
@WebMvcTest(Config.class)
public class ConfigTest extends BaseUnitTests {

    private ApplicationFacade facade;
    @Autowired
    private MockMvc mockMvc;

    private static final String EXPIRATION_TIME_SUFFIX = "/expiration_time";

    @Before
    public void setup() throws FogbowException {
        super.setup();
        this.facade = testUtils.mockApplicationFacade();
    }

    //test case: check if HttpStatus.OK is returned when an update expiration request is performed.
    @Test
    public void testUpdateCacheExpirationTime() throws Exception {
        // set up
        Mockito.doNothing().when(this.facade).updateCacheExpiration(Mockito.any());
        String endpoint = TestUtils.BASE_URL.concat(Config.ENDPOINT).concat(EXPIRATION_TIME_SUFFIX);
        HttpHeaders headers = new HttpHeaders();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content("{\"value\": \"10\"}")
                .contentType(MediaType.APPLICATION_JSON);

        // exercise
        MvcResult result =this.mockMvc.perform(requestBuilder).andReturn();
        //verify
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

}