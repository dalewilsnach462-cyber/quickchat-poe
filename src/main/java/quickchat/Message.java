package quickchat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Message.java
 *
 * Represents one QuickChat message and holds the arrays the application
 * works with (Parts 2 and 3 of the PROG5121 PoE):
 *   sent messages, disregarded messages, stored messages,
 *   message hashes and message IDs.
 *
 * JSON reading/writing reference: Stleary, 2024. JSON-in-Java (org.json).
 * Available at: https://github.com/stleary/JSON-java (Accessed: 2026).
 */
public class Message {

    // ---------- Arrays (filled while the program runs, never hard-coded) ----------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static int totalMessagesSent = 0;

    private static String jsonFilePath = "storedMessages.json";
    private static final Random RANDOM = new Random();

    // "+27" followed by nine digits (same rule as the Login cell check).
    private static final String CELL_PHONE_REGEX = "^\\+27\\d{9}$";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private String messageHash;

    public Message(String recipient, String messageText, int messageNumber) {
        this(recipient, messageText, messageNumber, null);
    }

    /** Used when a message is read back from JSON and must keep its original ID. */
    private Message(String recipient, String messageText, int messageNumber, String existingID) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = (existingID != null) ? existingID : generateMessageID();
        this.messageHash = createMessageHash();
    }

    // ---------- Getters ----------

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

    // ---------- Part 2: ID, recipient, length, hash ----------

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
     * Punctuation-only "words" (like a lone "!") are ignored.
     */
    public String createMessageHash() {
        String idStart = messageID.substring(0, 2);

        List<String> words = new ArrayList<>();
        for (String token : messageText.trim().split("\\s+")) {
            String cleaned = token.replaceAll("[^a-zA-Z0-9]", "");
            if (!cleaned.isEmpty()) {
                words.add(cleaned);
            }
        }

        String firstWord = words.isEmpty() ? "" : words.get(0);
        String lastWord = words.isEmpty() ? "" : words.get(words.size() - 1);

        messageHash = (idStart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    /**
     * Handles what the user chose to do with this message:
     * "send", "disregard" or "store". Also fills the Part 3 arrays.
     */
    public String sentMessage(String choice) {
        switch (choice.trim().toLowerCase()) {
            case "send":
                sentMessages.add(this);
                totalMessagesSent++;
                recordIdentifiers();
                return "Message successfully sent.";
            case "disregard":
                disregardedMessages.add(this);
                recordIdentifiers();
                return "Press 0 to delete the message.";
            case "store":
                storedMessages.add(this);
                recordIdentifiers();
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Adds this message's hash and ID to the hash and ID arrays. */
    private void recordIdentifiers() {
        messageHashes.add(messageHash);
        messageIDs.add(messageID);
    }

    // ---------- JSON storage ----------

    /** Appends this message to the JSON file. */
    public void storeMessage() {
        try {
            File file = new File(jsonFilePath);
            JSONArray messages = readJsonArray(file);
            messages.put(toJson());
            Files.write(file.toPath(), messages.toString(2).getBytes());
        } catch (IOException | JSONException e) {
            System.out.println("Could not store the message: " + e.getMessage());
        }
    }

    private JSONObject toJson() {
        JSONObject entry = new JSONObject();
        entry.put("messageID", messageID);
        entry.put("messageNumber", messageNumber);
        entry.put("messageHash", messageHash);
        entry.put("recipient", recipient);
        entry.put("message", messageText);
        return entry;
    }

    private static JSONArray readJsonArray(File file) throws IOException {
        if (file.exists() && file.length() > 0) {
            return new JSONArray(new String(Files.readAllBytes(file.toPath())));
        }
        return new JSONArray();
    }

    /** Part 3: reads the JSON file back into the storedMessages array. */
    public static void loadStoredMessagesFromJson() {
        try {
            JSONArray messages = readJsonArray(new File(jsonFilePath));
            storedMessages.clear();

            for (int i = 0; i < messages.length(); i++) {
                JSONObject entry = messages.getJSONObject(i);
                Message loaded = new Message(
                        entry.getString("recipient"),
                        entry.getString("message"),
                        entry.optInt("messageNumber", i),
                        entry.getString("messageID"));
                storedMessages.add(loaded);

                if (!messageIDs.contains(loaded.messageID)) {
                    messageIDs.add(loaded.messageID);
                }
                if (!messageHashes.contains(loaded.messageHash)) {
                    messageHashes.add(loaded.messageHash);
                }
            }
        } catch (IOException | JSONException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
        }
    }

    /** Rewrites the JSON file so it matches the storedMessages array. */
    private static void rewriteJsonFile() {
        try {
            JSONArray messages = new JSONArray();
            for (Message m : storedMessages) {
                messages.put(m.toJson());
            }
            Files.write(new File(jsonFilePath).toPath(), messages.toString(2).getBytes());
        } catch (IOException e) {
            System.out.println("Could not update the JSON file: " + e.getMessage());
        }
    }

    // ---------- Displaying messages ----------

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
        return formatAll(sentMessages);
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    private static String formatAll(List<Message> messages) {
        StringBuilder all = new StringBuilder();
        for (Message m : messages) {
            all.append(m.getDetails()).append(System.lineSeparator())
               .append("---").append(System.lineSeparator());
        }
        return all.toString();
    }

    // ---------- Part 3: array access ----------

    public static List<Message> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return Collections.unmodifiableList(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        return Collections.unmodifiableList(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return Collections.unmodifiableList(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return Collections.unmodifiableList(messageIDs);
    }

    // ---------- Part 3: stored message features ----------

    /** (a) Sender and recipient of all stored messages. */
    public static String displayStoredSendersAndRecipients(String senderUsername) {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        StringBuilder result = new StringBuilder();
        for (Message m : storedMessages) {
            result.append("Sender: ").append(senderUsername)
                  .append(", Recipient: ").append(m.recipient)
                  .append(System.lineSeparator());
        }
        return result.toString();
    }

    /** (b) The longest stored message. */
    public static String displayLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "There are no stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest.messageText;
    }

    /** (c) Search by message ID; shows the recipient and the message. */
    public static String searchByMessageID(String id) {
        List<Message> everything = new ArrayList<>();
        everything.addAll(sentMessages);
        everything.addAll(disregardedMessages);
        everything.addAll(storedMessages);

        for (Message m : everything) {
            if (m.messageID.equals(id)) {
                return "Recipient: " + m.recipient + ", Message: " + m.messageText;
            }
        }
        return "No message found with ID: " + id;
    }

    /** (d) All sent or stored messages for one recipient. */
    // Searches the sent and stored arrays for every message to this recipient
    public static String searchMessagesByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipient)) {
                found.add(m.messageText);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipient)) {
                found.add(m.messageText);
            }
        }
        if (found.isEmpty()) {
            return "No messages found for recipient: " + recipient;
        }
        return String.join(System.lineSeparator(), found);
    }

    /** (e) Deletes a message using its hash (from whichever array holds it). */
    public static String deleteMessageByHash(String hash) {
        List<List<Message>> allArrays = List.of(sentMessages, disregardedMessages, storedMessages);

        for (List<Message> array : allArrays) {
            for (Message m : array) {
                if (m.messageHash.equals(hash)) {
                    boolean wasStored = (array == storedMessages);
                    array.remove(m);
                    messageHashes.remove(hash);
                    messageIDs.remove(m.messageID);
                    if (wasStored) {
                        rewriteJsonFile();
                    }
                    return "Message: \"" + m.messageText + "\" successfully deleted.";
                }
            }
        }
        return "No message found with hash: " + hash;
    }

    /** (f) Report of all sent messages: ID, hash, recipient, message. */
    public static String displayReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to report.";
        }
        return formatAll(sentMessages);
    }

    /** (f) Report of all stored messages: ID, hash, recipient, message. */
    public static String displayStoredReport() {
        if (storedMessages.isEmpty()) {
            return "No stored messages to report.";
        }
        return formatAll(storedMessages);
    }

    // ---------- Test helpers ----------

    /** Points the JSON file somewhere else so tests never touch real data. */
    static void setJsonFilePathForTests(String path) {
        jsonFilePath = path;
    }

    /** Clears every array so unit tests start fresh (does not touch the file). */
    static void resetForTests() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}
