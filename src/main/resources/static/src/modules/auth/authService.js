import { apiPost } from '../../utils/api.js';
import { API_BASE_URL } from '../../utils/constants.js';

const STORAGE_KEY = 'educarena_user';

export async function login(username, password) {
    const response = await apiPost(`${API_BASE_URL}/auth/login`, {
        username,
        password
    });

    const user = {
        username: response.username,
        token: response.token
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return user;
}

export async function register(username, password) {
    const response = await apiPost(`${API_BASE_URL}/auth/register`, {
        username,
        password
    });

    const user = {
        username: response.username,
        token: response.token
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return user;
}

export function getStoredUser() {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
}

export function clearStoredUser() {
    localStorage.removeItem(STORAGE_KEY);
}
