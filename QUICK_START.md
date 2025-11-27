# Quick Start Guide

Get MacroMate up and running in 5 minutes!

## Prerequisites

- **PC**: Java 11 or higher installed
- **Android**: Phone/tablet with Android 7.0+
- **Network**: Both devices on the same WiFi network

## Step 1: Start PC Server

### Windows
```bash
cd pc-server
build.bat
run.bat
```

### Linux/Mac
```bash
cd pc-server
mvn clean package
java -jar target/macromate-server-1.0-SNAPSHOT.jar
```

The server will start on port **8887**.

## Step 2: Find Your PC's IP Address

### Windows
```bash
ipconfig
```
Look for "IPv4 Address" under your network adapter (e.g., `192.168.1.100`)

### Linux/Mac
```bash
ifconfig
# or
ip addr
```

## Step 3: Build Android App

### Option A: Android Studio (Recommended)
1. Open `android-client` folder in Android Studio
2. Wait for Gradle sync to complete
3. Click "Run" (green play button)
4. Select your device

### Option B: Command Line
```bash
cd android-client
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Step 4: Connect

1. Open MacroMate app on your phone
2. Tap the settings gear icon (⚙️) in bottom-right
3. Enter server URL: `http://YOUR_PC_IP:8887`
   - Example: `http://192.168.1.100:8887`
4. Tap "Connect"
5. You should see "Connected" in green

## Step 5: Configure Buttons

1. On PC server, click "Add Button"
2. Enter button name (e.g., "Copy")
3. Add icon emoji: 📋
4. Add action: Key Press
5. Configure: Ctrl + C
6. Click "Save"
7. Button appears on your phone instantly!

## Troubleshooting

### Can't Connect
- ✅ Check both devices are on same WiFi
- ✅ Verify server is running (check PC server window)
- ✅ Check firewall isn't blocking port 8887
- ✅ Try `http://` not `https://`

### Buttons Not Appearing
- ✅ Check connection status (should be green)
- ✅ Try disconnecting and reconnecting
- ✅ Check server logs for errors

### Macros Not Working
- ✅ Make sure the target application has focus
- ✅ Try adding small delays (50-100ms) between actions
- ✅ Some apps may block simulated input

## Example Macros

### Copy
- Key Press: C
- Modifiers: ✅ Ctrl

### Paste
- Key Press: V
- Modifiers: ✅ Ctrl

### Screenshot (Windows)
- Key Press: S
- Modifiers: ✅ Win, ✅ Shift

### Lock PC (Windows)
- Key Press: L
- Modifiers: ✅ Win

### Type Email
- Type Text: `your.email@example.com`

## Next Steps

- Explore different action types (Key Press, Type Text, Delay)
- Combine multiple actions into complex macros
- Add custom icons and images to buttons
- Create button layouts for different workflows

Enjoy using MacroMate! 🎮
