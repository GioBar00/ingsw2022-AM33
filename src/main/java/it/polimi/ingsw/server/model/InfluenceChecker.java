package it.polimi.ingsw.server.model;

/**
 * Interface for the classes that implement a checkInfluence method
 */
public interface InfluenceChecker {

    /**
     * The method calculated the Influence that each Player has on a specific IslandGroup, selects the most influential
     * Player and, in the casa that the Tower currently present on the IslandGroup is not the same one of the most
     * influential Player, swaps the tower with the correct one, then proceeds to check whether the IslandGroup
     * can be merged with the ones next to it.
     * In order to calculate the influence of the various Players, the method calls an overloaded version
     * of checkInfluence.
     *
     * @param islandGroupIndex of the IslandGroup in question
     */
    void checkInfluence(int islandGroupIndex);
}
