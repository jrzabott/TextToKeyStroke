package com.example;

import java.awt.*;
import java.awt.event.KeyEvent;

public class RobotKeystroke implements KeystrokeStrategy {

    private final int delay;
    private final Robot robot;

    public static class RobotInitializationException extends RuntimeException {
        public RobotInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public RobotKeystroke(int delay) {
        this.delay = delay;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            throw new RobotInitializationException("Error creating Robot instance: " + e.getMessage(), e);
        }
    }

    @Override
    public void simulateTyping(String text) {
        try {
            for (char c : text.toCharArray()) {
                typeCharacter(robot, c);
                Thread.sleep(delay); // Slight delay between keystrokes
            }
        } catch (Exception e) {
            System.err.println("Robot keystroke error: " + e.getMessage());
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
        }
    }

    private void typeCharacter(Robot robot, char character) {
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
        robot.delay(delay);
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
     * Determines if a character requires the Shift key to be pressed.
     * This is true for uppercase letters and symbols like +, *, ?, ".
     */
    private static boolean requiresShift(char character) {
        return Character.isUpperCase(character) || "+*?\"".indexOf(character) >= 0;
    }
}
