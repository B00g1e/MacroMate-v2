// Global state
let isConnected = false;
let buttons = [];
let autoReconnect = false;
let reconnectAttempts = 0;
let maxReconnectAttempts = 5;
let reconnectTimer = null;

// Neon color gradients for dynamic button styling
const neonGradients = [
    'linear-gradient(135deg, #ff006e 0%, #ff00ff 100%)',
    'linear-gradient(135deg, #00f5ff 0%, #0080ff 100%)',
    'linear-gradient(135deg, #7800ff 0%, #ff00aa 100%)',
    'linear-gradient(135deg, #00ff88 0%, #00ddff 100%)',
    'linear-gradient(135deg, #ff0055 0%, #ff6600 100%)',
    'linear-gradient(135deg, #00ffff 0%, #00ff88 100%)',
    'linear-gradient(135deg, #ff00ff 0%, #aa00ff 100%)',
    'linear-gradient(135deg, #ffaa00 0%, #ff0055 100%)',
    'linear-gradient(135deg, #00ddff 0%, #7800ff 100%)',
    'linear-gradient(135deg, #ff0088 0%, #ff00ff 100%)',
    'linear-gradient(135deg, #00ff00 0%, #00ffaa 100%)',
    'linear-gradient(135deg, #ff5500 0%, #ff00aa 100%)'
];

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
const autoReconnectToggle = document.getElementById('auto-reconnect');
const themeOptions = document.querySelectorAll('.theme-option');

// Load saved settings
const savedUrl = localStorage.getItem('serverUrl');
if (savedUrl) {
    serverUrlInput.value = savedUrl;
}

const savedAutoReconnect = localStorage.getItem('autoReconnect');
if (savedAutoReconnect === 'true') {
    autoReconnect = true;
    autoReconnectToggle.checked = true;
}

const savedTheme = localStorage.getItem('theme') || 'neon';
applyTheme(savedTheme);

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

    // Auto-reconnect logic
    if (autoReconnect && reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        const delay = Math.min(1000 * reconnectAttempts, 5000);
        Android.showToast(`Reconnecting in ${delay/1000}s... (${reconnectAttempts}/${maxReconnectAttempts})`);

        reconnectTimer = setTimeout(() => {
            const serverUrl = serverUrlInput.value.trim();
            if (serverUrl) {
                Android.connect(serverUrl);
            }
        }, delay);
    }
};

window.onError = function(error) {
    Android.showToast('Connection error: ' + error);

    // Auto-reconnect on error
    if (autoReconnect && reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        const delay = Math.min(1000 * reconnectAttempts, 5000);

        reconnectTimer = setTimeout(() => {
            const serverUrl = serverUrlInput.value.trim();
            if (serverUrl) {
                Android.connect(serverUrl);
            }
        }, delay);
    }
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

// Render buttons with dynamic neon effects
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

        // Apply dynamic neon gradient to icon
        const gradient = neonGradients[index % neonGradients.length];
        iconEl.style.background = gradient;

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

        // Enhanced click handler with neon flash effect
        buttonEl.addEventListener('click', () => {
            if (isConnected) {
                Android.sendButtonPress(button.id || `button_${index}`, button.name);

                // Neon flash effect
                buttonEl.style.borderColor = 'rgba(255, 0, 255, 0.9)';
                buttonEl.style.boxShadow = '0 0 40px rgba(255, 0, 255, 0.8), 0 0 60px rgba(255, 0, 255, 0.4), inset 0 0 30px rgba(255, 0, 255, 0.2)';

                setTimeout(() => {
                    buttonEl.style.borderColor = '';
                    buttonEl.style.boxShadow = '';
                }, 200);
            } else {
                Android.showToast('Not connected to server');

                // Red flash for error
                buttonEl.style.borderColor = 'rgba(255, 0, 85, 0.9)';
                buttonEl.style.boxShadow = '0 0 30px rgba(255, 0, 85, 0.6)';

                setTimeout(() => {
                    buttonEl.style.borderColor = '';
                    buttonEl.style.boxShadow = '';
                }, 200);
            }
        });

        // Add subtle hover glow effect for touch devices
        let touchTimer;
        buttonEl.addEventListener('touchstart', () => {
            touchTimer = setTimeout(() => {
                buttonEl.style.borderColor = 'rgba(120, 0, 255, 0.6)';
                buttonEl.style.boxShadow = '0 0 25px rgba(120, 0, 255, 0.4)';
            }, 100);
        });

        buttonEl.addEventListener('touchend', () => {
            clearTimeout(touchTimer);
            setTimeout(() => {
                buttonEl.style.borderColor = '';
                buttonEl.style.boxShadow = '';
            }, 150);
        });

        buttonGrid.appendChild(buttonEl);
    });
}

