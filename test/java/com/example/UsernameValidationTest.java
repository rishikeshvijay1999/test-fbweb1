import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class UsernameValidationTest {

    @Test
    public void testValidUsername() {
        // Valid username that does not consist entirely of numbers
        String validUsername = "user123";
        assertTrue(isValidUsername(validUsername));
    }

    @Test
    public void testInvalidUsername() {
        // Invalid username consisting entirely of numbers
        String invalidUsername = "123456";
        assertFalse(isValidUsername(invalidUsername));
    }

    private boolean isValidUsername(String username) {
        // Implement your validation logic here
        // For example, check if the username contains at least one non-numeric character
        return username.matches(".*[a-zA-Z]+.*");
    }
}
