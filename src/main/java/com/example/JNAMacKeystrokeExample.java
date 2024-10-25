package com.example;

import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.Map;

public class JNAMacKeystrokeExample {

    @SuppressWarnings("java:S100")
    public interface CoreGraphicsLibrary extends Library {
        CoreGraphicsLibrary INSTANCE = Native.load("ApplicationServices", CoreGraphicsLibrary.class);

        Pointer CGEventCreateKeyboardEvent(Pointer source, short keycode, boolean keyDown);
        void CGEventPost(int tap, Pointer event);
        void CFRelease(Pointer ref);
        void CGEventSetFlags(Pointer event, long flags);
    }

    @SuppressWarnings("java:S115")
    public static final int kCGHIDEventTap = 0;
    @SuppressWarnings("java:S115")
    public static final long kCGEventFlagMaskShift = 0x00020000;

    // Data class to hold keycode and shift information
    public static class KeyCodeAndShift {
        public final short keyCode;
        public final boolean shiftRequired;

        public KeyCodeAndShift(short keyCode, boolean shiftRequired) {
            this.keyCode = keyCode;
            this.shiftRequired = shiftRequired;
        }
    }

    // Mapping of characters to KeyCodeAndShift
    private static final Map<Character, KeyCodeAndShift> keyCodeMap = new HashMap<>();

    static {
        // Letters
        keyCodeMap.put('a', new KeyCodeAndShift((short) 0x00, false));
        keyCodeMap.put('A', new KeyCodeAndShift((short) 0x00, true));
        keyCodeMap.put('b', new KeyCodeAndShift((short) 0x0B, false));
        keyCodeMap.put('B', new KeyCodeAndShift((short) 0x0B, true));
        keyCodeMap.put('c', new KeyCodeAndShift((short) 0x08, false));
        keyCodeMap.put('C', new KeyCodeAndShift((short) 0x08, true));
        keyCodeMap.put('d', new KeyCodeAndShift((short) 0x02, false));
        keyCodeMap.put('D', new KeyCodeAndShift((short) 0x02, true));
        keyCodeMap.put('e', new KeyCodeAndShift((short) 0x0E, false));
        keyCodeMap.put('E', new KeyCodeAndShift((short) 0x0E, true));
        keyCodeMap.put('f', new KeyCodeAndShift((short) 0x03, false));
        keyCodeMap.put('F', new KeyCodeAndShift((short) 0x03, true));
        keyCodeMap.put('g', new KeyCodeAndShift((short) 0x05, false));
        keyCodeMap.put('G', new KeyCodeAndShift((short) 0x05, true));
        keyCodeMap.put('h', new KeyCodeAndShift((short) 0x04, false));
        keyCodeMap.put('H', new KeyCodeAndShift((short) 0x04, true));
        keyCodeMap.put('i', new KeyCodeAndShift((short) 0x22, false));
        keyCodeMap.put('I', new KeyCodeAndShift((short) 0x22, true));
        keyCodeMap.put('j', new KeyCodeAndShift((short) 0x26, false));
        keyCodeMap.put('J', new KeyCodeAndShift((short) 0x26, true));
        keyCodeMap.put('k', new KeyCodeAndShift((short) 0x28, false));
        keyCodeMap.put('K', new KeyCodeAndShift((short) 0x28, true));
        keyCodeMap.put('l', new KeyCodeAndShift((short) 0x25, false));
        keyCodeMap.put('L', new KeyCodeAndShift((short) 0x25, true));
        keyCodeMap.put('m', new KeyCodeAndShift((short) 0x2E, false));
        keyCodeMap.put('M', new KeyCodeAndShift((short) 0x2E, true));
        keyCodeMap.put('n', new KeyCodeAndShift((short) 0x2D, false));
        keyCodeMap.put('N', new KeyCodeAndShift((short) 0x2D, true));
        keyCodeMap.put('o', new KeyCodeAndShift((short) 0x1F, false));
        keyCodeMap.put('O', new KeyCodeAndShift((short) 0x1F, true));
        keyCodeMap.put('p', new KeyCodeAndShift((short) 0x23, false));
        keyCodeMap.put('P', new KeyCodeAndShift((short) 0x23, true));
        keyCodeMap.put('q', new KeyCodeAndShift((short) 0x0C, false));
        keyCodeMap.put('Q', new KeyCodeAndShift((short) 0x0C, true));
        keyCodeMap.put('r', new KeyCodeAndShift((short) 0x0F, false));
        keyCodeMap.put('R', new KeyCodeAndShift((short) 0x0F, true));
        keyCodeMap.put('s', new KeyCodeAndShift((short) 0x01, false));
        keyCodeMap.put('S', new KeyCodeAndShift((short) 0x01, true));
        keyCodeMap.put('t', new KeyCodeAndShift((short) 0x11, false));
        keyCodeMap.put('T', new KeyCodeAndShift((short) 0x11, true));
        keyCodeMap.put('u', new KeyCodeAndShift((short) 0x20, false));
        keyCodeMap.put('U', new KeyCodeAndShift((short) 0x20, true));
        keyCodeMap.put('v', new KeyCodeAndShift((short) 0x09, false));
        keyCodeMap.put('V', new KeyCodeAndShift((short) 0x09, true));
        keyCodeMap.put('w', new KeyCodeAndShift((short) 0x0D, false));
        keyCodeMap.put('W', new KeyCodeAndShift((short) 0x0D, true));
        keyCodeMap.put('x', new KeyCodeAndShift((short) 0x07, false));
        keyCodeMap.put('X', new KeyCodeAndShift((short) 0x07, true));
        keyCodeMap.put('y', new KeyCodeAndShift((short) 0x10, false));
        keyCodeMap.put('Y', new KeyCodeAndShift((short) 0x10, true));
        keyCodeMap.put('z', new KeyCodeAndShift((short) 0x06, false));
        keyCodeMap.put('Z', new KeyCodeAndShift((short) 0x06, true));

        // Digits
        keyCodeMap.put('1', new KeyCodeAndShift((short) 0x12, false));
        keyCodeMap.put('!', new KeyCodeAndShift((short) 0x12, true));
        keyCodeMap.put('2', new KeyCodeAndShift((short) 0x13, false));
        keyCodeMap.put('@', new KeyCodeAndShift((short) 0x13, true));
        keyCodeMap.put('3', new KeyCodeAndShift((short) 0x14, false));
        keyCodeMap.put('#', new KeyCodeAndShift((short) 0x14, true));
        keyCodeMap.put('4', new KeyCodeAndShift((short) 0x15, false));
        keyCodeMap.put('$', new KeyCodeAndShift((short) 0x15, true));
        keyCodeMap.put('5', new KeyCodeAndShift((short) 0x17, false));
        keyCodeMap.put('%', new KeyCodeAndShift((short) 0x17, true));
        keyCodeMap.put('6', new KeyCodeAndShift((short) 0x16, false));
        keyCodeMap.put('^', new KeyCodeAndShift((short) 0x16, true));
        keyCodeMap.put('7', new KeyCodeAndShift((short) 0x1A, false));
        keyCodeMap.put('&', new KeyCodeAndShift((short) 0x1A, true));
        keyCodeMap.put('8', new KeyCodeAndShift((short) 0x1C, false));
        keyCodeMap.put('*', new KeyCodeAndShift((short) 0x1C, true));
        keyCodeMap.put('9', new KeyCodeAndShift((short) 0x19, false));
        keyCodeMap.put('(', new KeyCodeAndShift((short) 0x19, true));
        keyCodeMap.put('0', new KeyCodeAndShift((short) 0x1D, false));
        keyCodeMap.put(')', new KeyCodeAndShift((short) 0x1D, true));

        // Special characters
        keyCodeMap.put('-', new KeyCodeAndShift((short) 0x1B, false));
        keyCodeMap.put('_', new KeyCodeAndShift((short) 0x1B, true));
        keyCodeMap.put('=', new KeyCodeAndShift((short) 0x18, false));
        keyCodeMap.put('+', new KeyCodeAndShift((short) 0x18, true));
        keyCodeMap.put('[', new KeyCodeAndShift((short) 0x21, false));
        keyCodeMap.put('{', new KeyCodeAndShift((short) 0x21, true));
        keyCodeMap.put(']', new KeyCodeAndShift((short) 0x1E, false));
        keyCodeMap.put('}', new KeyCodeAndShift((short) 0x1E, true));
        keyCodeMap.put('\\', new KeyCodeAndShift((short) 0x2A, false));
        keyCodeMap.put('|', new KeyCodeAndShift((short) 0x2A, true));
        keyCodeMap.put(';', new KeyCodeAndShift((short) 0x29, false));
        keyCodeMap.put(':', new KeyCodeAndShift((short) 0x29, true));
        keyCodeMap.put('\'', new KeyCodeAndShift((short) 0x27, false));
        keyCodeMap.put('"', new KeyCodeAndShift((short) 0x27, true));
        keyCodeMap.put(',', new KeyCodeAndShift((short) 0x2B, false));
        keyCodeMap.put('<', new KeyCodeAndShift((short) 0x2B, true));
        keyCodeMap.put('.', new KeyCodeAndShift((short) 0x2F, false));
        keyCodeMap.put('>', new KeyCodeAndShift((short) 0x2F, true));
        keyCodeMap.put('/', new KeyCodeAndShift((short) 0x2C, false));
        keyCodeMap.put('?', new KeyCodeAndShift((short) 0x2C, true));
        keyCodeMap.put('`', new KeyCodeAndShift((short) 0x32, false));
        keyCodeMap.put('~', new KeyCodeAndShift((short) 0x32, true));
        keyCodeMap.put(' ', new KeyCodeAndShift((short) 0x31, false));
    }

