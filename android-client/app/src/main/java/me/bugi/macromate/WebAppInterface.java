package me.bugi.macromate;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    Context context;
    SocketManager socketManager;

    public WebAppInterface(Context context, SocketManager socketManager) {
        this.context = context;
        this.socketManager = socketManager;
    }

    @JavascriptInterface
    public void connect(String serverUrl) {
        socketManager.connect(serverUrl);
    }

    @JavascriptInterface
    public void disconnect() {
        socketManager.disconnect();
    }

    @JavascriptInterface
    public void sendButtonPress(String buttonId, String buttonName) {
        socketManager.sendButtonPress(buttonId, buttonName);
    }

    @JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String getConnectionStatus() {
        return socketManager.isConnected() ? "connected" : "disconnected";
    }
}
