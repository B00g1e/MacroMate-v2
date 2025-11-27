package me.bugi.macromate.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class MacroMateWebSocketServer extends WebSocketServer {
    private MacroExecutor macroExecutor;
    private ServerGUI gui;
    private final Gson gson = new Gson();
    private Set<WebSocket> clients = new HashSet<>();

    public MacroMateWebSocketServer(int port, MacroExecutor macroExecutor, ServerGUI gui) {
        super(new InetSocketAddress(port));
        this.macroExecutor = macroExecutor;
        this.gui = gui;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        String clientId = conn.getRemoteSocketAddress().toString();
        gui.log("Client connected: " + clientId);
        gui.updateClientCount(clients.size());

        // Send current button configuration
        JsonObject message = new JsonObject();
        message.addProperty("type", "buttonConfig");
        message.addProperty("data", macroExecutor.getButtonConfigJson());
        conn.send(message.toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        String clientId = conn.getRemoteSocketAddress().toString();
        gui.log("Client disconnected: " + clientId);
        gui.updateClientCount(clients.size());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();

            if ("buttonPress".equals(type)) {
                JsonObject data = json.getAsJsonObject("data");
                String buttonId = data.get("buttonId").getAsString();
                String buttonName = data.get("buttonName").getAsString();

                gui.log("Button pressed: " + buttonName);
                new Thread(() -> macroExecutor.execute(buttonId)).start();
            }
        } catch (Exception e) {
            gui.log("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        gui.log("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        gui.log("WebSocket server started on port " + getPort());
        gui.log("Server IP: " + getServerIP());
    }

    public void broadcastConfig(String configJson) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "buttonConfig");
        message.addProperty("data", configJson);
        String messageStr = message.toString();

        for (WebSocket client : clients) {
            client.send(messageStr);
        }
    }

    public String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }

    public int getConnectedClientsCount() {
        return clients.size();
    }

    public boolean isRunning() {
        // WebSocketServer doesn't have isClosed(), check if connections exist or use a flag
        return getConnections() != null && !getConnections().isEmpty();
    }
}
