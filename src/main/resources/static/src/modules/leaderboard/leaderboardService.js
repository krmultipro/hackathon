import { apiGet } from '../../utils/api.js';
import { API_BASE_URL } from '../../utils/constants.js';

export function getPlayerLeaderboard(playerId) {
    return apiGet(`${API_BASE_URL}/elo-ratings/player/${playerId}`);
}

export function getSubjectLeaderboard(subjectId) {
    return apiGet(`${API_BASE_URL}/elo-ratings/subject/${subjectId}`);
}

