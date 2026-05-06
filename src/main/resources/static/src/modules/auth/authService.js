import { apiGet, apiPost } from '../../utils/api.js';
import { API_BASE_URL } from '../../utils/constants.js';

export async function login(username, password) {
    try {
        const response = await apiPost(`${API_BASE_URL}/auth/login`, {
            username,
            password
        });
        return { username: response.username };
    } catch (error) {
        throw new Error(error.message || 'Identifiants invalides');
    }
}

export async function register(username, password) {
    try {
        const response = await apiPost(`${API_BASE_URL}/auth/register`, {
            username,
            password
        });
        return { username: response.username };
    } catch (error) {
        throw new Error(error.message || 'Inscription impossible');
    }
}

export async function checkSession() {
    try {
        const response = await apiGet(`${API_BASE_URL}/auth/me`);
        return { username: response.username };
    } catch {
        return null;
    }
}

export async function logoutBackend() {
    try {
        await apiPost(`${API_BASE_URL}/auth/logout`, {});
    } catch {
        // ignore, le cookie sera supprimé côté client si besoin
    }
}
