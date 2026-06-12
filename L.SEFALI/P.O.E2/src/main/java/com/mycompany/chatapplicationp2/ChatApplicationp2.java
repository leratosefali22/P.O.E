/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.chatapplicationp2;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

/**
 * PROG5121 PoE Part 3 - Complete QuickChat Application
 * 
 * This application allows users to:
 * 1. Register with validated username, password, and SA cell phone number
 * 2. Login with registered credentials
 * 3. Send, discard, or store messages
 * 4. View, search, delete, and report on stored messages
 * 
 * Part 3 adds: 5 arrays for tracking messages, a Stored Messages sub-menu,
 *              and 6 new methods for message management.
 * 
 * @author Student
 */
public class ChatApplicationp2 {

    // The stored user data
    private String storedFirstName;
    private String storedLastName;
    private String storedUsername;
    private String storedPassword;
    private String storedCellPhone;

    // Scanner for user input
    private static final Scanner scanner = new Scanner(System.in);

    // ==========================================
    // CLASS 1: LOGIN (Unchanged from Part 1)
    // ==========================================
    
    // Check username - Returns true if username contains '_' and is 5 characters or less
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    // Password verification - Must contain capital letter, number, special character, min 8 chars
    public boolean checkPasswordComplexity(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    // Cell phone number verification - Must start with +27 followed by 9 digits (first digit 1-9)
    public boolean checkCellPhoneNumber(String cellPhone) {
        if (cellPhone == null) return false;
        return cellPhone.matches("\\+27[1-9][0-9]{8}");
    }

    // Registering user
    public String registerUser(String firstName, String lastName, String username,
                               String password, String cellPhone) {
        // Check username
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        // Check password
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        // Check cell phone
        if (!checkCellPhoneNumber(cellPhone)) {
            return "Cell phone number incorrectly formatted or does not contain international code. Please use format: +27XXXXXXXXX";
        }

        // Save details if all correct
        storedFirstName = firstName;
        storedLastName = lastName;
        storedUsername = username;
        storedPassword = password;
        storedCellPhone = cellPhone;

        return "User registered successfully!";
    }

    // User login - Returns true if username and password match stored ones
    public boolean loginUser(String username, String password) {
        if (storedUsername == null) return false;
        return storedUsername.equals(username) && storedPassword.equals(password);
    }

    // Returns welcome message if login succeeds, otherwise error message
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + storedFirstName + " " + storedLastName + ", it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
    
    // Get full name for storing in messages
    public String getFullName() {
        return storedFirstName + " " + storedLastName;
    }

    // ==========================================
    // CLASS 2: MESSAGE
    // ==========================================
    
    /**
     * Message class handles creation, storage, and management of messages.
     * Part 3 additions:
     * - 5 static ArrayLists for tracking messages
     * - Updated sentMessage() to populate arrays
     * - loadStoredMessagesFromFile() to restore from JSON
     * - 6 new static methods for message management
     */
    public static class Message {
        // Instance variables
        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        
        // ========== PART 3: STATIC ARRAYS (Step 1) ==========
        // These 5 arrays track all messages as the user works
        public static ArrayList<String> sentMessagesArray = new ArrayList<>();
        public static ArrayList<String> disregardedMessages = new ArrayList<>();
        public static ArrayList<String> storedMessagesArray = new ArrayList<>();
        public static ArrayList<String> messageHashArray = new ArrayList<>();
        public static ArrayList<String> messageIDArray = new ArrayList<>();
        
        // Static fields for tracking
        public static int totalMessagesSent = 0;
        public static String senderName = ""; // Set after login

        // Constructor
        public Message(int messageNumber) {
            this.messageID = generateMessageID();
            this.messageNumber = messageNumber;
        }

        // Helper to generate a unique random 10-digit number
        private String generateMessageID() {
            Random rand = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(rand.nextInt(10));
            }
            return sb.toString();
        }

        // Method Name: checkMessageID()
        public boolean checkMessageID() {
            return this.messageID != null && this.messageID.length() <= 10;
        }

        // Method Name: checkRecipientCell()
        public String checkRecipientCell(String cell) {
            if (cell != null && cell.length() <= 10 && cell.startsWith("+")) {
                return "Cell number accepted.";
            }
            return "Invalid cell format. Must be max 10 chars and start with a code.";
        }

        // Method Name: createMessageHash()
        public String createMessageHash() {
            String firstTwoID = this.messageID.substring(0, 2);
            String trimmed = this.messageText.trim();
            String[] words = trimmed.split("\\s+");

            String firstWord = words[0];
            String lastWord = words[words.length - 1];

            this.messageHash = (firstTwoID + ":" + this.messageNumber + ":" + firstWord + lastWord).toUpperCase();
            return this.messageHash;
        }

        // ========== PART 3: UPDATED sentMessage() (Step 2) ==========
        // Method Name: sentMessage()
        public String sentMessage() {
            // Build the entry string with tab separators
            String entry = messageID + "\t" + messageHash + "\t" + recipient + "\t" + messageText + "\t" + senderName;
            
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Discard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    totalMessagesSent++;
                    sentMessagesArray.add(entry);
                    messageHashArray.add(messageHash);
                    messageIDArray.add(messageID);
                    return "Message successfully sent.";

                case "2":
                    disregardedMessages.add(entry);
                    return "Message discarded.";

                case "3":
                    storeMessage(); // still writes to JSON as before
                    storedMessagesArray.add(entry);
                    messageHashArray.add(messageHash);
                    messageIDArray.add(messageID);
                    return "Message successfully stored.";

                default:
                    return "Invalid choice. Message discarded.";
            }
        }

