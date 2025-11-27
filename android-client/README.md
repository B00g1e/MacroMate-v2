# MacroMate Android Client

Android mobile application for controlling PC macros remotely.

## Features

- Button grid interface
- Responsive layout (3x4 portrait, 6x2 landscape)
- WebSocket connection to PC server
- Real-time button configuration sync
- Visual feedback on button press
- Connection status indicator

## Building

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 24+
- Gradle 8.0+

### Build Instructions

1. Open project in Android Studio
2. Sync Gradle files
3. Build > Make Project
4. Run on device or emulator

### Build from Command Line

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

## Usage

1. Install and open the app
2. Tap settings icon in bottom-right
3. Enter server URL: `http://YOUR_PC_IP:8887`
4. Tap "Connect"
5. Buttons will appear once connected

## Configuration

Server URL is saved in localStorage and persists between sessions.

## Project Structure

```
app/
├── src/main/
│   ├── java/me/bugi/macromate/
│   │   ├── MainActivity.java        # Main activity with WebView
│   │   └── SocketManager.java       # WebSocket client
│   ├── assets/
│   │   ├── index.html               # Main UI
│   │   ├── app.js                   # Frontend logic
│   │   └── styles.css               
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Dependencies

- OkHttp 4.12.0 - WebSocket client
- Gson 2.10.1 - JSON parsing
- Material Components - UI components

## Technical Details

### WebSocket Protocol

Messages are JSON objects:

```json
{
  "type": "buttonPress",
  "data": {
    "buttonId": "btn_123",
    "buttonName": "Copy"
  }
}
```

### JavaScript Bridge

Native Android methods exposed to WebView:
- `Android.connect(url)` - Connect to server
- `Android.disconnect()` - Disconnect from server
- `Android.sendButtonPress(id, name)` - Send button press
- `Android.showToast(message)` - Show toast message

## Permissions

- `INTERNET` - Network communication
- `ACCESS_NETWORK_STATE` - Check connectivity
