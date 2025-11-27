package me.bugi.macromate.server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ButtonEditorDialog extends JDialog {
    private JTextField nameField;
    private JTextField iconField;
    private JTextField imageField;
    private JPanel actionsPanel;
    private List<ActionPanel> actionPanels = new ArrayList<>();

    private MacroExecutor.ButtonData button;
    private int buttonIndex;
    private MacroExecutor macroExecutor;
    private boolean saved = false;

    public ButtonEditorDialog(Frame owner, MacroExecutor.ButtonData button, int index, MacroExecutor macroExecutor) {
        super(owner, button == null ? "Add New Button" : "Edit Button", true);
        this.button = button;
        this.buttonIndex = index;
        this.macroExecutor = macroExecutor;

        setSize(600, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();

        if (button != null) {
            loadButton();
        }
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top panel for name, icon and image
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Name field
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.add(new JLabel("Button Name:"), BorderLayout.NORTH);
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        namePanel.add(nameField, BorderLayout.CENTER);
        topPanel.add(namePanel);
        topPanel.add(Box.createVerticalStrut(10));

        // Icon field
        JPanel iconPanel = new JPanel(new BorderLayout(5, 5));
        JLabel iconLabel = new JLabel("Icon (emoji):");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        iconPanel.add(iconLabel, BorderLayout.NORTH);
        iconField = new JTextField();
        iconField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconField.setToolTipText("Enter an emoji (e.g., 📋, 🎵, 🔒)");
        iconPanel.add(iconField, BorderLayout.CENTER);
        topPanel.add(iconPanel);
        topPanel.add(Box.createVerticalStrut(10));

        // Image URL field
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        JLabel imageLabel = new JLabel("Image URL (optional):");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imagePanel.add(imageLabel, BorderLayout.NORTH);
        imageField = new JTextField();
        imageField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imageField.setToolTipText("Enter image URL (e.g., https://example.com/icon.png)");
        imagePanel.add(imageField, BorderLayout.CENTER);
        topPanel.add(imagePanel);
        topPanel.add(Box.createVerticalStrut(10));

        // Actions panel
        JPanel actionsHeaderPanel = new JPanel(new BorderLayout());
        actionsHeaderPanel.add(new JLabel("Actions:"), BorderLayout.WEST);

        JButton addActionButton = new JButton("+ Add Action");
        addActionButton.addActionListener(e -> addActionPanel(null));
        actionsHeaderPanel.add(addActionButton, BorderLayout.EAST);

        actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(actionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(actionsHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel);
    }

    private void loadButton() {
        nameField.setText(button.name);

        if (button.icon != null) {
            iconField.setText(button.icon);
        }

        if (button.image != null) {
            imageField.setText(button.image);
        }

        for (MacroExecutor.ActionData action : button.actions) {
            addActionPanel(action);
        }
    }

    private void addActionPanel(MacroExecutor.ActionData action) {
        ActionPanel panel = new ActionPanel(action);
        actionPanels.add(panel);
        actionsPanel.add(panel);
        actionsPanel.add(Box.createVerticalStrut(5));
        actionsPanel.revalidate();
        actionsPanel.repaint();
    }

    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a button name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<MacroExecutor.ActionData> actions = new ArrayList<>();
        for (ActionPanel panel : actionPanels) {
            MacroExecutor.ActionData action = panel.getAction();
            if (action != null) {
                actions.add(action);
            }
        }

        String icon = iconField.getText().trim();
        String image = imageField.getText().trim();

        if (button == null) {
            // New button
            button = new MacroExecutor.ButtonData();
            button.id = "btn_" + System.currentTimeMillis();
            button.name = name;
            button.icon = icon.isEmpty() ? null : icon;
            button.image = image.isEmpty() ? null : image;
            button.actions = actions;
            macroExecutor.getButtonConfig().buttons.add(button);
        } else {
            // Update existing button
            button.name = name;
            button.icon = icon.isEmpty() ? null : icon;
            button.image = image.isEmpty() ? null : image;
            button.actions = actions;
        }

        macroExecutor.saveConfig();
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    // Inner class for action editor
    private class ActionPanel extends JPanel {
        private JComboBox<String> typeCombo;
        private JPanel fieldsPanel;

        // Key Press fields
        private JTextField keyField;
        private JCheckBox ctrlCheck, shiftCheck, altCheck, winCheck;

        // Type Text fields
        private JTextField textField;

        // Delay fields
        private JSpinner delaySpinner;

        public ActionPanel(MacroExecutor.ActionData action) {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            // Type selector
            JPanel headerPanel = new JPanel(new BorderLayout());
            typeCombo = new JComboBox<>(new String[]{"Key Press", "Type Text", "Delay"});
            typeCombo.addActionListener(e -> updateFields());

            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> {
                actionPanels.remove(this);
                Container parent = getParent();
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            });

            headerPanel.add(typeCombo, BorderLayout.CENTER);
            headerPanel.add(removeButton, BorderLayout.EAST);

            // Fields panel
            fieldsPanel = new JPanel(new BorderLayout());

            add(headerPanel, BorderLayout.NORTH);
            add(fieldsPanel, BorderLayout.CENTER);

            if (action != null) {
                loadAction(action);
            } else {
                updateFields();
            }
        }

        private void loadAction(MacroExecutor.ActionData action) {
            if ("keyPress".equals(action.type)) {
                typeCombo.setSelectedIndex(0);
                updateFields();

                if (action.key != null) {
                    keyField.setText(KeyEvent.getKeyText(action.key));
                }

                if (action.modifiers != null) {
                    for (int mod : action.modifiers) {
                        if (mod == KeyEvent.VK_CONTROL) ctrlCheck.setSelected(true);
                        if (mod == KeyEvent.VK_SHIFT) shiftCheck.setSelected(true);
                        if (mod == KeyEvent.VK_ALT) altCheck.setSelected(true);
                        if (mod == KeyEvent.VK_WINDOWS) winCheck.setSelected(true);
                    }
                }
            } else if ("typeText".equals(action.type)) {
                typeCombo.setSelectedIndex(1);
                updateFields();
                if (action.text != null) {
                    textField.setText(action.text);
                }
            } else if ("delay".equals(action.type)) {
                typeCombo.setSelectedIndex(2);
                updateFields();
                if (action.ms != null) {
                    delaySpinner.setValue(action.ms);
                }
            }
        }

        private void updateFields() {
            fieldsPanel.removeAll();

            String type = (String) typeCombo.getSelectedItem();
            if ("Key Press".equals(type)) {
                createKeyPressFields();
            } else if ("Type Text".equals(type)) {
                createTypeTextFields();
            } else if ("Delay".equals(type)) {
                createDelayFields();
            }

            fieldsPanel.revalidate();
            fieldsPanel.repaint();
        }

        private void createKeyPressFields() {
            JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));

            JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            keyPanel.add(new JLabel("Key:"));
            keyField = new JTextField(15);
            keyField.setToolTipText("Press any key or type key name (e.g., F1, Enter, Tab)");
            keyPanel.add(keyField);

            JPanel modPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            modPanel.add(new JLabel("Modifiers:"));
            ctrlCheck = new JCheckBox("Ctrl");
            shiftCheck = new JCheckBox("Shift");
            altCheck = new JCheckBox("Alt");
            winCheck = new JCheckBox("Win");
            modPanel.add(ctrlCheck);
            modPanel.add(shiftCheck);
            modPanel.add(altCheck);
            modPanel.add(winCheck);

            panel.add(keyPanel);
            panel.add(modPanel);

            fieldsPanel.add(panel);
        }

        private void createTypeTextFields() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Text:"));
            textField = new JTextField(30);
            panel.add(textField);
            fieldsPanel.add(panel);
        }

        private void createDelayFields() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("Delay (ms):"));
            delaySpinner = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 100));
            panel.add(delaySpinner);
            fieldsPanel.add(panel);
        }

        public MacroExecutor.ActionData getAction() {
            MacroExecutor.ActionData action = new MacroExecutor.ActionData();
            String type = (String) typeCombo.getSelectedItem();

            if ("Key Press".equals(type)) {
                action.type = "keyPress";
                String keyText = keyField.getText().trim();
                if (!keyText.isEmpty()) {
                    action.key = parseKeyCode(keyText);

                    List<Integer> modifiers = new ArrayList<>();
                    if (ctrlCheck.isSelected()) modifiers.add(KeyEvent.VK_CONTROL);
                    if (shiftCheck.isSelected()) modifiers.add(KeyEvent.VK_SHIFT);
                    if (altCheck.isSelected()) modifiers.add(KeyEvent.VK_ALT);
                    if (winCheck.isSelected()) modifiers.add(KeyEvent.VK_WINDOWS);

                    action.modifiers = modifiers.stream().mapToInt(i -> i).toArray();
                }
            } else if ("Type Text".equals(type)) {
                action.type = "typeText";
                action.text = textField.getText();
            } else if ("Delay".equals(type)) {
                action.type = "delay";
                action.ms = (Integer) delaySpinner.getValue();
            }

            return action;
        }

        private int parseKeyCode(String keyText) {
            // Common mappings
            String upper = keyText.toUpperCase();

            // Try to find by key text
            try {
                java.lang.reflect.Field[] fields = KeyEvent.class.getFields();
                for (java.lang.reflect.Field field : fields) {
                    if (field.getName().startsWith("VK_")) {
                        String fieldName = field.getName().substring(3);
                        if (fieldName.equals(upper) || fieldName.replace("_", "").equals(upper.replace(" ", ""))) {
                            return field.getInt(null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // If single character, use extended key code
            if (keyText.length() == 1) {
                return KeyEvent.getExtendedKeyCodeForChar(keyText.charAt(0));
            }

            return KeyEvent.VK_UNDEFINED;
        }
    }
}
