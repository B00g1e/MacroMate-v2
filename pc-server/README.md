# MacroMate PC Server

Java desktop application for configuring and executing macros triggered from Android client.

## Features

- WebSocket server for client connections
- Swing GUI with modern FlatLaf theme
- Visual button configuration editor
- Support for multiple action types:
  - Key presses with modifiers (Ctrl, Shift, Alt, Win)
  - Text typing
  - Delays
- Button customization (name, emoji icon, image URL)
- JSON-based configuration storage
- Real-time sync to all connected clients

## Building

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build Instructions

```bash
# Clean and build
mvn clean package

# Run the application
java -jar target/macromate-server-1.0-SNAPSHOT.jar
```

The JAR file includes all dependencies (uber JAR).

## Usage

1. Run the server application
2. Server starts on port 8887
3. Use the GUI to add/edit/delete buttons
4. Configure actions for each button
5. Connected Android clients receive updates automatically

## Configuration

Configuration is saved to `button_config.json` in the application directory.

### Button Configuration Format

```json
{
  "buttons": [
    {
      "id": "btn_123",
      "name": "Copy",
      "icon": "📋",
      "image": "https://example.com/icon.png",
      "actions": [
        {
          "type": "keyPress",
          "key": 67,
          "modifiers": [17]
        }
      ]
    }
  ]
}
```

## Action Types

### Key Press
- **key**: Java KeyEvent key code (e.g., VK_C = 67)
- **modifiers**: Array of modifier key codes
  - Ctrl: VK_CONTROL (17)
  - Shift: VK_SHIFT (16)
  - Alt: VK_ALT (18)
  - Win: VK_WINDOWS (524)

### Type Text
- **text**: String to type

### Delay
- **ms**: Milliseconds to wait

## Project Structure

```
src/main/java/me/bugi/macromate/server/
├── ServerMain.java              # Application entry point
├── MacroMateWebSocketServer.java # WebSocket server
├── MacroExecutor.java           # Macro execution & config
├── ServerUI.java                # Main GUI window
└── ButtonEditorDialog.java      # Button configuration dialog
```

## Dependencies

- Java-WebSocket 1.5.6 - WebSocket server
- Gson 2.10.1 - JSON processing
- FlatLaf 3.2.5 - Modern Swing theme

## Technical Details

### WebSocket Protocol

Server listens on port 8887 and handles:
- Client connections
- Button press messages
- Configuration broadcasts

### Macro Execution

Uses `java.awt.Robot` class for:
- Keyboard input simulation
- Key combinations
- Text typing with delays

### Configuration Management

- Auto-saves on every change
- Broadcasts updates to all connected clients
- JSON format for easy editing

## Default Port

Server runs on port **8887**. To change, modify `PORT` constant in `MacroMateWebSocketServer.java`.

## Troubleshooting

### Port Already in Use
- Check if another instance is running
- Change the port number in code

### Macros Not Working
- Ensure the application has focus permissions
- Some applications may block simulated input
- Try adding small delays between actions

### Clients Not Connecting
- Check firewall settings
- Verify both devices are on same network
- Confirm server IP address is correct
