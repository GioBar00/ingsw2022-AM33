package it.polimi.ingsw.server.model.enums;

import java.util.EnumSet;

/**
 * This class represents the preset of the game. It contains all the information for setting up the game.
 */
public enum GamePreset {
    /**
     * Game for two players
     */
    TWO(2, 8, 2, 7, 3, 3, EnumSet.of(Tower.WHITE, Tower.BLACK)),
    /**
     * Game for three players
     */
    THREE(3, 6, 3, 9, 4, 4, EnumSet.of(Tower.WHITE, Tower.BLACK, Tower.GREY)),
    /**
     * Game for four players
     */
    FOUR(4, 8, 4, 7, 3, 3, EnumSet.of(Tower.WHITE, Tower.BLACK));

    /**
     * number of players
     */
    private final int playersNumber;

    /**
     * number of towers
     */
    private final int towersNumber;

    /**
     * number of clouds.
     */
    private final int cloudsNumber;

    /**
     * number of slot in the entrance.
     */
    private final int entranceCapacity;

    /**
     * number of slot in the cloud.
     */
    private final int cloudCapacity;

    /**
     * number of the moves a player has to do.
     */
    private final int maxNumMoves;

    /**
     * Set of available towers.
     */
    private final EnumSet<Tower> towers;

    /**
     * Public constructor of the class.
     *
     * @param numPlayers    number of players.
     * @param towersValue   number of towers.
     * @param cloudsNumber  number of clouds.
     * @param entranceValue number of slot in the entrance.
     * @param cloudCapacity number of slot in the cloud.
     * @param maxNumMoves   number of the moves a player has to do.
     * @param towers        set of available towers.
     */

    GamePreset(int numPlayers, int towersValue, int cloudsNumber, int entranceValue, int cloudCapacity, int maxNumMoves, EnumSet<Tower> towers) {
        this.playersNumber = numPlayers;
        this.towersNumber = towersValue;
        this.cloudsNumber = cloudsNumber;
        this.entranceCapacity = entranceValue;
        this.cloudCapacity = cloudCapacity;
        this.maxNumMoves = maxNumMoves;
        this.towers = towers;
    }

    /**
     * Getter for the number of players.
     *
     * @return the number of players.
     */
    public int getPlayersNumber() {
        return playersNumber;
    }

    /**
     * Getter for the number of towers.
     *
     * @return the number of towers.
     */
    public int getEntranceCapacity() {
        return entranceCapacity;
    }

    /**
     * Getter for the number of towers.
     *
     * @return the number of towers.
     */
    public int getTowersNumber() {
        return towersNumber;
    }

    /**
     * Getter for the cloud capacity.
     *
     * @return the cloud capacity.
     */
    public int getCloudCapacity() {
        return cloudCapacity;
    }

    /**
     * Getter for the number of clouds.
     *
     * @return the number of clouds.
     */
    public int getCloudsNumber() {
        return cloudsNumber;
    }

    /**
     * Getter for the number of the moves a player has to do.
     *
     * @return the number of the moves a player has to do.
     */
    public int getMaxNumMoves() {
        return maxNumMoves;
    }

    /**
     * Getter for the set of available towers.
     *
     * @return the set of available towers.
     */
    public EnumSet<Tower> getTowers() {
        return EnumSet.copyOf(towers);
    }

    /**
     * This method returns the GamePreset corresponding to the number of players.
     *
     * @param number the number of players.
     * @return the GamePreset corresponding to the number of players.
     */
    public static GamePreset getFromNumber(int number) {
        return switch (number) {
            case 2 -> GamePreset.TWO;
            case 3 -> GamePreset.THREE;
            case 4 -> GamePreset.FOUR;
            default -> null;
        };
    }
}
