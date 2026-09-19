package quickchat;

import java.util.Scanner;

/**
 * Main.java
 *
 * Console application for Part 1 of the PROG5121 PoE: registration and
 * login. No GUI / JOptionPane is used, as required by the brief.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== QuickChat Registration ===");

        // Keep asking for details until registration succeeds.
        boolean registered = false;
        while (!registered) {
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter a username (must contain '_' and be <= 5 characters): ");
            String username = scanner.nextLine();

            System.out.print("Enter a password (min 8 chars, capital letter, number, special character): ");
            String password = scanner.nextLine();

            System.out.print("Enter your South African cell phone number (e.g. +27831234567): ");
            String cellPhoneNumber = scanner.nextLine();

            login.setFirstName(firstName);
            login.setLastName(lastName);

            String result = login.registerUser(username, password, cellPhoneNumber);
            System.out.println(result);
            System.out.println();

            registered = login.checkUserName(username)
                    && login.checkPasswordComplexity(password)
                    && login.checkCellPhoneNumber(cellPhoneNumber);

            if (!registered) {
                System.out.println("Please try registering again.\n");
            }
        }

        System.out.println("=== QuickChat Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));

        scanner.close();
    }
}