// Get default icon based on index (FontAwesome)
function getDefaultIcon(index) {
    const icons = [
        'fas fa-keyboard',      // Keyboard
        'fas fa-computer-mouse', // Mouse
        'fas fa-clipboard',     // Clipboard
        'fas fa-scissors',      // Scissors
        'fas fa-folder',        // Folder
        'fas fa-volume-high',   // Volume
        'fas fa-music',         // Music
        'fas fa-desktop',       // Desktop
        'fas fa-lock',          // Lock
        'fas fa-gear',          // Settings
        'fas fa-arrows-rotate', // Rotate
        'fas fa-globe'          // Globe
    ];
    return `<i class="${icons[index % icons.length]}"></i>`;
}

// Generate default buttons with FontAwesome icons
function generateDefaultButtons() {
    return [
        { id: 'btn_1', name: 'Copy', icon: '<i class="fas fa-copy"></i>' },
        { id: 'btn_2', name: 'Paste', icon: '<i class="fas fa-paste"></i>' },
        { id: 'btn_3', name: 'Screenshot', icon: '<i class="fas fa-camera"></i>' },
        { id: 'btn_4', name: 'Select All', icon: '<i class="fas fa-check-double"></i>' },
        { id: 'btn_5', name: 'Save', icon: '<i class="fas fa-floppy-disk"></i>' },
        { id: 'btn_6', name: 'Find', icon: '<i class="fas fa-magnifying-glass"></i>' },
        { id: 'btn_7', name: 'Alt+Tab', icon: '<i class="fas fa-window-restore"></i>' },
        { id: 'btn_8', name: 'Show Desktop', icon: '<i class="fas fa-desktop"></i>' },
        { id: 'btn_9', name: 'Task Mgr', icon: '<i class="fas fa-list-check"></i>' },
        { id: 'btn_10', name: 'Lock PC', icon: '<i class="fas fa-lock"></i>' },
        { id: 'btn_11', name: 'Undo', icon: '<i class="fas fa-rotate-left"></i>' },
        { id: 'btn_12', name: 'Redo', icon: '<i class="fas fa-rotate-right"></i>' }
    ];
}

// Auto-reconnect toggle handler
autoReconnectToggle.addEventListener('change', (e) => {
    autoReconnect = e.target.checked;
    localStorage.setItem('autoReconnect', autoReconnect);

    if (autoReconnect) {
        Android.showToast('Auto-reconnect enabled');
    } else {
        Android.showToast('Auto-reconnect disabled');
        // Clear any pending reconnect
        if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        }
        reconnectAttempts = 0;
    }
});

// Theme switching
themeOptions.forEach(option => {
    option.addEventListener('click', () => {
        const theme = option.dataset.theme;
        applyTheme(theme);
        localStorage.setItem('theme', theme);
        Android.showToast(`Theme changed to ${theme}`);
    });
});

function applyTheme(theme) {
    document.body.setAttribute('data-theme', theme);

    // Update active theme option
    themeOptions.forEach(opt => {
        opt.classList.remove('active');
        if (opt.dataset.theme === theme) {
            opt.classList.add('active');
        }
    });
}

// Reset reconnect attempts on successful connection
window.onConnected = (function() {
    const originalOnConnected = window.onConnected;
    return function() {
        reconnectAttempts = 0;
        if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        }
        if (originalOnConnected) originalOnConnected();
    };
})();

// Initialize the app
function init() {
    updateConnectionStatus();
    renderButtons();
}

// Start the app
init();
