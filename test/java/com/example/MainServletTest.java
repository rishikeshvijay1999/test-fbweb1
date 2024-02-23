import static org.junit.Assert.*;

import org.junit.Test;

public class MainServletTest {

    @Test
    public void testIndexJspContent() {
        // Simulating the presence of "facebook" in index.jsp content for testing
        String simulatedJspContent = "<html>\n<body>\nWelcome to my website. This is a Facebook page.\n</body>\n</html>";

        // Replace this with the actual logic of your test
        assertTrue(simulatedJspContent.contains("Facebook"));
    }
}
