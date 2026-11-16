package quickchat;

import java.util.Scanner;

/**
 * Main.java
 *
 * Console application for QuickChat. Part 1: register and log in.
 * Part 2: send messages from a numeric menu. No GUI / JOptionPane.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // ---------- Part 1: registration ----------
        System.out.println("=== QuickChat Registration ===");
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

            System.out.println(login.registerUser(username, password, cellPhoneNumber));
            System.out.println();

            registered = login.checkUserName(username)
                    && login.checkPasswordComplexity(password)
                    && login.checkCellPhoneNumber(cellPhoneNumber);
            if (!registered) {
                System.out.println("Please try registering again.\n");
            }
        }

        // ---------- Part 1: login (required before messaging) ----------
        System.out.println("=== QuickChat Login ===");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();
            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            loggedIn = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loggedIn));
        }

        // ---------- Part 2: messaging ----------
        System.out.println();
        System.out.println("Welcome to QuickChat.");
        Message.loadStoredMessagesFromJson();

        int maxMessages = readPositiveInt(scanner, "How many messages do you wish to enter? ");
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    if (messagesEntered >= maxMessages) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                        break;
                    }
                    messagesEntered = sendMessages(scanner, maxMessages, messagesEntered);
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                case "4":
                    storedMessagesMenu(scanner, login.getUsername());
                    break;
                default:
                    System.out.println("Invalid option, please enter 1, 2, 3 or 4.");
            }
        }

        scanner.close();
    }

    /** Lets the user enter the remaining messages; returns the new count entered. */
    private static int sendMessages(Scanner scanner, int maxMessages, int alreadyEntered) {
        for (int i = alreadyEntered; i < maxMessages; i++) {
            System.out.println();
            System.out.println("--- Message " + (i + 1) + " of " + maxMessages + " ---");

            // Recipient: keep asking until it is valid.
            String recipient;
            boolean recipientValid;
            do {
                System.out.print("Enter recipient cell number (e.g. +27718693002): ");
                recipient = scanner.nextLine().trim();
                String result = new Message(recipient, "check", i).checkRecipientCell();
                System.out.println(result);
                recipientValid = result.equals("Cell phone number successfully captured.");
            } while (!recipientValid);

            // Message text: keep asking until it is 1-250 characters.
            String text;
            do {
                System.out.print("Enter your message: ");
                text = scanner.nextLine();
                if (text.length() > 250) {
                    System.out.println("Please enter a message of less than 250 characters.");
                }
            } while (text.length() > 250 || text.isBlank());

            Message message = new Message(recipient, text, i);
            System.out.println("Message ID generated: " + message.getMessageID());

            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(message.sentMessage("send"));
                    System.out.println(message.getDetails());
                    break;
                case "2":
                    System.out.println(message.sentMessage("disregard"));
                    break;
                case "3":
                    System.out.println(message.sentMessage("store"));
                    break;
                default:
                    System.out.println("Invalid option, message disregarded.");
            }
        }
        return maxMessages;
    }

    /** Part 3: sub-menu for working with the stored/sent message arrays. */
    private static void storedMessagesMenu(Scanner scanner, String senderUsername) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Stored Messages ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search all messages for a recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display a report of the messages");
            System.out.println("g) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println(Message.displayStoredSendersAndRecipients(senderUsername));
                    break;
                case "b":
                    System.out.println(Message.displayLongestStoredMessage());
                    break;
                case "c":
                    System.out.print("Enter the message ID: ");
                    System.out.println(Message.searchByMessageID(scanner.nextLine().trim()));
                    break;
                case "d":
                    System.out.print("Enter the recipient number: ");
                    System.out.println(Message.searchMessagesByRecipient(scanner.nextLine().trim()));
                    break;
                case "e":
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteMessageByHash(scanner.nextLine().trim()));
                    break;
                case "f":
                    System.out.println("=== Stored messages ===");
                    System.out.println(Message.displayStoredReport());
                    System.out.println("=== Sent messages ===");
                    System.out.println(Message.displayReport());
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please enter a letter from a to g.");
            }
        }
    }

    // Keeps asking until the user types a whole number greater than 0
    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}
