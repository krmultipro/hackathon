import { apiGet } from '../../utils/api.js';
import { API_BASE_URL } from '../../utils/constants.js';

export async function getGlobalLeaderboard() {
    const players = await apiGet(`${API_BASE_URL}/players`);

    return [...players]
        .sort((left, right) => (right.globalElo || 0) - (left.globalElo || 0))
        .slice(0, 10);
}

export function getPlayerLeaderboard(playerId) {
    return apiGet(`${API_BASE_URL}/elo-ratings/player/${playerId}`);
}

export function getSubjectLeaderboard(subjectId) {
    return apiGet(`${API_BASE_URL}/elo-ratings/subject/${subjectId}`);
}
