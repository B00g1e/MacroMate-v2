package me.bugi.macromate;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketManager {
    private static final String TAG = "SocketManager";
    private WebSocket webSocket;
    private Context context;
    private WebView webView;
    private Handler mainHandler;
    private final Gson gson = new Gson();

    public SocketManager(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void connect(String serverUrl) {
        // Convert http URL to ws URL
        String wsUrl = serverUrl.replace("http://", "ws://").replace("https://", "wss://");

        Log.d(TAG, "Connecting to: " + wsUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connected");
                notifyWebView("onConnected", "");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);

                try {
                    JsonObject json = gson.fromJson(text, JsonObject.class);
                    String type = json.get("type").getAsString();

                    if ("buttonConfig".equals(type)) {
                        String data = json.get("data").getAsString();
                        notifyWebView("onButtonConfig", data);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing message", e);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                notifyWebView("onDisconnected", "");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error", t);
                notifyWebView("onError", t.getMessage());
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User disconnected");
            webSocket = null;
        }
    }

    public void sendButtonPress(String buttonId, String buttonName) {
        if (webSocket != null) {
            JsonObject message = new JsonObject();
            message.addProperty("type", "buttonPress");

            JsonObject data = new JsonObject();
            data.addProperty("buttonId", buttonId);
            data.addProperty("buttonName", buttonName);

            message.add("data", data);

            String jsonMessage = message.toString();
            webSocket.send(jsonMessage);
            Log.d(TAG, "Button press sent: " + buttonName);
        } else {
            Log.w(TAG, "Cannot send button press - not connected");
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }

    private void notifyWebView(String eventName, String data) {
        mainHandler.post(() -> {
            String jsCode = String.format("if(window.%s) window.%s('%s')",
                eventName, eventName, data.replace("'", "\\'").replace("\n", "\\n"));
            webView.evaluateJavascript(jsCode, null);
        });
    }
}
