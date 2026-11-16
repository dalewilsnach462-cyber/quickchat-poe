package quickchat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StoredMessagesTest.java
 *
 * Unit tests for Part 3 (arrays, searching, deleting and the report),
 * using the five test messages supplied in the PoE brief.
 */
public class StoredMessagesTest {

    private static final String TEST_JSON = "test-part3-stored.json";

    private static final String RECIPIENT_1 = "+27834557896";
    private static final String TEXT_1 = "Did you get the cake?";

    private static final String RECIPIENT_2 = "+27838884567";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";

    private static final String RECIPIENT_3 = "+27834484567";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";

    private static final String RECIPIENT_4 = "0838884567";
    private static final String TEXT_4 = "It is dinner time !";

    private static final String RECIPIENT_5 = "+27838884567";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @BeforeEach
    void setUp() {
        Message.setJsonFilePathForTests(TEST_JSON);
        new File(TEST_JSON).delete();
        Message.resetForTests();

        message1 = new Message(RECIPIENT_1, TEXT_1, 0);
        message1.sentMessage("send");

        message2 = new Message(RECIPIENT_2, TEXT_2, 1);
        message2.sentMessage("store");

        message3 = new Message(RECIPIENT_3, TEXT_3, 2);
        message3.sentMessage("disregard");

        message4 = new Message(RECIPIENT_4, TEXT_4, 3);
        message4.sentMessage("send");

        message5 = new Message(RECIPIENT_5, TEXT_5, 4);
        message5.sentMessage("store");
    }

    @AfterEach
    void cleanUp() {
        new File(TEST_JSON).delete();
    }

    // ---------- Arrays populated ----------

    @Test
    void testSentMessagesArrayPopulated() {
        List<String> texts = new ArrayList<>();
        for (Message m : Message.getSentMessages()) {
            texts.add(m.getMessageText());
        }
        assertEquals(List.of(TEXT_1, TEXT_4), texts);
    }

    @Test
    void testDisregardedMessagesArrayPopulated() {
        assertEquals(1, Message.getDisregardedMessages().size());
        assertEquals(TEXT_3, Message.getDisregardedMessages().get(0).getMessageText());
    }

    @Test
    void testHashAndIdArraysPopulated() {
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(5, Message.getMessageIDs().size());
        assertTrue(Message.getMessageHashes().contains(message2.getMessageHash()));
        assertTrue(Message.getMessageIDs().contains(message4.getMessageID()));
    }

    @Test
    void testHashIgnoresLoneExclamationMark() {
        // "It is dinner time !" -> first word IT, last real word TIME
        assertTrue(message4.getMessageHash().endsWith(":3:ITTIME"));
    }

    // ---------- Longest stored message ----------

    @Test
    void testDisplayLongestStoredMessage() {
        assertEquals(TEXT_2, Message.displayLongestStoredMessage());
    }

    // ---------- Search by message ID ----------

    @Test
    void testSearchByMessageID() {
        String result = Message.searchByMessageID(message4.getMessageID());
        assertTrue(result.contains(TEXT_4));
        assertTrue(result.contains(RECIPIENT_4));
    }

    @Test
    void testSearchByMessageIDNotFound() {
        assertEquals("No message found with ID: 0000000000", Message.searchByMessageID("0000000000"));
    }

    // ---------- Search by recipient ----------

    @Test
    void testSearchMessagesByRecipient() {
        String expected = TEXT_2 + System.lineSeparator() + TEXT_5;
        assertEquals(expected, Message.searchMessagesByRecipient("+27838884567"));
    }

    // ---------- Delete by hash ----------

    @Test
    void testDeleteMessageByHash() throws IOException {
        String result = Message.deleteMessageByHash(message2.getMessageHash());

        assertEquals("Message: \"" + TEXT_2 + "\" successfully deleted.", result);
        assertEquals(1, Message.getStoredMessages().size());

        String fileContent = new String(Files.readAllBytes(new File(TEST_JSON).toPath()));
        assertFalse(fileContent.contains("Where are you?"));
        assertTrue(fileContent.contains("Ok, I am leaving without you."));
    }

    @Test
    void testDeleteMessageByHashNotFound() {
        assertEquals("No message found with hash: XX:9:NOPE", Message.deleteMessageByHash("XX:9:NOPE"));
    }

    // ---------- Report ----------

    @Test
    void testDisplayReportShowsSentMessages() {
        String report = Message.displayReport();

        assertTrue(report.contains(message1.getMessageHash()));
        assertTrue(report.contains(RECIPIENT_1));
        assertTrue(report.contains(TEXT_1));
        assertTrue(report.contains(message4.getMessageHash()));
        assertTrue(report.contains(TEXT_4));
        assertFalse(report.contains(TEXT_2));
    }

    @Test
    void testDisplayStoredReport() {
        String report = Message.displayStoredReport();
        assertTrue(report.contains(TEXT_2));
        assertTrue(report.contains(TEXT_5));
        assertFalse(report.contains(TEXT_1));
    }

    // ---------- Reading the JSON file into the array ----------

    @Test
    void testReadJsonFileIntoStoredArray() {
        Message.resetForTests();
        assertEquals(0, Message.getStoredMessages().size());

        Message.loadStoredMessagesFromJson();

        List<Message> loaded = Message.getStoredMessages();
        assertEquals(2, loaded.size());
        assertEquals(TEXT_2, loaded.get(0).getMessageText());
        assertEquals(TEXT_5, loaded.get(1).getMessageText());
        assertEquals(message2.getMessageID(), loaded.get(0).getMessageID());
    }

    // ---------- Sender and recipient listing ----------

    @Test
    void testDisplayStoredSendersAndRecipients() {
        String result = Message.displayStoredSendersAndRecipients("kyl_1");
        assertTrue(result.contains("Sender: kyl_1, Recipient: +27838884567"));
    }
}
