package me.bugi.macromate.server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerGUI extends JFrame {
    private MacroMateWebSocketServer socketServer;
    private MacroExecutor macroExecutor;

    private JLabel serverIPLabel;
    private JLabel portLabel;
    private JLabel clientCountLabel;
    private JTextArea logArea;
    private JButton startStopButton;
    private JPanel buttonListPanel;

    private boolean serverRunning = false;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ServerGUI() {
        setTitle("MacroMate Server");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        macroExecutor = new MacroExecutor();

        initComponents();
        loadButtonList();

        // Auto-start server
        startServer();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createButtonConfigPanel());
        splitPane.setRightComponent(createLogPanel());
        splitPane.setDividerLocation(550);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(60, 63, 65));

        // Title
        JLabel titleLabel = new JLabel("MacroMate Server");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Server Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setOpaque(false);

        JLabel ipTitleLabel = new JLabel("Server IP:");
        ipTitleLabel.setForeground(Color.LIGHT_GRAY);
        serverIPLabel = new JLabel("Starting...");
        serverIPLabel.setForeground(Color.WHITE);
        serverIPLabel.setFont(new Font("Courier New", Font.BOLD, 14));

        JLabel portTitleLabel = new JLabel("Port:");
        portTitleLabel.setForeground(Color.LIGHT_GRAY);
        portLabel = new JLabel("3000");
        portLabel.setForeground(Color.WHITE);
        portLabel.setFont(new Font("Courier New", Font.BOLD, 14));

        JLabel clientTitleLabel = new JLabel("Connected Clients:");
        clientTitleLabel.setForeground(Color.LIGHT_GRAY);
        clientCountLabel = new JLabel("0");
        clientCountLabel.setForeground(new Color(76, 175, 80));
        clientCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        infoPanel.add(ipTitleLabel);
        infoPanel.add(serverIPLabel);
        infoPanel.add(portTitleLabel);
        infoPanel.add(portLabel);
        infoPanel.add(clientTitleLabel);
        infoPanel.add(clientCountLabel);

        // Start/Stop Button
        startStopButton = new JButton("Stop Server");
        startStopButton.setFocusPainted(false);
        startStopButton.addActionListener(e -> toggleServer());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(startStopButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createButtonConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Button Configuration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton addButton = new JButton("+ Add Button");
        addButton.addActionListener(e -> showButtonDialog(null, -1));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Button List Panel (scrollable)
        buttonListPanel = new JPanel();
        buttonListPanel.setLayout(new BoxLayout(buttonListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(buttonListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Activity Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(300, 0));

        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> logArea.setText(""));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(clearButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadButtonList() {
        buttonListPanel.removeAll();

        int index = 0;
        for (MacroExecutor.ButtonData button : macroExecutor.getButtonConfig().buttons) {
            JPanel buttonItem = createButtonItem(button, index);
            buttonListPanel.add(buttonItem);
            buttonListPanel.add(Box.createVerticalStrut(5));
            index++;
        }

        buttonListPanel.revalidate();
        buttonListPanel.repaint();
    }

    private JPanel createButtonItem(MacroExecutor.ButtonData button, int index) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel(button.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel idLabel = new JLabel(button.id + " • " + button.actions.size() + " action(s)");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        idLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        JButton testButton = new JButton("Test");
        testButton.addActionListener(e -> {
            log("Testing: " + button.name);
            new Thread(() -> macroExecutor.execute(button.id)).start();
        });

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showButtonDialog(button, index));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setForeground(new Color(211, 47, 47));
        deleteButton.addActionListener(e -> deleteButton(index));

        buttonsPanel.add(testButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);

        return panel;
    }

    private void showButtonDialog(MacroExecutor.ButtonData button, int index) {
        ButtonEditorDialog dialog = new ButtonEditorDialog(this, button, index, macroExecutor);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadButtonList();
            if (socketServer != null) {
                socketServer.broadcastConfig(macroExecutor.getButtonConfigJson());
            }
        }
    }

    private void deleteButton(int index) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this button?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            macroExecutor.getButtonConfig().buttons.remove(index);
            macroExecutor.saveConfig();
            loadButtonList();

            if (socketServer != null) {
                socketServer.broadcastConfig(macroExecutor.getButtonConfigJson());
            }

            log("Button deleted");
        }
    }

    private void startServer() {
        try {
            socketServer = new MacroMateWebSocketServer(3000, macroExecutor, this);
            socketServer.start();

            serverIPLabel.setText(socketServer.getServerIP());
            serverRunning = true;
            startStopButton.setText("Stop Server");
            startStopButton.setBackground(new Color(211, 47, 47));

        } catch (Exception e) {
            log("ERROR: Failed to start server - " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to start server: " + e.getMessage(),
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopServer() {
        try {
            if (socketServer != null) {
                socketServer.stop();
            }

            serverRunning = false;
            startStopButton.setText("Start Server");
            startStopButton.setBackground(null);

        } catch (Exception e) {
            log("ERROR: Failed to stop server - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void toggleServer() {
        if (serverRunning) {
            stopServer();
        } else {
            startServer();
        }
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = timeFormat.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void updateClientCount(int count) {
        SwingUtilities.invokeLater(() -> {
            clientCountLabel.setText(String.valueOf(count));
        });
    }
}
