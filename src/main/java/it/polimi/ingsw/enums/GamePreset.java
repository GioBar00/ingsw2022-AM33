package it.polimi.ingsw.enums;

public enum GamePreset {
    TWO(2,8, 2,7, 3, 3),
    THREE(3,6, 3,9, 4, 4),
    FOUR(4,8, 4,7, 3, 3);

    private final int playersNumber;
    private final int towersNumber;
    private final int cloudsNumber;
    private final int entranceCapacity;
    private final int cloudCapacity;
    private final int maxNumMoves;

    GamePreset(int numPlayers, int towersValue, int cloudsNumber, int entranceValue, int cloudCapacity, int maxNumMoves){
        this.playersNumber = numPlayers;
        this.towersNumber = towersValue;
        this.cloudsNumber = cloudsNumber;
        this.entranceCapacity = entranceValue;
        this.cloudCapacity = cloudCapacity;
        this.maxNumMoves = maxNumMoves;
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
}
