package javaapplications; // FILE CODE IS LOCATED IN

import java.util.Scanner; // SCANER FOR USER INPUT
import java.util.regex.Pattern;

/**
 * Login class that handles all user registration and login logic.
 * Contains the 6 required methods for POE Part 1.
 */
class Login {
    // Stored user details after successful registration
    private String storedFirstName;
    private String storedLastName;
    private String storedUsername;
    private String storedPassword;
    private String storedPhone;

    // +++++++++++++++++++++++1. USERNAME VALIDATION ++++++++++++++++++++++++
    /**
     * Checks if username has an underscore (_) and is 5 characters or less.
     * @param username the username to check
     * @return true if valid, false otherwise
     */
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // ==================== 2. PASSWORD VALIDATION ====================
    /**
     * Checks password strength:
     * - At least 8 characters long
     * - Contains at least one capital letter
     * - Contains at least one number
     * - Contains at least one special character (not A-Z, a-z, or 0-9)
     * @param password the password to check
     * @return true if meets all criteria, false otherwise
     */
    public boolean checkPasswordComplexity(String password) {
        boolean longEnough = password.length() >= 8;
        boolean hasCapital = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");
        return longEnough && hasCapital && hasNumber && hasSpecial;
    }

    // ~~~~~~~~~~~~~~~~ 3. CELL PHONE VALIDATION ~~~~~~~~~~~~~~~~
    /**
     * Validates South African cell number format.
     * Must start with +27 and be followed by exactly 9 digits.
     * Regex source: Common SA phone validation pattern, adapted from examples at regexlib.com
     * @param phone the phone number to check
     * @return true if matches +27 followed by 9 digits, false otherwise
     */
    public boolean checkCellPhoneNumber(String phone) {
        // Pattern: starts with +27, then exactly 9 digits (total length = 12)
        String regex = "^\\+27[0-9]{9}$";
        return Pattern.matches(regex, phone);
    }

    // ~~~~~~~~~~~~~~~~~~~~ 4. REGISTRATION LOGIC ~~~~~~~~~~~~~~~~~~~~~
    /**
     * Attempts to register a new user with the given details.
     * Validates each field using the check methods above.
     * @param firstName user's first name
     * @param lastName user's last name
     * @param username desired username
     * @param password desired password
     * @param phone cell phone number
     * @return success message if all fields valid, otherwise specific error message
     */
    public String registerUser(String firstName, String lastName, String username, String password, String phone) {
        // Check username
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        // Check password
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        // Check phone number
        if (!checkCellPhoneNumber(phone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        
        // All checks passed — save the user's details
        this.storedFirstName = firstName;
        this.storedLastName = lastName;
        this.storedUsername = username;
        this.storedPassword = password;
        this.storedPhone = phone;
        
        return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.\nRegistration successful!";
    }

    // ~~~~~~~~~~~~~~~~~~~~~ 5. LOGIN VERIFICATION~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Checks if the provided credentials match the stored registered user.
     * @param username username entered at login
     * @param password password entered at login
     * @return true if both match the stored values, false otherwise
     */
    public boolean loginUser(String username, String password) {
        // Make sure a user has registered first
        if (storedUsername == null) {
            return false;
        }
        return storedUsername.equals(username) && storedPassword.equals(password);
    }

    // ~~~~~~~~~~~~~~~~~~~~ 6. LOGIN STATUS MESSAGE ~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Returns the appropriate message after a login attempt.
     * @param username username entered at login
     * @param password password entered at login
     * @return welcome message if credentials correct, otherwise error message
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            // Success message with the user's stored first and last name
            return "Welcome " + storedFirstName + ", " + storedLastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

// ++++++++++++++++++ MAIN APPLICATION ++++++++++++++++++++++
/**
 * Console-based app that lets a user register and then log in.
 * Uses the Login class with all 6 required methods.
 */
public class LoginPart1 {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Login loginSystem = new Login();
        
        System.out.println("\n======= WELCOME TO THE CHAT APP SETUP =======\n");
        
        // ---------- REGISTRATION PHASE ----------
        System.out.println("--- REGISTRATION ---");
        
        // Get first and last name (no special validation needed for these)
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine().trim();
        
        // Get valid username (loop until it meets the rules)
        String username;
        while (true) {
            System.out.print("\nCreate a username (max 5 chars, must contain '_'): ");
            username = scanner.nextLine().trim();
            if (loginSystem.checkUserName(username)) {
                System.out.println("[OK] Username successfully captured.");
                break;
            } else {
                System.out.println("[ERROR] Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        }
        
        // Get valid password (loop until it meets complexity rules)
        String password;
        while (true) {
            System.out.print("\nCreate a password (8+ chars, 1 capital, 1 number, 1 special char): ");
            password = scanner.nextLine().trim();
            if (loginSystem.checkPasswordComplexity(password)) {
                System.out.println("[OK] Password successfully captured.");
                break;
            } else {
                System.out.println("[ERROR] Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        }
        
        // Get valid SA cell number (loop until it matches +27 format)
        String phone;
        while (true) {
            System.out.print("\nEnter your cell number (e.g., +27831234567): ");
            phone = scanner.nextLine().trim();
            if (loginSystem.checkCellPhoneNumber(phone)) {
                System.out.println("[OK] Cell phone number successfully added.");
                break;
            } else {
                System.out.println("[ERROR] Cell phone number incorrectly formatted or does not contain international code.");
            }
        }
        
        // Attempt to register the user (all fields are already valid, but registerUser will store them)
        String registrationResult = loginSystem.registerUser(firstName, lastName, username, password, phone);
        System.out.println("\n" + registrationResult);
        
        if (!registrationResult.contains("successful")) {
            System.out.println("Registration failed. Restart the app and try again.");
            return; // Stop here if registration didn't work (shouldn't happen with our loops)
        }
        
        // ---------- LOGIN PHASE ----------
        System.out.println("\n--- LOGIN ---"); 
        int attempts = 3;
        boolean loggedIn = false;
        
        while (attempts > 0 && !loggedIn) {
            System.out.println("\nAttempts remaining: " + attempts);
            System.out.print("Username: ");
            String loginUsername = scanner.nextLine().trim();
            System.out.print("Password: ");
            String loginPassword = scanner.nextLine().trim();
            
            // Use the required returnLoginStatus method to get the right message
            String loginMessage = loginSystem.returnLoginStatus(loginUsername, loginPassword);
            System.out.println(loginMessage);
            
            if (loginSystem.loginUser(loginUsername, loginPassword)) {
                loggedIn = true;
                System.out.println("\nAccess granted! You may proceed to the main chat area (coming in Part 2).");
            } else {
                attempts--;
                if (attempts == 0) {
                    System.out.println("\nToo many failed attempts. Please restart the application.");
                }
            }
        }
        
        System.out.println("\n=~=~=~=~=~=~= END OF PART ONE =~=~=~=~=~=~=~=");
        scanner.close();
    }
}