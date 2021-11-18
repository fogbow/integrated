package cloud.fogbow.rcs.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import cloud.fogbow.common.exceptions.FogbowException;

/*
 * This class is intended to reuse code components to assist other unit test classes 
 * but does not contemplate performing any tests. The @Ignore annotation is being used 
 * in this context to prevent it from being initialized as a test class.
 */
@Ignore
@RunWith(PowerMockRunner.class)
public class BaseUnitTests {

    protected TestUtils testUtils;

    @Before
    public void setup() throws FogbowException {
        this.testUtils = new TestUtils();
    }
}
