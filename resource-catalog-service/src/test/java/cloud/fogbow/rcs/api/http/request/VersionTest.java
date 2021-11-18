package cloud.fogbow.rcs.api.http.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cloud.fogbow.rcs.core.BaseUnitTests;
import cloud.fogbow.rcs.core.TestUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(Version.class)
public class VersionTest extends BaseUnitTests {

    @Autowired
    private MockMvc mockMvc;

    // test case: When executing a GET request for the "/rcs/version" endpoint, it
    // must invoke the getVersion method by returning the Status OK and its content
    // in JSON format.
    @Test
    public void testGetVersion() throws Exception {
        // set up
        String expected = this.testUtils.getVersionNumberResponseContent();

        // exercise
        this.mockMvc.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + Version.VERSION_ENDPOINT))
                // verify
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

}
