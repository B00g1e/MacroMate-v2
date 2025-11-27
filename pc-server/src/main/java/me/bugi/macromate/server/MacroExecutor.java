package me.bugi.macromate.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacroExecutor {
    private Robot robot;
    private ButtonConfig config;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String configPath = "button-config.json";

    public MacroExecutor() {
        try {
            robot = new Robot();
            robot.setAutoDelay(50);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        loadConfig();
    }

    private void loadConfig() {
        File file = new File(configPath);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = gson.fromJson(reader, ButtonConfig.class);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create default configuration
        config = createDefaultConfig();
        saveConfig();
    }

    private ButtonConfig createDefaultConfig() {
        ButtonConfig cfg = new ButtonConfig();
        cfg.buttons = new ArrayList<>();

        // Copy
        cfg.buttons.add(new ButtonData("btn_1", "Copy",
                createKeyPressAction(KeyEvent.VK_C, KeyEvent.VK_CONTROL)));

        // Paste
        cfg.buttons.add(new ButtonData("btn_2", "Paste",
                createKeyPressAction(KeyEvent.VK_V, KeyEvent.VK_CONTROL)));

        // Screenshot (PrintScreen)
        cfg.buttons.add(new ButtonData("btn_3", "Screenshot",
                createKeyPressAction(KeyEvent.VK_PRINTSCREEN)));

        // Select All
        cfg.buttons.add(new ButtonData("btn_4", "Select All",
                createKeyPressAction(KeyEvent.VK_A, KeyEvent.VK_CONTROL)));

        // Save
        cfg.buttons.add(new ButtonData("btn_5", "Save",
                createKeyPressAction(KeyEvent.VK_S, KeyEvent.VK_CONTROL)));

        // Find
        cfg.buttons.add(new ButtonData("btn_6", "Find",
                createKeyPressAction(KeyEvent.VK_F, KeyEvent.VK_CONTROL)));

        // Alt+Tab
        cfg.buttons.add(new ButtonData("btn_7", "Alt+Tab",
                createKeyPressAction(KeyEvent.VK_TAB, KeyEvent.VK_ALT)));

        // Windows+D (Show Desktop)
        cfg.buttons.add(new ButtonData("btn_8", "Show Desktop",
                createKeyPressAction(KeyEvent.VK_D, KeyEvent.VK_WINDOWS)));

        // Ctrl+Shift+Esc (Task Manager)
        cfg.buttons.add(new ButtonData("btn_9", "Task Manager",
                createKeyPressAction(KeyEvent.VK_ESCAPE, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT)));

        // Windows+L (Lock PC)
        cfg.buttons.add(new ButtonData("btn_10", "Lock PC",
                createKeyPressAction(KeyEvent.VK_L, KeyEvent.VK_WINDOWS)));

        // Ctrl+Z (Undo)
        cfg.buttons.add(new ButtonData("btn_11", "Undo",
                createKeyPressAction(KeyEvent.VK_Z, KeyEvent.VK_CONTROL)));

        // Ctrl+Y (Redo)
        cfg.buttons.add(new ButtonData("btn_12", "Redo",
                createKeyPressAction(KeyEvent.VK_Y, KeyEvent.VK_CONTROL)));

        return cfg;
    }

    private List<ActionData> createKeyPressAction(int key, int... modifiers) {
        List<ActionData> actions = new ArrayList<>();
        ActionData action = new ActionData();
        action.type = "keyPress";
        action.key = key;
        action.modifiers = modifiers;
        actions.add(action);
        return actions;
    }

    public void execute(String buttonId) {
        ButtonData button = findButton(buttonId);
        if (button == null) {
            System.err.println("Button not found: " + buttonId);
            return;
        }

        System.out.println("Executing macro: " + button.name);

        for (ActionData action : button.actions) {
            executeAction(action);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeAction(ActionData action) {
        if (robot == null) return;

        switch (action.type) {
            case "keyPress":
                executeKeyPress(action);
                break;
            case "typeText":
                executeTypeText(action);
                break;
            case "delay":
                executeDelay(action);
                break;
            default:
                System.err.println("Unknown action type: " + action.type);
        }
    }

    private void executeKeyPress(ActionData action) {
        // Press modifiers
        if (action.modifiers != null) {
            for (int modifier : action.modifiers) {
                robot.keyPress(modifier);
            }
        }

        // Press and release main key
        robot.keyPress(action.key);
        robot.keyRelease(action.key);

        // Release modifiers in reverse order
        if (action.modifiers != null) {
            for (int i = action.modifiers.length - 1; i >= 0; i--) {
                robot.keyRelease(action.modifiers[i]);
            }
        }
    }

    private void executeTypeText(ActionData action) {
        if (action.text == null) return;

        for (char c : action.text.toCharArray()) {
            typeChar(c);
            robot.delay(10);
        }
    }

    private void typeChar(char c) {
        boolean uppercase = Character.isUpperCase(c);
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

        if (keyCode != KeyEvent.VK_UNDEFINED) {
            if (uppercase) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }

            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);

            if (uppercase) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }

    private void executeDelay(ActionData action) {
        try {
            Thread.sleep(action.ms != null ? action.ms : 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ButtonData findButton(String buttonId) {
        for (ButtonData button : config.buttons) {
            if (button.id.equals(buttonId)) {
                return button;
            }
        }
        return null;
    }

    public ButtonConfig getButtonConfig() {
        return config;
    }

    public String getButtonConfigJson() {
        return gson.toJson(config);
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configPath)) {
            gson.toJson(config, writer);
            System.out.println("Configuration saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(ButtonConfig config) {
        this.config = config;
        saveConfig();
    }

    // Data classes
    public static class ButtonConfig {
        public List<ButtonData> buttons;
    }

    public static class ButtonData {
        public String id;
        public String name;
        public String icon;        // Emoji ili unicode icon
        public String image;       // URL slike
        public List<ActionData> actions;

        public ButtonData() {}

        public ButtonData(String id, String name, List<ActionData> actions) {
            this.id = id;
            this.name = name;
            this.actions = actions;
        }
    }

    public static class ActionData {
        public String type;
        public Integer key;
        public int[] modifiers;
        public String text;
        public Integer ms;
    }
}
