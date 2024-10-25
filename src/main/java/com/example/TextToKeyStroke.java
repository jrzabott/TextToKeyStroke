package com.example;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextToKeyStroke {

    /**
     * Default delay between keystrokes in milliseconds.
     */
    public static final long DEFAULT_DELAY = 100L;
    private static final int BUFFER_SIZE = 1024;  // Read the file in chunks of 1KB

    public static void main(String[] args) {

        // For tests only. Uncomment
        // args = new String[]{"8NIYyXyl2zQLvArN6cMTXiNpa3pc=BAkhiO7IC9jkD5o/UsvyY=ge4pg2SDke6CKfDhRqQQFyqsqh78z6LcTLuMxa=N+YkhTs1OJiRA66=sr=N=Zv73ftfWbUhqD3jv0SBl7J7cYczNaFZ+lyQcgEdaLxnaCSVCrisLk7MO3NERHjoFFcmxboR/WCGx+Q1+CxB8AYc=V", "--delay", "0"}; // NOSONAR

        // Parse command-line arguments
        CommandLineOptions options = parseArguments(args);
        if (options == null) {
            printUsage();
            return;
        }

        // Check for mutual exclusivity of text input and file input
        if (options.inputFilePath != null && options.inputText != null) {
            System.out.println("Error: You cannot provide both --input and text. Use one or the other.");
            return;
        }

        try {
            // Prepare for sending keystrokes
            Robot robot = new Robot();
            System.out.println("Please focus on the target window... starting in 5 seconds.");
            Thread.sleep(5000);  // Give time to focus on the target window

            // Send keystrokes from file or text
            if (options.inputFilePath != null) {
                System.out.println("Sending keystrokes from file: " + options.inputFilePath);
                sendFileAsKeystrokes(robot, options.inputFilePath, options.delayBetweenKeystrokes);
            } else if (options.inputText != null) {
                System.out.println("Sending text as keystrokes: " + options.inputText);
                sendTextAsKeystrokes(robot, options.inputText, safeLongToInt(options.delayBetweenKeystrokes));
            } else {
                System.out.println("Error: No text or input file provided.");
            }

            System.out.println("Keystroke sending completed!");

        } catch (AWTException | IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace(); // TODO - improve logging for the future
        } catch (InterruptedException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace(); // TODO - improve logging for the future
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Parses command-line arguments and returns an object containing the options.
     */
    private static CommandLineOptions parseArguments(String[] args) {
        if (args.length == 0 || args[0].equals("--help")) {
            return null;  // Return null or print help information
        }

        CommandLineOptions options = new CommandLineOptions();

        int argIndex = 0;
        while (argIndex < args.length) {
            String arg = args[argIndex];

            if (arg.startsWith("--")) {
                // Handle prefixed options
                switch (arg) {
                    case "--input" -> argIndex = handleInputFile(args, argIndex, options);
                    case "--delay" -> argIndex = handleDelay(args, argIndex, options);
                    default -> {
                        System.out.println("Unknown option: " + arg);
                        return null;
                    }
                }
            } else {
                // Handle anything without a prefix as input text
                if (options.inputFilePath == null) {
                    options.inputText = arg;  // Treat this as input text
                } else {
                    System.out.println("Error: Both --input and raw text provided. Use one or the other.");
                    return null;
                }
            }

            argIndex++;  // Move to the next argument
        }

        return options;
    }

    /**
     * Handles the --input argument, ensuring a file path is provided.
     * Returns the updated index after processing.
     */
    private static int handleInputFile(String[] args, int currentIndex, CommandLineOptions options) {
        if (currentIndex + 1 >= args.length) {
            System.out.println("Error: --input requires a file path.");
            return currentIndex;
        }
        options.inputFilePath = args[++currentIndex];
        return currentIndex;
    }

    /**
     * Handles the --delay argument, ensuring a valid integer is provided for the delay.
     * Returns the updated index after processing.
     */
    private static int handleDelay(String[] args, int currentIndex, CommandLineOptions options) {
        if (currentIndex + 1 < args.length) {
            try {
                options.delayBetweenKeystrokes = Integer.parseInt(args[++currentIndex]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid delay value. Using default delay of 100ms.");
                options.delayBetweenKeystrokes = 100;  // Set to default if invalid
            }
        } else {
            System.out.println("Error: --delay requires a value.");
        }
        return currentIndex;
    }

    /**
     * Sends text as keystrokes using the Robot class.
     */
    private static void sendTextAsKeystrokes(Robot robot, String text, int delayBetweenKeystrokes) {
        for (char character : text.toCharArray()) {
            sendKeystroke(robot, character, delayBetweenKeystrokes);
        }
    }

    /**
     * Sends the content of a file as keystrokes using the Robot class.
     */
    private static void sendFileAsKeystrokes(Robot robot, String filePath, long delayBetweenKeystrokes) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    char character = (char) buffer[i];
                    sendKeystroke(robot, character, delayBetweenKeystrokes);
                }
            }
        }
    }

    /**
     * Sends a single character as a keystroke using the Robot class.
     */
    private static void sendKeystroke(Robot robot, char character, long delayBetweenKeystrokes) {
        int keyCode = getKeyCodeForCharacter(character);  // Get the correct keycode for the character
        if (keyCode == KeyEvent.VK_UNDEFINED)
            return;

        // Handle Shift key if needed (e.g., for +, *, ?, and uppercase letters)
        if (requiresShift(character)) {
            robot.keyPress(KeyEvent.VK_SHIFT);  // Press Shift key
        }

        // Press the key and release it
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);

        // Release the Shift key if it was pressed
        if (requiresShift(character)) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

        // Add the delay between keystrokes
        robot.delay(safeLongToInt(delayBetweenKeystrokes));
    }

    /**
     * Determines if a character requires the Shift key to be pressed.
     * This is true for uppercase letters and symbols like +, *, ?, ".
     */
    private static boolean requiresShift(char character) {
        return Character.isUpperCase(character) || "+*?\"".indexOf(character) >= 0;
    }

    /**
     * Gets the keycode for the character, handling special cases for Base64 and common symbols.
     * Handles characters like +, /, =, and others that need special handling.
     */
    private static int getKeyCodeForCharacter(char character) {
        switch (character) {
            case '+':
                return KeyEvent.VK_EQUALS;  // '+' requires Shift with the '=' key
            case '/':
                return KeyEvent.VK_SLASH;   // '/' key
            case '=':
                return KeyEvent.VK_EQUALS;  // '=' key
            // Handle additional special characters if needed
            default:
                return KeyEvent.getExtendedKeyCodeForChar(character);  // Default case for normal characters
        }
    }


    /**
     * Safely converts a long value to an integer, handling overflow and underflow.
     * If the long value is less than Integer.MIN_VALUE, Integer.MIN_VALUE is returned. If the long value is greater
     * than Integer.MAX_VALUE, Integer.MAX_VALUE is returned.
     *
     * @param delayBetweenKeystrokes The long value to convert.
     * @return The integer value.
     */
    private static int safeLongToInt(long delayBetweenKeystrokes) {
        if (delayBetweenKeystrokes < Integer.MIN_VALUE)
            throw new IllegalArgumentException(delayBetweenKeystrokes + " cannot be cast to int without changing its value.");
        if (delayBetweenKeystrokes > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) delayBetweenKeystrokes;
    }

    /**
     * Prints the usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar TextToKeyStroke.jar [options] <text>");
        System.out.println("Options:");
        System.out.println("  --input <path_to_text_file>  Read text from file instead of command-line input");
        System.out.println("  --delay <milliseconds>       Delay between keystrokes (default: 100ms)");
    }

    /**
     * Helper class to store command-line options.
     */
    private static class CommandLineOptions {
        private String inputFilePath = null;
        private String inputText = null;
        private long delayBetweenKeystrokes = TextToKeyStroke.DEFAULT_DELAY;
    }

}
