package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TextToKeyStrokeMain {

    protected static final String ARG_ROBOT = "--robot";
    protected static final String ARG_JNA = "--jna";
    protected static final String ERR_MSG_ROBOT_OR_JNA = "Specify either --robot or --jna, not both.";

    public static void main(String[] args) {
        try {
            KeystrokeStrategy strategy = parseArguments(args);
            String inputText = getInputText(args);

            System.out.println("Waiting 5 seconds to focus on the target window...");
            Thread.sleep(5000); // Give time to focus on the target window
            strategy.simulateTyping(inputText);
            Thread.sleep(500); // Optional: Wait for the last keystroke to be processed
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            printUsage();
        }
    }

    private static KeystrokeStrategy parseArguments(String[] args) {
        int delay = parseDelay(args);
        boolean robotSelected = false;
        boolean jnaSelected = false;

        for (String arg : args) {
            if (ARG_ROBOT.equalsIgnoreCase(arg)) {
                if (jnaSelected) throw new IllegalArgumentException(ERR_MSG_ROBOT_OR_JNA);
                robotSelected = true;
            }

            if (ARG_JNA.equalsIgnoreCase(arg)) {
                if (robotSelected) throw new IllegalArgumentException(ERR_MSG_ROBOT_OR_JNA);
                jnaSelected = true;
            }
        }

        if (robotSelected) return new RobotKeystroke(delay);
        if (jnaSelected) return new JNAKeystroke(delay);
        throw new IllegalArgumentException("Specify either --robot or --jna.");
    }

    private static String getInputText(String[] args) throws IOException {
        StringBuilder inputText = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (ARG_ROBOT.equalsIgnoreCase(args[i]) || ARG_JNA.equalsIgnoreCase(args[i])) {
                continue; // Skip mode arguments
            }

            if ("--file".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                return readFile(args[i + 1]); // Read from file if --file option is present
            }

            if ("--delay".equalsIgnoreCase(args[i])) {
                i++; // Skip the delay value
                continue;
            }

            // Collect remaining arguments as input text
            inputText.append(args[i]).append(" ");
        }

        if (!inputText.isEmpty()) {
            return inputText.toString().trim(); // Return concatenated input text
        }

        // Fallback to reading from standard input if no text argument is provided
        System.out.println("Enter text to type (end with Ctrl+D):");
        return new String(System.in.readAllBytes());
    }


    private static String readFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        return String.join("\n", lines);
    }

    private static int parseDelay(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--delay".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid delay value. Using default delay of 50ms.");
                }
            }
        }
        return 50; // Default delay of 50ms
    }

    private static void printUsage() {
        System.out.println("Usage: java com.example.TextToKeyStrokeMain --robot|--jna [--file <filePath>] [--delay <milliseconds>] [text]");
        System.out.println("Options:");
        System.out.println("  --robot          Use Robot-based keystroke simulation.");
        System.out.println("  --jna            Use JNA-based keystroke simulation.");
        System.out.println("  --file <path>    Path to a text file with input text (optional).");
        System.out.println("  --delay <ms>     Delay between keystrokes in milliseconds (optional, default is 50ms).");
        System.out.println("  text             Text to type directly, if no --file is specified.");
        System.out.println("Note: --robot and --jna are mutually exclusive. Please specify one.");
    }
}
