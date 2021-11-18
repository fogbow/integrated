package cloud.fogbow.rcs.api.http.request;

import cloud.fogbow.rcs.core.ApplicationFacade;
import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@PrepareForTest({ ApplicationFacade.class })
@PowerMockRunnerDelegate(SpringRunner.class)
@WebMvcTest(Service.class)
public class ServiceTest extends BaseUnitTests {
    private static final String SERVICE_ENDPOINT = TestUtils.BASE_URL.concat(
            TestUtils.MEMBERS[TestUtils.LOCAL_MEMBER_INDEX] + TestUtils.BASE_URL + TestUtils.SERVICES[TestUtils.LOCAL_MEMBER_INDEX]);

    @Autowired
    private MockMvc mockMvc;

    private ApplicationFacade facade;

    @Before
    public void setUp() {
        this.facade = this.testUtils.mockApplicationFacade();
    }

    // test case: When executing a GET request for the "/rcs/services/{memberId}/serviceType" endpoint, it
    // must invoke the getService method by returning the Status OK, the spec catalog content to be used
    // on the swagger-ui template and the respective generated catalog page.
    @Test
    public void testGetAllMembers() throws Exception {
        // set up
        String expected = this.testUtils.getServiceResponseContent();
        String fakeCatalogSpec = this.testUtils.getCatalogSpec();
        Mockito.doReturn(fakeCatalogSpec).when(this.facade).getService(Mockito.anyString(), Mockito.anyString());

        // exercise
        this.mockMvc.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL.concat(Service.ENDPOINT + SERVICE_ENDPOINT)))
                // verify
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    //test case: Check if the HttpStatus.OK is returned when a delete request is performed.
    @Test
    public void testDeleteService() throws Exception {
        // set up
        Mockito.doNothing().when(this.facade).removeCache(Mockito.anyString(), Mockito.anyString());
        // exercise
        this.mockMvc.perform(MockMvcRequestBuilders.delete(TestUtils.BASE_URL.concat(Service.ENDPOINT + SERVICE_ENDPOINT)))
                // verify
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
