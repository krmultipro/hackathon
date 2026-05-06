const JSON_HEADERS = {
    "Content-Type": "application/json"
};

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
    const response = await fetch(url, {
        credentials: "include",
        ...options
    });
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
            ...(options.headers || {})
        },
        body: JSON.stringify(body),
        ...options
    });
}
