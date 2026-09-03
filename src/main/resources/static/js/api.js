const API_BASE = '/api';
// wrap fetch to get api, throw eerror for branching
async function apiFetch(path, options) {
    const res = await fetch(API_BASE + path, options);
    if (!res.ok) {
        const message = await res.text().catch(() => 'Request failed');
        const err = new Error(message || ('Request failed (' + res.status + ')'));
        err.status = res.status;
        throw err;
    }
    const contentType = res.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return res.json();
    }
    return res.text();
}

function apiGet(path) {
    return apiFetch(path, { method: 'GET' });
}

function apiPost(path, body) {
    return apiFetch(path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
}

function apiPut(path, body) {
    return apiFetch(path, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
}