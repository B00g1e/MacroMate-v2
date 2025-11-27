# MacroMate

Remote macro control application with Android client and PC server that communicate through WebSocket.

## Features

- 🎮 **Modern UI** - Dark theme with customizable button grid
- 📱 **Mobile Client** - Android app with responsive layout (portrait 3x4, landscape 6x2)
- 🖥️ **PC Server** - Java desktop application for macro configuration and execution
- 🔌 **WebSocket Communication** - Real-time button press synchronization
- ⚡ **Macro Execution** - Key presses, text typing, and delays
- 🎨 **Customizable Buttons** - Configure names, emoji icons, and image URLs
- 🔄 **Auto-sync** - Button configuration automatically syncs to all connected clients

## Project Structure

```
MacroMate v2/
├── android-client/     # Android mobile application
└── pc-server/          # Java desktop server application
```

## Quick Start

### PC Server

1. Navigate to `pc-server` directory
2. Build: `mvn clean package`
3. Run: `java -jar target/macromate-server-1.0-SNAPSHOT.jar`
4. Server starts on port 8887

### Android Client

1. Open `android-client` in Android Studio
2. Build and run on your Android device
3. Open settings (gear icon)
4. Enter server URL: `http://YOUR_PC_IP:8887`
5. Click Connect

## Configuration

### Adding Buttons (PC Server)

1. Click "Add Button" in the server UI
2. Configure:
   - **Name**: Button label
   - **Icon**: Emoji (e.g., 📋, 🎵, 🔒)
   - **Image URL**: Optional image URL
3. Add actions:
   - **Key Press**: Keyboard shortcuts with modifiers
   - **Type Text**: Text to type
   - **Delay**: Wait time in milliseconds

### Example Macros

- **Copy**: Ctrl + C
- **Paste**: Ctrl + V
- **Screenshot**: Win + Shift + S
- **Lock PC**: Win + L

## Technology Stack

### Android Client
- WebView with HTML/CSS/JS frontend
- OkHttp for WebSocket communication
- Gson for JSON parsing
- Material Design components

### PC Server
- Java 11
- Java-WebSocket library
- Swing GUI with FlatLaf theme
- Java Robot for macro execution
- Gson for JSON serialization

## Requirements

- **Android**: Android 7.0+ (API 24+)
- **PC**: Java 11 or higher
- **Network**: Both devices on same network

## License

MIT License
