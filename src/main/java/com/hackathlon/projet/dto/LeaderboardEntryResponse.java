package com.hackathlon.projet.dto;

public class LeaderboardEntryResponse {

    private final Long playerId;
    private final String username;
    private final Integer globalElo;
    private final int rank;

    public LeaderboardEntryResponse(Long playerId, String username, Integer globalElo, int rank) {
        this.playerId = playerId;
        this.username = username;
        this.globalElo = globalElo;
        this.rank = rank;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public Integer getGlobalElo() {
        return globalElo;
    }

    public int getRank() {
        return rank;
    }
}
