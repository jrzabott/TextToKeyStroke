
# TextToKeyStrokeMain Application

The **TextToKeyStrokeMain** application allows users to simulate keystrokes from a given text file or direct input. 
This application can be used in environments where only keystroke-based data entry is possible, especially useful for 
Virtual Desktop Infrastructure (VDI) setups where traditional file transfer options are unavailable.

## Features
- Supports multiple keystroke simulation methods:
  - `--robot`: Utilizes Java's `Robot` class to simulate keystrokes.
  - `--jna`: Uses Java Native Access (JNA) for keystroke simulation with greater control over native system events.
- Allows users to specify a delay between keystrokes for more controlled simulation.
- Supports input from a file or directly from command line arguments.
- Ideal for encoding data where only keystroke input is available.

## Requirements
- Java Development Kit (JDK) 8 or above.
- Gradle for build management (optional if using the pre-built JAR).

## Build Instructions

1. Clone the repository or download the source files.
2. Navigate to the project directory.
3. Run the following command to build the project:

   ```shell
   ./gradlew build
   ```

4. After the build completes, find the generated JAR file in the `build/libs` directory.

## Usage

Run the application with one of the following commands:

```shell
java -jar TextToKeyStrokes-1.0-SNAPSHOT.jar --robot|--jna [--file <filePath>] [--delay <milliseconds>] [text]
```

### Options
- **--robot**: Use Robot-based keystroke simulation.
- **--jna**: Use JNA-based keystroke simulation.
- **--file <filePath>**: Specify the path to a text file with the input text (optional). If omitted, use command line arguments or standard input.
- **--delay <milliseconds>**: Set the delay between each keystroke in milliseconds (optional, default is 50ms).
- **text**: Directly provide text to type if `--file` is not specified.

> **Note**: `--robot` and `--jna` are mutually exclusive options. Please specify only one.

### Example Usage

#### 1. Keystroke Simulation from Command Line Text
   ```shell
   java -jar TextToKeyStrokes-1.0-SNAPSHOT.jar --robot --delay 100 "Hello, World!"
   ```

#### 2. Keystroke Simulation from a File
   ```shell
   java -jar TextToKeyStrokes-1.0-SNAPSHOT.jar --jna --file path/to/input.txt --delay 50
   ```

## Limitations
- This application is limited by the speed of keystroke simulation, which can be slow for large amounts of data.
- In some environments, using JNA may require special permissions or configurations.

## Troubleshooting

1. **Application Loses Focus**: Ensure the target window remains focused before starting the keystroke simulation. In some cases, a slight delay or focus-lock setup might be necessary.
2. **Incorrect Character Output**: Check if the correct input method (`--robot` or `--jna`) is used, as certain applications may handle these methods differently.

## License
This application is distributed under the MIT License. See `LICENSE` for more information.

---