        // Method Name: storeMessage() - Updated to include sender name
        public void storeMessage() {
            String json =
                    "{\n" +
                    "  \"messageID\": \"" + messageID + "\",\n" +
                    "  \"messageNumber\": " + messageNumber + ",\n" +
                    "  \"sender\": \"" + senderName + "\",\n" +
                    "  \"recipient\": \"" + recipient + "\",\n" +
                    "  \"message\": \"" + messageText + "\",\n" +
                    "  \"hash\": \"" + messageHash + "\"\n" +
                    "},\n";

            try (FileWriter fw = new FileWriter("stored_messages.json", true)) {
                fw.write(json);
                System.out.println("Saved to stored_messages.json");
            } catch (IOException e) {
                System.out.println("Could not save: " + e.getMessage());
            }
        }

        // ========== PART 3: JSON Loading Method (Step 3) ==========
        /**
         * Loads stored messages from JSON file and rebuilds storedMessagesArray.
         * Reference: JSON reading technique adapted from
         * https://www.baeldung.com/java-read-json-file
         */
        public static void loadStoredMessagesFromFile() {
            storedMessagesArray.clear();
            java.io.File file = new java.io.File("stored_messages.json");
            if (!file.exists()) {
                System.out.println("No stored messages file found.");
                return;
            }
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                String id = "", hash = "", recip = "", msg = "", sender = "";
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.contains("\"messageID\"")) id = jsonValue(line);
                    else if (line.contains("\"sender\"")) sender = jsonValue(line);
                    else if (line.contains("\"recipient\"")) recip = jsonValue(line);
                    else if (line.contains("\"message\"")) msg = jsonValue(line);
                    else if (line.contains("\"hash\"")) hash = jsonValue(line);
                    else if (line.startsWith("}") && !id.isEmpty()) {
                        storedMessagesArray.add(id + "\t" + hash + "\t" + recip + "\t" + msg + "\t" + sender);
                        if (!messageIDArray.contains(id)) messageIDArray.add(id);
                        if (!messageHashArray.contains(hash)) messageHashArray.add(hash);
                        id = ""; hash = ""; recip = ""; msg = ""; sender = "";
                    }
                }
            } catch (java.io.IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        /**
         * Helper: extracts the value from a JSON line like "key": "value"
         * Finds the colon after the key's closing quote to handle hash values
         * that contain their own colons (e.g. "10:2:WHERETIME.").
         */
        private static String jsonValue(String line) {
            int keyClose = line.indexOf('"', line.indexOf('"') + 1);
            int colon = line.indexOf(':', keyClose);
            if (colon < 0) return "";
            String val = line.substring(colon + 1).trim();
            if (val.endsWith(",")) val = val.substring(0, val.length() - 1).trim();
            if (val.startsWith("\"") && val.endsWith("\"")) return val.substring(1, val.length() - 1);
            return val;
        }

        // ========== PART 3: SIX REPORT METHODS (Step 4) ==========
        
        // a) Display sender and recipient of stored messages
        public static String displayStoredSenderRecipient() {
            if (storedMessagesArray.isEmpty()) return "No stored messages found.";
            StringBuilder sb = new StringBuilder("\n--- STORED MESSAGES: SENDER & RECIPIENT ---\n");
            for (String raw : storedMessagesArray) {
                String[] p = raw.split("\t", -1);
                String sender = (p.length > 4 && !p[4].isEmpty()) ? p[4] : senderName;
                sb.append("Sender: ").append(sender)
                  .append(" | Recipient: ").append(p[2]).append("\n");
            }
            return sb.toString();
        }

        // b) Display the longest message (from sent, stored, or disregarded arrays)
        public static String getLongestMessage() {
            String longest = "";
            java.util.List<ArrayList<String>> all = 
                java.util.Arrays.asList(sentMessagesArray, storedMessagesArray, disregardedMessages);
            for (ArrayList<String> arr : all) {
                for (String raw : arr) {
                    String[] p = raw.split("\t", -1);
                    String text = p.length > 3 ? p[3] : "";
                    if (text.length() > longest.length()) longest = text;
                }
            }
            return longest.isEmpty() ? "No messages found." : longest;
        }

        // c) Search by message ID (searches sent and stored arrays)
        public static String searchMessageByID(String searchID) {
            java.util.List<ArrayList<String>> all = 
                java.util.Arrays.asList(sentMessagesArray, storedMessagesArray);
            for (ArrayList<String> arr : all) {
                for (String raw : arr) {
                    String[] p = raw.split("\t", -1);
                    if (p[0].equals(searchID)) {
                        return "Recipient: " + p[2] + "\nMessage : " + p[3];
                    }
                }
            }
            return "Message ID not found.";
        }

        // d) Search messages by recipient (returns all matching entries)
        public static String searchMessagesByRecipient(String searchRecipient) {
            StringBuilder sb = new StringBuilder();
            java.util.List<ArrayList<String>> all = 
                java.util.Arrays.asList(sentMessagesArray, storedMessagesArray);
            for (ArrayList<String> arr : all) {
                for (String raw : arr) {
                    String[] p = raw.split("\t", -1);
                    if (p[2].equals(searchRecipient)) {
                        sb.append(p[3]).append("\n");
                    }
                }
            }
            return sb.length() > 0 ? sb.toString().trim() : "No messages found for this recipient.";
        }

        // e) Delete a message by its hash (uses Iterator for safe deletion)
        public static String deleteMessageByHash(String hash) {
            java.util.List<ArrayList<String>> all = 
                java.util.Arrays.asList(storedMessagesArray, sentMessagesArray);
            for (ArrayList<String> arr : all) {
                java.util.Iterator<String> it = arr.iterator();
                while (it.hasNext()) {
                    String[] p = it.next().split("\t", -1);
                    if (p[1].equalsIgnoreCase(hash)) {
                        String text = p[3];
                        it.remove();
                        messageHashArray.remove(p[1]);
                        return "Message: \"" + text + "\" successfully deleted.";
                    }
                }
            }
            return "Message hash not found.";
        }

        // f) Full message report
        public static String displayFullReport() {
            java.util.List<ArrayList<String>> all = 
                java.util.Arrays.asList(sentMessagesArray, storedMessagesArray);
            boolean hasAny = false;
            for (ArrayList<String> a : all) {
                if (!a.isEmpty()) {
                    hasAny = true;
                    break;
                }
            }
            if (!hasAny) return "No messages to display.";
            
            StringBuilder sb = new StringBuilder("\n========== FULL MESSAGE REPORT ==========\n");
            for (ArrayList<String> arr : all) {
                for (String raw : arr) {
                    String[] p = raw.split("\t", -1);
                    sb.append("Hash : ").append(p[1]).append("\n")
                      .append("Recipient : ").append(p[2]).append("\n")
                      .append("Message : ").append(p[3]).append("\n")
                      .append("------------------------------------------\n");
                }
            }
            return sb.toString();
        }
        
        // ========== LEGACY METHODS (from Part 2, kept for compatibility) ==========
        
        // Prints all sent messages (legacy method)
        public static String printMessages() {
            if (sentMessagesArray.isEmpty()) {
                return "No messages were sent this session.";
            }
            StringBuilder sb = new StringBuilder("\n===== ALL SENT MESSAGES =====\n");
            for (String raw : sentMessagesArray) {
                String[] p = raw.split("\t", -1);
                sb.append("ID: ").append(p[0])
                  .append(" | Hash: ").append(p[1])
                  .append(" | To: ").append(p[2])
                  .append(" | Msg: ").append(p[3]).append("\n");
            }
            return sb.toString();
        }

        // Returns total messages sent (legacy method)
        public static int returnTotalMessages() {
            return totalMessagesSent;
        }

        // Setters for instance variables
        public void setRecipient(String r) { recipient = r; }
        public void setMessageText(String m) { messageText = m; }
        public String getMessageID() { return messageID; }
        public String getMessageHash() { return messageHash; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
    }

    // ==========================================
    // CLASS 3: STOREDMESSAGESMENU (Step 5)
    // ==========================================
    
    /**
     * StoredMessagesMenu handles the sub-menu for message management.
     * This class is entirely new for Part 3.
     */
    static class StoredMessagesMenu {
        static void show(Scanner scanner) {
            Message.loadStoredMessagesFromFile(); // refresh from JSON on every entry
            
            boolean running = true;
            while (running) {
                System.out.println("\n--- STORED MESSAGES ---");
                System.out.println("a) Sender & recipient of all stored messages");
                System.out.println("b) Longest message");
                System.out.println("c) Search by message ID");
                System.out.println("d) Search by recipient");
                System.out.println("e) Delete message by hash");
                System.out.println("f) Full message report");
                System.out.println("q) Back to QuickChat menu");
                System.out.print("Choice: ");
                
                String choice = scanner.nextLine().toLowerCase().trim();
                
                switch (choice) {
                    case "a":
                        System.out.println(Message.displayStoredSenderRecipient());
                        break;
                    case "b":
                        System.out.println("Longest message: " + Message.getLongestMessage());
                        break;
                    case "c":
                        System.out.print("Enter message ID: ");
                        System.out.println(Message.searchMessageByID(scanner.nextLine().trim()));
                        break;
                    case "d":
                        System.out.print("Enter recipient number: ");
                        System.out.println(Message.searchMessagesByRecipient(scanner.nextLine().trim()));
                        break;
                    case "e":
                        System.out.print("Enter message hash: ");
                        System.out.println(Message.deleteMessageByHash(scanner.nextLine().trim()));
                        break;
                    case "f":
                        System.out.println(Message.displayFullReport());
                        break;
                    case "q":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid. Enter a-f or q.");
                }
            }
        }
    }

    // ==========================================
    // MAIN CLASS (Step 6)
    // ==========================================
    
    // Display QuickChat menu
    private static void displayQuickChatMenu() {
        System.out.println("\n--- QUICKCHAT MAIN MENU ---");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Stored Messages");  // NEW for Part 3
        System.out.println("4) Quit");
        System.out.print("Enter your choice: ");
    }

    // Run QuickChat after successful login
    private void runQuickChat() {
        System.out.println("\nWelcome to QuickChat");

        // Ask how many messages the user wants to send
        int numMessages = 0;
        while (numMessages <= 0) {
            System.out.print("How many messages do you wish to send? ");
            try {
                numMessages = Integer.parseInt(scanner.nextLine());
                if (numMessages <= 0) {
                    System.out.println("Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        boolean chatRunning = true;
        int msgCount = 0;

        while (chatRunning) {
            displayQuickChatMenu();
            String chatChoice = scanner.nextLine();

            switch (chatChoice) {
                case "1": // SEND MESSAGES
                    if (msgCount >= numMessages) {
                        System.out.println("You have reached your message limit of " + numMessages + " messages.");
                        break;
                    }

                    while (msgCount < numMessages) {
                        msgCount++;
                        System.out.println("\n--- MESSAGE " + msgCount + " of " + numMessages + " ---");

                        // Create a new Message object
                        Message msg = new Message(msgCount);

                        // Get recipient (loop until valid)
                        String cell;
                        while (true) {
                            System.out.print("Recipient cell number (format +27, max 10 chars): ");
                            cell = scanner.nextLine();
                            String cellCheck = msg.checkRecipientCell(cell);
                            System.out.println(cellCheck);
                            if (cellCheck.equals("Cell number accepted.")) break;
                        }
                        msg.setRecipient(cell);

                        // Get message text (loop until valid - max 250 chars)
                        String text;
                        while (true) {
                            System.out.print("Message (max 250 characters): ");
                            text = scanner.nextLine();
                            if (text.length() > 250) {
                                System.out.println("Please enter a message of less than 250 characters.");
                            } else if (text.trim().isEmpty()) {
                                System.out.println("Message cannot be empty.");
                            } else {
                                System.out.println("Message received.");
                                break;
                            }
                        }
                        msg.setMessageText(text);

                        // Build the hash
                        String hash = msg.createMessageHash();

                        // Show message details
                        System.out.println("\n--- MESSAGE DETAILS ---");
                        System.out.println("Message ID: " + msg.getMessageID());
                        System.out.println("Message Hash: " + hash);
                        System.out.println("Recipient: " + msg.getRecipient());
                        System.out.println("Message: " + msg.getMessageText());

                        // Send / Discard / Store
                        String result = msg.sentMessage();
                        System.out.println(result);

                        if (msgCount >= numMessages) {
                            System.out.println("\nYou have reached your message limit of " + numMessages + " messages.");
                            break;
                        }

                        if (msgCount < numMessages) {
                            System.out.print("\nDo you want to send another message? (yes/no): ");
                            String continueChoice = scanner.nextLine().toLowerCase();
                            if (!continueChoice.equals("yes")) {
                                break;
                            }
                        }
                    }
                    break;

                case "2": // SHOW RECENTLY SENT MESSAGES
                    System.out.println(Message.printMessages());
                    break;

                case "3": // STORED MESSAGES (NEW for Part 3)
                    StoredMessagesMenu.show(scanner);
                    break;

                case "4": // QUIT
                    System.out.println("\nThank you for using QuickChat!");
                    System.out.println("Session Summary:");
                    System.out.println("- Total messages sent: " + Message.returnTotalMessages());
                    System.out.println("- Messages processed: " + msgCount + " of " + numMessages);
                    chatRunning = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1, 2, 3, or 4.");
            }
        }
    }

    // ==========================================
    // MAIN METHOD - Entry point
    // ==========================================
    public static void main(String[] args) {
        ChatApplicationp2 app = new ChatApplicationp2();

        // Display welcome message
        System.out.println("========================================");
        System.out.println("     WELCOME TO ChatApplication");
        System.out.println("========================================");

        // REGISTRATION
        System.out.println("\n----- REGISTRATION -----");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter your username (must contain '_' and be <= 5 characters): ");
        String username = scanner.nextLine();

        System.out.print("Enter your password (8+ chars, one capital, one number, one special): ");
        String password = scanner.nextLine();

        System.out.print("Enter your SA cell phone number (format +27XXXXXXXXX): ");
        String cellPhone = scanner.nextLine();

        String regResult = app.registerUser(firstName, lastName, username, password, cellPhone);
        System.out.println(regResult);

        if (!regResult.equals("User registered successfully!")) {
            System.out.println("Registration failed. Please try again.");
            return;
        }

        System.out.println("\nRegistration successful! You can now log in.");

        // LOGIN
        System.out.println("\n----- LOGIN -----");
        System.out.print("Enter your username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = scanner.nextLine();

        String loginStatus = app.returnLoginStatus(loginUsername, loginPassword);
        System.out.println(loginStatus);

        if (!app.loginUser(loginUsername, loginPassword)) {
            System.out.println("Login failed. Exiting QuickChat...");
            return;
        }

        // ========== PART 3: Set sender name after login (Step 6, Change 1) ==========
        Message.senderName = app.getFullName();

        // Load stored messages from JSON file
        Message.loadStoredMessagesFromFile();

        // Start QuickChat messaging feature
        System.out.println("\nLogin successful! Starting QuickChat...");
        app.runQuickChat();

        scanner.close();
    }
}