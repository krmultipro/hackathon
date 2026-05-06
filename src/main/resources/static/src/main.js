import config from './config.js';
import { checkSession, login, logoutBackend, register } from './modules/auth/authService.js';

export function startGame() {
    if (!window.game) {
        window.game = new Phaser.Game(config);
    }
    return window.game;
}

function setMessage(message, type = 'error') {
    const messageNode = document.getElementById('auth-message');
    if (!messageNode) {
        return;
    }

    messageNode.textContent = message || '';
    messageNode.className = `auth-message ${type}`;
}

function setLoading(isLoading) {
    const loginButton = document.getElementById('login-button');
    const registerButton = document.getElementById('register-button');

    if (loginButton) {
        loginButton.disabled = isLoading;
    }

    if (registerButton) {
        registerButton.disabled = isLoading;
    }
}

function getCredentials() {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    return {
        username: usernameInput ? usernameInput.value.trim() : '',
        password: passwordInput ? passwordInput.value : ''
    };
}

function showGame() {
    const authScreen = document.getElementById('auth-screen');
    if (authScreen) {
        authScreen.style.display = 'none';
    }

    document.body.classList.remove('auth-active');
    document.body.classList.add('game-started');
    startGame();
}

export async function logout() {
    const authScreen = document.getElementById('auth-screen');
    const passwordInput = document.getElementById('password');

    await logoutBackend();

    if (window.game) {
        window.game.destroy(true);
        window.game = null;
    }

    if (authScreen) {
        authScreen.style.display = 'flex';
    }

    if (passwordInput) {
        passwordInput.value = '';
    }

    setMessage('', 'error');
    document.body.classList.remove('game-started');
    document.body.classList.add('auth-active');
}

async function handleLogin() {
    const { username, password } = getCredentials();

    if (!username || !password) {
        setMessage('Nom d’utilisateur et mot de passe requis');
        return;
    }

    try {
        setLoading(true);
        setMessage('');
        await login(username, password);
        showGame();
    } catch (error) {
        setMessage(error.message || 'Identifiants invalides');
    } finally {
        setLoading(false);
    }
}

async function handleRegister() {
    const { username, password } = getCredentials();

    if (!username || !password) {
        setMessage('Nom d’utilisateur et mot de passe requis');
        return;
    }

    try {
        setLoading(true);
        setMessage('');
        await register(username, password);
        showGame();
    } catch (error) {
        setMessage(error.message || 'Inscription impossible');
    } finally {
        setLoading(false);
    }
}

function bindAuthUi() {
    const loginButton = document.getElementById('login-button');
    const registerButton = document.getElementById('register-button');
    const passwordInput = document.getElementById('password');

    if (loginButton) {
        loginButton.addEventListener('click', handleLogin);
    }

    if (registerButton) {
        registerButton.addEventListener('click', handleRegister);
    }

    if (passwordInput) {
        passwordInput.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                handleLogin();
            }
        });
    }
}

window.addEventListener('load', async () => {
    window.logout = logout;
    document.body.classList.add('auth-active');
    bindAuthUi();

    const user = await checkSession();
    if (user) {
        showGame();
    }
});