    public static void main(String[] args) throws InterruptedException {
        String input = "HelloBase64Test+/="; // Example Base64 string

        System.out.println("Simulating typing: " + input);
        System.out.println("Waiting 5 seconds to focus on the target window...");
        Thread.sleep(5000);  // Give time to focus on the target window

        simulateTypingString(input);
    }

    public static void simulateTypingString(String text) {
        for (char c : text.toCharArray()) {
            simulateCharacter(c);
        }
    }

    public static void simulateCharacter(char character) {
        KeyCodeAndShift kc = keyCodeMap.get(character);

        if (kc == null) {
            System.err.println("Unsupported character: " + character);
            return;
        }

        simulateKeyPress(kc.keyCode, true, kc.shiftRequired);  // Key down
        simulateKeyPress(kc.keyCode, false, kc.shiftRequired); // Key up
    }

    public static void simulateKeyPress(short keycode, boolean keyDown, boolean shiftRequired) {
        Pointer event = CoreGraphicsLibrary.INSTANCE.CGEventCreateKeyboardEvent(null, keycode, keyDown);

        long flags = 0;
        if (shiftRequired) {
            flags |= kCGEventFlagMaskShift;
        }

        CoreGraphicsLibrary.INSTANCE.CGEventSetFlags(event, flags);

        CoreGraphicsLibrary.INSTANCE.CGEventPost(kCGHIDEventTap, event);
        CoreGraphicsLibrary.INSTANCE.CFRelease(event);
    }
}
