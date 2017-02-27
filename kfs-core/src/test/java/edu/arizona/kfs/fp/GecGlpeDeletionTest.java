package edu.arizona.kfs.fp;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KFS_TEST_SYS8;

@ConfigureContext(session = KFS_TEST_SYS8)
public class GecGlpeDeletionTest extends KualiTestBase {

    public void testFrameworkInit() {
        // If we can even get to the point of passing the explicit "pass" case, then
        // shows us that the kuali test framework was successful in bootstrapping
        assertTrue(true);
    }

}
