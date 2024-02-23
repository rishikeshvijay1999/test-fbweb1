import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.junit.Assert.fail;

public class PageContentTest {

    @Test
    public void testTwitterWordNotPresent() {
        try {
            // Load the HTML content of your page
            String html = "<html><body><p>This is a sample page</p></body></html>"; // Replace with your actual HTML content

            // Parse the HTML content using JSoup
            Document document = Jsoup.parse(html);

            // Check if the word "twitter" is present in the body of the page
            Element body = document.body();
            String bodyText = body.text();
            if (bodyText.toLowerCase().contains("lite")) {
                // If "twitter" is present, fail the test
                fail("Page contains the word 'lite', build should fail.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during test execution: " + e.getMessage());
        }
    }
}
