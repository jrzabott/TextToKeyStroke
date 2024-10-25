package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JNAMacKeystrokeExample {

    /**
     * Constants for CoreGraphics library. Represents the event tap and source state. Used for the event tap.
     */
    @SuppressWarnings("java:S115")
    public static final int kCGHIDEventTap = 0;
    /**
     * Represents the HID system state. Used for the event source.
     */
    @SuppressWarnings("java:S115")
    public static final int kCGEventSourceStateHIDSystemState = 1;

    /**
     * Main method to demonstrate typing a sample Base64 string.
     *
     * @param args Command-line arguments.
     * @throws InterruptedException If the thread is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        String input = "HelloBase64Test+/="; // Example Base64 string

        System.out.println("Simulating typing: " + input);
        System.out.println("Waiting 5 seconds to focus on the target window...");
        Thread.sleep(5000); // Give time to focus on the target window

        // Submit typing to another thread via a single-threaded executor
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            final Future<?> typing = executor.submit(() -> simulateTypingString(input));
            typing.get(); // Wait for the typing to finish
            Thread.sleep(1000); // Optional: Wait for the last keystroke to be processed
        } catch (ExecutionException e) {
            e.printStackTrace(); // TODO - improve logging for the future
        }
    }

    /**
     * Simulates typing a string by generating keyboard events.
     *
     * @param text The text to type.
     */
    public static void simulateTypingString(String text) {
        // Create an event source
        Pointer eventSource = CoreGraphicsLibrary.INSTANCE.CGEventSourceCreate(kCGEventSourceStateHIDSystemState);

        for (char c : text.toCharArray()) {
            simulateCharacter(c, eventSource);

            // Optional: Introduce a small delay between keystrokes
            try {
                Thread.sleep(10); // Adjust delay as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Release the event source
        CoreGraphicsLibrary.INSTANCE.CFRelease(eventSource);

        // Run the run loop to process events
        CoreFoundationLibrary.INSTANCE.CFRunLoopRunInMode("kCFRunLoopDefaultMode", 2.0, false);
    }

    /**
     * Simulates typing a single character by generating keyboard events.
     *
     * @param character   The character to type.
     * @param eventSource The event source.
     */
    public static void simulateCharacter(char character, Pointer eventSource) {
        // Create key down and key up events
        Pointer keyDownEvent = CoreGraphicsLibrary.INSTANCE.CGEventCreateKeyboardEvent(eventSource, (short) 0, true);
        Pointer keyUpEvent = CoreGraphicsLibrary.INSTANCE.CGEventCreateKeyboardEvent(eventSource, (short) 0, false);

        char[] chars = new char[]{character};

        // Set the Unicode string for the events
        CoreGraphicsLibrary.INSTANCE.CGEventKeyboardSetUnicodeString(keyDownEvent, chars.length, chars);
        CoreGraphicsLibrary.INSTANCE.CGEventKeyboardSetUnicodeString(keyUpEvent, chars.length, chars);

        // Post the events
        CoreGraphicsLibrary.INSTANCE.CGEventPost(kCGHIDEventTap, keyDownEvent);
        CoreGraphicsLibrary.INSTANCE.CGEventPost(kCGHIDEventTap, keyUpEvent);

        // Release the events
        CoreGraphicsLibrary.INSTANCE.CFRelease(keyDownEvent);
        CoreGraphicsLibrary.INSTANCE.CFRelease(keyUpEvent);
    }

    /**
     * Interface for CoreGraphics library functions.
     */
    @SuppressWarnings("java:S100")
    public interface CoreGraphicsLibrary extends Library {
        CoreGraphicsLibrary INSTANCE = Native.load("ApplicationServices", CoreGraphicsLibrary.class);

        /**
         * Creates a keyboard event.
         *
         * @param eventSource The event source.
         * @param virtualKey  The virtual key code.
         * @param keyDown     True if the key is pressed, false if released.
         * @return A pointer to the created event.
         */
        Pointer CGEventCreateKeyboardEvent(Pointer eventSource, short virtualKey, boolean keyDown);

        /**
         * Sets the Unicode string for a keyboard event.
         *
         * @param event         The event.
         * @param stringLength  The length of the string.
         * @param unicodeString The Unicode string.
         */
        void CGEventKeyboardSetUnicodeString(Pointer event, int stringLength, char[] unicodeString);

        /**
         * Posts an event.
         *
         * @param tap   The event tap.
         * @param event The event.
         */
        void CGEventPost(int tap, Pointer event);

        /**
         * Releases a reference to an event.
         *
         * @param ref The reference to release.
         */
        void CFRelease(Pointer ref);

        /**
         * Creates an event source.
         *
         * @param sourceStateID The source state ID.
         * @return A pointer to the created event source.
         */
        Pointer CGEventSourceCreate(int sourceStateID);
    }

    /**
     * Interface for CoreFoundation library functions.
     */
    @SuppressWarnings("java:S100")
    public interface CoreFoundationLibrary extends Library {
        CoreFoundationLibrary INSTANCE = Native.load("CoreFoundation", CoreFoundationLibrary.class);

        /**
         * Runs the run loop in a given mode for a given time.
         *
         * @param mode                     The mode.
         * @param seconds                  The time in seconds.
         * @param returnAfterSourceHandled True to return after the source is handled.
         */
        void CFRunLoopRunInMode(String mode, double seconds, boolean returnAfterSourceHandled);
    }
}
