package quickchat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java
 *
 * Unit tests for Part 2, using the test data supplied in the PoE brief.
 */
public class MessageTest {

    private static final String MSG1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() {
        Message.resetForTests();
    }

    @AfterEach
    void cleanUp() {
        new File("storedMessages.json").delete();
    }

    // ---------- Message length ----------

    @Test
    void testMessageLengthSuccess() {
        Message m = new Message("+27718693002", MSG1, 0);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    void testMessageLengthFailure() {
        Message m = new Message("+27718693002", "a".repeat(260), 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", m.checkMessageLength());
    }

    // ---------- Recipient ----------

    @Test
    void testRecipientCorrectlyFormatted() {
        Message m = new Message("+27718693002", MSG1, 0);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    void testRecipientIncorrectlyFormatted() {
        Message m = new Message("08575975889", MSG2, 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    // ---------- Message hash (the ID is random, so compare the parts) ----------

    @Test
    void testMessageHashMessage1() {
        Message m = new Message("+27718693002", MSG1, 0);
        String expected = m.getMessageID().substring(0, 2) + ":0:HITONIGHT";
        assertEquals(expected, m.createMessageHash());
    }

    @Test
    void testMessageHashesInLoop() {
        String[] messages = {MSG1, MSG2};
        String[] expectedEnds = {":0:HITONIGHT", ":1:HIPAYMENT"};

        for (int i = 0; i < messages.length; i++) {
            Message m = new Message("+27718693002", messages[i], i);
            assertTrue(m.createMessageHash().endsWith(expectedEnds[i]));
        }
    }

    // ---------- Message ID ----------

    @Test
    void testMessageIDCreated() {
        Message m = new Message("+27718693002", MSG1, 0);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
    }

    // ---------- Send / disregard / store ----------

    @Test
    void testSendMessage() {
        Message m = new Message("+27718693002", MSG1, 0);
        assertEquals("Message successfully sent.", m.sentMessage("send"));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void testDisregardMessage() {
        Message m = new Message("08575975889", MSG2, 1);
        assertEquals("Press 0 to delete the message.", m.sentMessage("disregard"));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void testStoreMessage() {
        Message m = new Message("+27718693002", MSG1, 0);
        assertEquals("Message successfully stored.", m.sentMessage("store"));
        assertTrue(new File("storedMessages.json").exists());
    }

    // ---------- Totals / printing ----------

    @Test
    void testTotalMessagesAndPrint() {
        new Message("+27718693002", MSG1, 0).sentMessage("send");
        new Message("08575975889", MSG2, 1).sentMessage("disregard");

        assertEquals(1, Message.returnTotalMessages());
        assertTrue(Message.printMessages().contains(MSG1));
        assertFalse(Message.printMessages().contains(MSG2));
    }
}
