// Global state
let isConnected = false;
let buttons = [];

// DOM Elements
const settingsBtn = document.getElementById('settings-btn');
const settingsPanel = document.getElementById('settings-panel');
const closeSettingsBtn = document.getElementById('close-settings');
const connectBtn = document.getElementById('connect-btn');
const disconnectBtn = document.getElementById('disconnect-btn');
const serverUrlInput = document.getElementById('server-url');
const buttonGrid = document.getElementById('button-grid');
const statusIndicator = document.getElementById('status-indicator');
const statusText = document.getElementById('status-text');

// Load saved server URL
const savedUrl = localStorage.getItem('serverUrl');
if (savedUrl) {
    serverUrlInput.value = savedUrl;
}

// Settings panel toggle
settingsBtn.addEventListener('click', () => {
    settingsPanel.classList.add('active');
});

closeSettingsBtn.addEventListener('click', () => {
    settingsPanel.classList.remove('active');
});

settingsPanel.addEventListener('click', (e) => {
    if (e.target === settingsPanel) {
        settingsPanel.classList.remove('active');
    }
});

// Connection handlers
connectBtn.addEventListener('click', () => {
    const serverUrl = serverUrlInput.value.trim();
    if (serverUrl) {
        localStorage.setItem('serverUrl', serverUrl);
        Android.connect(serverUrl);
        Android.showToast('Connecting to server...');
    } else {
        Android.showToast('Please enter a server URL');
    }
});

disconnectBtn.addEventListener('click', () => {
    Android.disconnect();
    Android.showToast('Disconnecting...');
});

// Callbacks from native Android code
window.onConnected = function() {
    isConnected = true;
    updateConnectionStatus();
    Android.showToast('Connected to server');
};

window.onDisconnected = function() {
    isConnected = false;
    updateConnectionStatus();
    Android.showToast('Disconnected from server');
};

window.onError = function(error) {
    Android.showToast('Connection error: ' + error);
};

window.onButtonConfig = function(configJson) {
    try {
        const config = JSON.parse(configJson);
        buttons = config.buttons || [];
        renderButtons();
    } catch (e) {
        console.error('Error parsing button config:', e);
    }
};

// Update UI based on connection status
function updateConnectionStatus() {
    if (isConnected) {
        statusIndicator.classList.remove('status-disconnected');
        statusIndicator.classList.add('status-connected');
        statusText.textContent = 'Connected';
        connectBtn.disabled = true;
        disconnectBtn.disabled = false;
    } else {
        statusIndicator.classList.remove('status-connected');
        statusIndicator.classList.add('status-disconnected');
        statusText.textContent = 'Disconnected';
        connectBtn.disabled = false;
        disconnectBtn.disabled = true;
    }
}

// Render buttons
function renderButtons() {
    buttonGrid.innerHTML = '';

    if (buttons.length === 0) {
        // Show default buttons if no config received
        buttons = generateDefaultButtons();
    }

    buttons.forEach((button, index) => {
        const buttonEl = document.createElement('button');
        buttonEl.className = 'macro-button';

        // Create icon element
        const iconEl = document.createElement('div');
        iconEl.className = 'macro-button-icon';

        // If button has image URL, show it; otherwise show emoji/icon
        if (button.image) {
            const imgEl = document.createElement('img');
            imgEl.src = button.image;
            imgEl.alt = button.name;
            imgEl.onerror = () => {
                // Fallback to emoji if image fails to load
                iconEl.innerHTML = button.icon || getDefaultIcon(index);
            };
            iconEl.appendChild(imgEl);
        } else {
            // Show emoji/icon or default
            iconEl.innerHTML = button.icon || getDefaultIcon(index);
        }

        // Create label element
        const labelEl = document.createElement('div');
        labelEl.className = 'macro-button-label';
        labelEl.textContent = button.name;

        buttonEl.appendChild(iconEl);
        buttonEl.appendChild(labelEl);

        buttonEl.addEventListener('click', () => {
            if (isConnected) {
                Android.sendButtonPress(button.id || `button_${index}`, button.name);
                // Visual feedback
                buttonEl.style.background = '#3a3a3a';
                setTimeout(() => {
                    buttonEl.style.background = '#2a2a2a';
                }, 150);
            } else {
                Android.showToast('Not connected to server');
            }
        });

        buttonGrid.appendChild(buttonEl);
    });
}

// Get default icon based on index
function getDefaultIcon(index) {
    const icons = ['⌨️', '🖱️', '📋', '✂️', '📁', '🔊', '🎵', '🖥️', '🔒', '⚙️', '🔄', '🌐'];
    return icons[index % icons.length];
}

// Generate default buttons
function generateDefaultButtons() {
    return [
        { id: 'btn_1', name: 'Copy', icon: '📋' },
        { id: 'btn_2', name: 'Paste', icon: '📄' },
        { id: 'btn_3', name: 'Screenshot', icon: '📸' },
        { id: 'btn_4', name: 'Select All', icon: '✨' },
        { id: 'btn_5', name: 'Save', icon: '💾' },
        { id: 'btn_6', name: 'Find', icon: '🔍' },
        { id: 'btn_7', name: 'Alt+Tab', icon: '🔄' },
        { id: 'btn_8', name: 'Show Desktop', icon: '🖥️' },
        { id: 'btn_9', name: 'Task Mgr', icon: '⚙️' },
        { id: 'btn_10', name: 'Lock PC', icon: '🔒' },
        { id: 'btn_11', name: 'Undo', icon: '↩️' },
        { id: 'btn_12', name: 'Redo', icon: '↪️' }
    ];
}

// Initialize the app
function init() {
    updateConnectionStatus();
    renderButtons();
}

// Start the app
init();
