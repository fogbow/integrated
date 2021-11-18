package cloud.fogbow.ms.api.http.request;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cloud.fogbow.ms.constants.SystemConstants;

@RunWith(SpringRunner.class)
@WebMvcTest
public class VersionTests {

    private static final String BASE_ENDPOINT = "/";
    private static final String BUILD_PROPERTY = "abcd";
    private static final String VERSION_NUMBER_SEPARATOR = "-";

    @Autowired
    private MockMvc mockMvc;

    private Version version;

    @Before
    public void setup() {
        this.version = new Version();
    }

    // test case: When invoking the getVersionNumber method with no parameters, it
    // must use a default file and return the version number concatenated the value
    // of the default file build number property.
    @Test
    public void testVersionWithoutConfPath() throws Exception {
        // exercise
        String build = this.version.getVersionNumber();

        // verify
        Assert.assertEquals(SystemConstants.API_VERSION_NUMBER + VERSION_NUMBER_SEPARATOR + BUILD_PROPERTY, build);
    }

    // test case: When executing a GET request for the "/ms/version" endpoint, it
    // must invoke the getVersion method by returning the Status OK and the version
    // content in format json.
    @Test
    public void testGetVersion() throws Exception {
        // set up
        String expected = getJsonResponseContent();

        // exercise
        this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_ENDPOINT + Version.VERSION_ENDPOINT))
                // verify
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    private String getJsonResponseContent() {
        return "{\"version\":" + SystemConstants.API_VERSION_NUMBER
                + VERSION_NUMBER_SEPARATOR
                + BUILD_PROPERTY
                + "}";
    }

}
