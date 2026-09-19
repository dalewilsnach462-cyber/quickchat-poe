package quickchat;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles user registration and login for the QuickChat console application
 * (Part 1 of the PROG5121 PoE).
 *
 * Responsibilities:
 *  - Validate a username (must contain an underscore and be no more than
 *    five characters long).
 *  - Validate password complexity (min 8 characters, a capital letter,
 *    a number, and a special character).
 *  - Validate a South African cell phone number using a regular expression.
 *  - Register a user and report success/failure messages.
 *  - Authenticate a user on login and report the correct status message.
 *
 * Reference:
 * The regular-expression approach used in checkCellPhoneNumber() to validate
 * a phone number that begins with an international dialling code is adapted
 * from the pattern-based validation techniques described at:
 * Baeldung, 2023. Validate Phone Numbers with Regular Expressions in Java.
 * Available at: https://www.baeldung.com/java-regex-validate-phone-numbers
 * (Accessed: 2026).
 */



public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Matches a "+" followed by the South African country code (27) and
    // exactly 9 further digits, e.g. +27838968976.
    private static final String CELL_PHONE_REGEX = "^\\+27\\d{9}$";

    public Login() {
    }

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---------- Getters / setters ----------

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    // ---------- Validation methods ----------

    /**
     * Checks that the username contains an underscore and is no more than
     * five characters long.
     */
    // Checks two conditions: the username must include an underscore
// and must not exceed five characters in length.
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks that the password:
     *  - is at least eight characters long
     *  - contains a capital letter
     *  - contains a number
     *  - contains a special character
     */
    // Password must be at least 8 characters, and contain a capital 
     // letter, a number, and a special character to be considered valid
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        boolean hasMinLength = password.length() >= 8;
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return hasMinLength && hasCapital && hasNumber && hasSpecialChar;
    }

    /**
     * Checks that the cell phone number contains the South African
     * international country code (+27) followed by nine digits.
     */
     // Validates that the number starts with South Africa's international
    // dialling code (+27) followed by nine digits.
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        return Pattern.matches(CELL_PHONE_REGEX, cellPhoneNumber);
    }

    // ---------- Registration ----------

    /**
     * Validates the supplied username, password and cell phone number and,
     * if all three are valid, stores them against this Login instance.
     *
     * @return the combined feedback message for all three checks.
     */
     // Runs all three validation checks and only stores the user's
    // details if username, password, and cell number are all valid.
    public String registerUser(String username, String password, String cellPhoneNumber) {
        boolean validUsername = checkUserName(username);
        boolean validPassword = checkPasswordComplexity(password);
        boolean validCell = checkCellPhoneNumber(cellPhoneNumber);

        StringBuilder message = new StringBuilder();

        message.append(validUsername
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        message.append(System.lineSeparator());

        message.append(validPassword
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        message.append(System.lineSeparator());

        message.append(validCell
                ? "Cell phone number successfully added."
                : "Cell phone number incorrectly formatted or does not contain international code.");

        if (validUsername && validPassword && validCell) {
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
            message.append(System.lineSeparator()).append("Registration successful!");
        }

        return message.toString();
    }

    // ---------- Login ----------

    /**
     * Verifies that the supplied username and password match the details
     * captured during registration.
     */
    public boolean loginUser(String username, String password) {
        return username != null && password != null
                && username.equals(this.username)
                && password.equals(this.password);
    }

    /**
     * Returns the appropriate message depending on whether login succeeded.
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
