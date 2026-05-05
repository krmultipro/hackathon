import { apiGet, apiPost } from '../../utils/api.js';
import { API_BASE_URL } from '../../utils/constants.js';

const MATCHES_URL = `${API_BASE_URL}/matches`;

export function createMatch(payload) {
    return apiPost(MATCHES_URL, payload);
}

export function getMatchById(matchId) {
    return apiGet(`${MATCHES_URL}/${matchId}`);
}

export function submitMatchAnswer(payload) {
    return apiPost(`${API_BASE_URL}/match-answers`, payload);
}

