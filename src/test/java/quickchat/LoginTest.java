package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest.java
 *
 * Unit tests for the Login class, using the exact test data supplied in the
 * PROG5121 PoE Part 1 brief.
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login("Kyle", "Smith");
    }

    // ---------- Username tests ----------

    @Test
    void testUsernameCorrectlyFormatted() {
        // Test Data: "kyl_1"
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void testUsernameIncorrectlyFormatted() {
        // Test Data: "kyle!!!!!!!"
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    // ---------- Password tests ----------

    @Test
    void testPasswordMeetsComplexityRequirements() {
        // Test Data: "Ch&&sec@ke99!"
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void testPasswordDoesNotMeetComplexityRequirements() {
        // Test Data: "password"
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // ---------- Cell phone number tests ----------

    @Test
    void testCellPhoneNumberCorrectlyFormatted() {
        // Test Data: +27838968976
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void testCellPhoneNumberIncorrectlyFormatted() {
        // Test Data: 08966553
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // ---------- registerUser() message tests ----------

    @Test
    void testRegisterUser_AllValid_ReturnsSuccessMessages() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(result.contains("Username successfully captured."));
        assertTrue(result.contains("Password successfully captured."));
        assertTrue(result.contains("Cell phone number successfully added."));
    }

    @Test
    void testRegisterUser_InvalidUsername_ReturnsErrorMessage() {
        String result = login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(result.contains(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length."));
    }

    @Test
    void testRegisterUser_InvalidPassword_ReturnsErrorMessage() {
        String result = login.registerUser("kyl_1", "password", "+27838968976");
        assertTrue(result.contains(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character."));
    }

    @Test
    void testRegisterUser_InvalidCellPhone_ReturnsErrorMessage() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553");
        assertTrue(result.contains("Cell phone number incorrectly formatted or does not contain international code."));
    }

    // ---------- Login tests ----------

    @Test
    void testLoginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void testLoginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
    }

    // ---------- returnLoginStatus() tests ----------

    @Test
    void testReturnLoginStatus_Success() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean loggedIn = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(loggedIn));
    }

    @Test
    void testReturnLoginStatus_Failure() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean loggedIn = login.loginUser("kyl_1", "wrongPassword1!");
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(loggedIn));
    }
}
