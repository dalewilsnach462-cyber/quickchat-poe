package quickchat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Message.java
 *
 * Represents one QuickChat message (Part 2 of the PROG5121 PoE) and keeps
 * track of the messages sent while the program is running.
 *
 * JSON storage reference: Stleary, 2024. JSON-in-Java (org.json).
 * Available at: https://github.com/stleary/JSON-java (Accessed: 2026).
 */
public class Message {

    // Messages sent while the program is running, and the running total.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private static final String JSON_FILE_PATH = "storedMessages.json";
    private static final Random RANDOM = new Random();

    // "+27" followed by nine digits (same rule as the Login cell check).
    private static final String CELL_PHONE_REGEX = "^\\+27\\d{9}$";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private String messageHash;

    public Message(String recipient, String messageText, int messageNumber) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    /** Builds a random ten-digit ID, one digit at a time. */
    private String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    /** Ensures the message ID is not more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Ensures the recipient has an international code and a valid length. */
    public String checkRecipientCell() {
        if (recipient != null && Pattern.matches(CELL_PHONE_REGEX, recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    /** Ensures the message is no longer than 250 characters. */
    public String checkMessageLength() {
        int length = messageText.length();
        if (length <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - 250) + "; please reduce the size.";
    }

    /**
     * Creates the hash: first two digits of the ID, a colon, the message
     * number, a colon, then the first and last words, all in capitals.
     */
    // Hash format: first two ID digits : message number : first word + last word (all caps)
    public String createMessageHash() {
        String idStart = messageID.substring(0, 2);

        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        messageHash = (idStart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    /**
     * Handles what the user chose to do with this message:
     * "send", "disregard" or "store".
     */
    public String sentMessage(String choice) {
        switch (choice.trim().toLowerCase()) {
            case "send":
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case "disregard":
                return "Press 0 to delete the message.";
            case "store":
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Appends this message to storedMessages.json. */
    public void storeMessage() {
        try {
            File file = new File(JSON_FILE_PATH);
            JSONArray messages;

            if (file.exists() && file.length() > 0) {
                messages = new JSONArray(new String(Files.readAllBytes(file.toPath())));
            } else {
                messages = new JSONArray();
            }

            JSONObject entry = new JSONObject();
            entry.put("messageID", messageID);
            entry.put("messageHash", messageHash);
            entry.put("recipient", recipient);
            entry.put("message", messageText);
            messages.put(entry);

            Files.write(file.toPath(), messages.toString(2).getBytes());
        } catch (IOException e) {
            System.out.println("Could not store the message: " + e.getMessage());
        }
    }

    /** Full details of one message: ID, hash, recipient, message. */
    public String getDetails() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    /** Returns every message sent while the program has been running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder all = new StringBuilder();
        for (Message m : sentMessages) {
            all.append(m.getDetails()).append(System.lineSeparator())
               .append("---").append(System.lineSeparator());
        }
        return all.toString();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /** Clears static state so unit tests start fresh. */
    static void resetForTests() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }
}
