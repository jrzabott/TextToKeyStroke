package com.example;

import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public class JNAKeystroke implements KeystrokeStrategy {

    private final int delay;

    public JNAKeystroke(int delay) {
        this.delay = delay;
    }


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

    @Override
    public void simulateTyping(String text) {
        // Create an event source
        Pointer eventSource = JNAMacKeystrokeExample.CoreGraphicsLibrary.INSTANCE.CGEventSourceCreate(kCGEventSourceStateHIDSystemState);
        for (char c : text.toCharArray()) {
            simulateCharacter(c, eventSource);

            // Optional: Introduce a small delay between keystrokes
            try {
                Thread.sleep(delay); // Adjust delay as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Release the event source
        JNAMacKeystrokeExample.CoreGraphicsLibrary.INSTANCE.CFRelease(eventSource);
        // Run the run loop to process events
        JNAMacKeystrokeExample.CoreFoundationLibrary.INSTANCE.CFRunLoopRunInMode("kCFRunLoopDefaultMode", 2.0, false);
    }

    private void simulateCharacter(char character, Pointer eventSource) {
        Pointer keyDownEvent = CoreGraphicsLibrary.INSTANCE.CGEventCreateKeyboardEvent(eventSource, (short) 0, true);
        Pointer keyUpEvent = CoreGraphicsLibrary.INSTANCE.CGEventCreateKeyboardEvent(eventSource, (short) 0, false);

        char[] chars = new char[]{character};
        CoreGraphicsLibrary.INSTANCE.CGEventKeyboardSetUnicodeString(keyDownEvent, chars.length, chars);
        CoreGraphicsLibrary.INSTANCE.CGEventKeyboardSetUnicodeString(keyUpEvent, chars.length, chars);

        CoreGraphicsLibrary.INSTANCE.CGEventPost(kCGHIDEventTap, keyDownEvent);
        CoreGraphicsLibrary.INSTANCE.CGEventPost(kCGHIDEventTap, keyUpEvent);

        CoreGraphicsLibrary.INSTANCE.CFRelease(keyDownEvent);
        CoreGraphicsLibrary.INSTANCE.CFRelease(keyUpEvent);
    }

    /**
     * Interface for CoreGraphics library functions.
     */
    @SuppressWarnings("java:S100")
    public interface CoreGraphicsLibrary extends Library {
        JNAMacKeystrokeExample.CoreGraphicsLibrary INSTANCE = Native.load("ApplicationServices", JNAMacKeystrokeExample.CoreGraphicsLibrary.class);

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
        JNAMacKeystrokeExample.CoreFoundationLibrary INSTANCE = Native.load("CoreFoundation", JNAMacKeystrokeExample.CoreFoundationLibrary.class);

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
