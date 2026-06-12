package com.mycompany.Mavenproject1 ;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Mavenproject1 {
    
    // The stored user data
    private String storedFirstName;
    private String storedLastName;
    private String storedUsername;
    private String storedPassword;
    private String storedCellPhone;
    
    // Global trackers for messages
    private static int totalMessagesSent = 0;
    private static ArrayList<String> allSentMessages = new ArrayList<>();
    
    // ==========================================
    // MESSAGE CLASS (Nested)
    // ==========================================
    public static class Message {
        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        private static Scanner msgScanner = new Scanner(System.in);

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

        // Method Name: sentMessage() 
        public String sentMessage() {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Discard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Your choice: ");
            String choice = msgScanner.nextLine();

            switch (choice) {
                case "1":
                   Mavenproject1 .totalMessagesSent++;
                   Mavenproject1 .allSentMessages.add(
                        "ID: " + messageID + " | Hash: " + messageHash +
                        " | To: " + recipient + " | Msg: " + messageText
                    );
                    return "Message successfully sent.";

                case "2":
                    return "Message discarded.";

                case "3":
                    storeMessage(); 
                    return "Message successfully stored.";

                default:
                    return "Invalid choice. Message discarded.";
            }
        }

        // Method Name: storeMessage()
        public void storeMessage() {
            String json =
                "{\n" +
                "  \"messageID\": \"" + messageID + "\",\n" +
                "  \"messageNumber\": " + messageNumber + ",\n" +
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

        // Loops through every sent message and returns them all as one String
        public static String printMessages() {
            if (allSentMessages.isEmpty()) {
                return "No messages were sent this session.";
            }
            StringBuilder sb = new StringBuilder("\n===== ALL SENT MESSAGES =====\n");
            for (String msg : allSentMessages) {
                sb.append(msg).append("\n");
            }
            return sb.toString();
        }

        // Returns the total number of messages actually sent
        public static int returnTotalMessages() {
            return totalMessagesSent;
        }

        // Setters & Getters
        public void setRecipient(String r)   { recipient = r; }
        public void setMessageText(String m) { messageText = m; }
        public String getMessageID()         { return messageID; }
        public String getMessageHash()       { return messageHash; }
        public String getRecipient()         { return recipient; }
        public String getMessageText()       { return messageText; }
    }
    
    // ==========================================
    // USER MANAGEMENT METHODS
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
    
    // ==========================================
    // QUICKCHAT MESSAGING METHODS
    // ==========================================
    
    // Display QuickChat menu
    private static void displayQuickChatMenu() {
        System.out.println("\n==============================");
        System.out.println("QUICKCHAT MAIN MENU");
        System.out.println("==============================");
        System.out.println("1. Send Messages");
        System.out.println("2. Show recently sent messages");
        System.out.println("3. Quit");
        System.out.print("Enter your choice: ");
    }
    
    // Run QuickChat after successful login
    private void runQuickChat() {
        Scanner scanner = new Scanner(System.in);
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
                    
                case "3": // QUIT
                    System.out.println("\nThank you for using QuickChat!");
                    System.out.println("Session Summary:");
                    System.out.println("- Total messages sent: " + Message.returnTotalMessages());
                    System.out.println("- Messages processed: " + msgCount + " of " + numMessages);
                    chatRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }
    
    // ==========================================
    // MAIN METHOD
    // ==========================================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
      Mavenproject1  app = new Mavenproject1 ();
        
        // Display welcome message
        System.out.println("========================================");
        System.out.println("     WELCOME TO QUICKCHAT SYSTEM");
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
            scanner.close();
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
            scanner.close();
            return;
        }
        
        // Start QuickChat messaging feature
        System.out.println("\nLogin successful! Starting QuickChat...");
        app.runQuickChat();
    }
}