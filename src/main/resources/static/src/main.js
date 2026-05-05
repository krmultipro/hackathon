import config from './config.js';
import { login, register } from './modules/auth/authService.js';

let gameInstance = null;

export function startGame() {
    if (gameInstance) {
        return gameInstance;
    }

    document.body.classList.remove('auth-active');
    document.body.classList.add('game-active');
    gameInstance = new Phaser.Game(config);
    return gameInstance;
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
        authScreen.hidden = true;
    }

    startGame();
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
        setMessage('Identifiants invalides');
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
        setMessage('Inscription impossible');
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

window.addEventListener('load', () => {
    document.body.classList.add('auth-active');
    bindAuthUi();
});
