package it.polimi.ingsw.enums;

public enum PlayerNumber {
    TWO(2,8,7, 3), THREE(3,6,9, 4), FOUR(4,8,7, 3);

    private final int playersValue;
    private final int towersValue;
    private final int entranceValue;
    private final int cloudCapacity;

    PlayerNumber(int numPlayers, int towersValue, int entranceValue, int cloudCapacity){
        this.playersValue = numPlayers;
        this.towersValue = towersValue;
        this.entranceValue = entranceValue;
        this.cloudCapacity = cloudCapacity;
    }

    public int getPlayersValue() { return playersValue; }

    public int getEntranceValue() { return entranceValue; }

    public int getTowersValue() { return towersValue; }

    public int getCloudCapacity() { return cloudCapacity; }
}
