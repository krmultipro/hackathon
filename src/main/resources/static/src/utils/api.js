const JSON_HEADERS = {
    "Content-Type": "application/json"
};

const AUTH_STORAGE_KEY = "educarena_user";

function getAuthHeaders() {
    const rawUser = localStorage.getItem(AUTH_STORAGE_KEY);

    if (!rawUser) {
        return {};
    }

    try {
        const user = JSON.parse(rawUser);
        if (!user?.token) {
            return {};
        }

        return {
            Authorization: `Bearer ${user.token}`
        };
    } catch {
        return {};
    }
}

async function parseJsonResponse(response) {
    const rawText = await response.text();

    if (!rawText) {
        return {};
    }

    try {
        return JSON.parse(rawText);
    } catch {
        return { message: rawText };
    }
}

async function request(url, options = {}) {
    const response = await fetch(url, options);
    const data = await parseJsonResponse(response);

    if (!response.ok) {
        const message = data.message || `HTTP ${response.status}`;
        throw new Error(message);
    }

    return data;
}

export function apiGet(url, options = {}) {
    return request(url, {
        method: "GET",
        headers: {
            ...getAuthHeaders(),
            ...(options.headers || {})
        },
        ...options
    });
}

export function apiPost(url, body, options = {}) {
    return request(url, {
        method: "POST",
        headers: {
            ...JSON_HEADERS,
            ...getAuthHeaders(),
            ...(options.headers || {})
        },
        body: JSON.stringify(body),
        ...options
    });
}
