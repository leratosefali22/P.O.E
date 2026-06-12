/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.chatapplicationp2;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for PROG5121 PoE Part 3.
 * Tests the 6 stored message methods using the exact test data from the spec.
 * 
 * @author Student
 */
public class MessageTest {

    @BeforeEach
    void setUp() {
        // Clear all static arrays before each test
        ChatApplicationp2.Message.sentMessagesArray.clear();
        ChatApplicationp2.Message.disregardedMessages.clear();
        ChatApplicationp2.Message.storedMessagesArray.clear();
        ChatApplicationp2.Message.messageHashArray.clear();
        ChatApplicationp2.Message.messageIDArray.clear();
        ChatApplicationp2.Message.totalMessagesSent = 0;
        
        // Set sender name for all messages (simulate logged-in user)
        ChatApplicationp2.Message.senderName = "Test User";
        
        // Populate arrays with the 5 test messages
        loadTestData();
    }
    
    /**
     * Loads the exact test data from the specification.
     * Format: "messageID\tmessageHash\trecipient\tmessageText\tsenderName"
     * 
     * For Message 4, the spec says "Developer" is the sender,
     * so we use senderName = "Developer" for that entry.
     */
    private void loadTestData() {
        // Message 1: Sent
        String msg1 = "1111111111\tHASH1\t+27834557896\tDid you get the cake?\tTest User";
        ChatApplicationp2.Message.sentMessagesArray.add(msg1);
        ChatApplicationp2.Message.messageIDArray.add("1111111111");
        ChatApplicationp2.Message.messageHashArray.add("HASH1");
        ChatApplicationp2.Message.totalMessagesSent++;
        
        // Message 2: Stored (longest)
        String msg2 = "2222222222\tHASH2\t+27838884567\tWhere are you? You are late! I have asked you to be on time.\tTest User";
        ChatApplicationp2.Message.storedMessagesArray.add(msg2);
        ChatApplicationp2.Message.messageIDArray.add("2222222222");
        ChatApplicationp2.Message.messageHashArray.add("HASH2");
        
        // Message 3: Disregard
        String msg3 = "3333333333\tHASH3\t+27834484567\tYohoooo, I am at your gate.\tTest User";
        ChatApplicationp2.Message.disregardedMessages.add(msg3);
        ChatApplicationp2.Message.messageIDArray.add("3333333333");
        ChatApplicationp2.Message.messageHashArray.add("HASH3");
        
        // Message 4: Sent (but with Developer as sender)
        String msg4 = "0838884567\tHASH4\t+27831234567\tIt is dinner time!\tDeveloper";
        ChatApplicationp2.Message.sentMessagesArray.add(msg4);
        ChatApplicationp2.Message.messageIDArray.add("0838884567");
        ChatApplicationp2.Message.messageHashArray.add("HASH4");
        ChatApplicationp2.Message.totalMessagesSent++;
        
        // Message 5: Stored
        String msg5 = "5555555555\tHASH5\t+27838884567\tOk, I am leaving without you.\tTest User";
        ChatApplicationp2.Message.storedMessagesArray.add(msg5);
        ChatApplicationp2.Message.messageIDArray.add("5555555555");
        ChatApplicationp2.Message.messageHashArray.add("HASH5");
    }
    
    // ========== TEST 1: Sent Messages array correctly populated ==========
    @Test
    void testSentMessagesArrayContainsExpectedMessages() {
        // Expected sent messages: Message 1 and Message 4
        String expectedMsg1 = "Did you get the cake?";
        String expectedMsg4 = "It is dinner time!";
        
        boolean found1 = false, found4 = false;
        for (String raw : ChatApplicationp2.Message.sentMessagesArray) {
            String[] parts = raw.split("\t");
            String text = parts[3];
            if (text.equals(expectedMsg1)) found1 = true;
            if (text.equals(expectedMsg4)) found4 = true;
        }
        
        assertTrue(found1, "Sent messages array should contain: " + expectedMsg1);
        assertTrue(found4, "Sent messages array should contain: " + expectedMsg4);
    }
    
    // ========== TEST 2: Display the longest message ==========
    @Test
    void testLongestMessage() {
        String longest = ChatApplicationp2.Message.getLongestMessage();
        String expected = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expected, longest, "Longest message should be Message 2");
    }
    
    // ========== TEST 3: Search for messageID ==========
    @Test
    void testSearchMessageByID() {
        // Search for Message 4 ID "0838884567"
        String result = ChatApplicationp2.Message.searchMessageByID("0838884567");
        assertTrue(result.contains("It is dinner time!"), 
                   "Search by ID should return 'It is dinner time!'");
    }
    
    // ========== TEST 4: Search all messages to a particular recipient ==========
    @Test
    void testSearchMessagesByRecipient() {
        // Recipient +27838884567 should return Message 2 and Message 5
        String result = ChatApplicationp2.Message.searchMessagesByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                   "Should find Message 2 for recipient +27838884567");
        assertTrue(result.contains("Ok, I am leaving without you."),
                   "Should find Message 5 for recipient +27838884567");
    }
    
    // ========== TEST 5: Delete a message using a message hash ==========
    @Test
    void testDeleteMessageByHash() {
        // Delete Message 2 using its hash "HASH2"
        String deleteResult = ChatApplicationp2.Message.deleteMessageByHash("HASH2");
        assertTrue(deleteResult.contains("successfully deleted"),
                   "Delete should confirm deletion");
        
        // Verify it's gone from storedMessagesArray
        boolean stillExists = false;
        for (String raw : ChatApplicationp2.Message.storedMessagesArray) {
            if (raw.contains("Where are you? You are late!")) {
                stillExists = true;
                break;
            }
        }
        assertFalse(stillExists, "Message 2 should no longer exist in storedMessagesArray");
        
        // Also verify hash removed from messageHashArray
        assertFalse(ChatApplicationp2.Message.messageHashArray.contains("HASH2"),
                    "Message hash should be removed from messageHashArray");
    }
    
    // ========== TEST 6: Display Report ==========
    @Test
    void testDisplayFullReport() {
        String report = ChatApplicationp2.Message.displayFullReport();
        
        // Report should contain all sent and stored messages (4 messages: Message1,2,4,5)
        assertTrue(report.contains("Did you get the cake?"), 
                   "Report should contain Message 1");
        assertTrue(report.contains("Where are you? You are late! I have asked you to be on time."),
                   "Report should contain Message 2");
        assertTrue(report.contains("It is dinner time!"),
                   "Report should contain Message 4");
        assertTrue(report.contains("Ok, I am leaving without you."),
                   "Report should contain Message 5");
        
        // Report should NOT contain disregarded messages (Message 3)
        assertFalse(report.contains("Yohoooo, I am at your gate."),
                    "Report should NOT include disregarded messages");
        
        // Check report structure: contains "Hash :", "Recipient :", "Message :"
        assertTrue(report.contains("Hash :") && report.contains("Recipient :") && report.contains("Message :"),
                   "Report should display hash, recipient, and message for each entry");
    }
}