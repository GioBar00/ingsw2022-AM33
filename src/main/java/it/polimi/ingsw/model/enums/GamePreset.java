package it.polimi.ingsw.model.enums;

import java.util.EnumSet;

public enum GamePreset {
    TWO(2,8, 2,7, 3, 3, EnumSet.of(Tower.WHITE, Tower.BLACK)),
    THREE(3,6, 3,9, 4, 4, EnumSet.of(Tower.WHITE, Tower.BLACK, Tower.GREY)),
    FOUR(4,8, 4,7, 3, 3, EnumSet.of(Tower.WHITE, Tower.BLACK));

    private final int playersNumber;
    private final int towersNumber;
    private final int cloudsNumber;
    private final int entranceCapacity;
    private final int cloudCapacity;
    private final int maxNumMoves;
    private final EnumSet<Tower> towers;

    GamePreset(int numPlayers, int towersValue, int cloudsNumber, int entranceValue, int cloudCapacity, int maxNumMoves, EnumSet<Tower> towers){
        this.playersNumber = numPlayers;
        this.towersNumber = towersValue;
        this.cloudsNumber = cloudsNumber;
        this.entranceCapacity = entranceValue;
        this.cloudCapacity = cloudCapacity;
        this.maxNumMoves = maxNumMoves;
        this.towers = towers;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public int getEntranceCapacity() {
        return entranceCapacity;
    }

    public int getTowersNumber() {
        return towersNumber;
    }

    public int getCloudCapacity() {
        return cloudCapacity;
    }

    public int getCloudsNumber() {
        return cloudsNumber;
    }

    public int getMaxNumMoves() {
        return maxNumMoves;
    }

    public EnumSet<Tower> getTowers() {
        return EnumSet.copyOf(towers);
    }
}
