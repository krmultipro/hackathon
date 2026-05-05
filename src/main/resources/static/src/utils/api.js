const DEFAULT_HEADERS = {
    'Content-Type': 'application/json'
};

async function request(method, url, body, options = {}) {
    const response = await fetch(url, {
        method,
        headers: {
            ...DEFAULT_HEADERS,
            ...(options.headers || {})
        },
        body: body !== undefined ? JSON.stringify(body) : undefined
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return response.json();
    }

    return response.text();
}

export function apiGet(url, options) {
    return request('GET', url, undefined, options);
}

export function apiPost(url, body, options) {
    return request('POST', url, body, options);
}

