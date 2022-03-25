package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;

public interface EffectHandler {
    /**
     * Gets a random student from the bag.
     * @return random student got from the bag.
     */
    StudentColor getStudentFromBag();

    /**
     * Adds a student to a specific island.
     * @param s student to add
     * @param islandGroupIndex index of the island group.
     * @param islandGroup index of the island in the island group.
     */
    void addStudentToIsland(StudentColor s, int islandGroupIndex, int islandGroup);

    /**
     * Moves the professors to the current player if it has the same number of student in the hall of the current owner.
     * @return map of the professors moves and their original owner's player index.
     */
    EnumMap<StudentColor, Integer> tryGiveProfsToCurrPlayer();

    /**
     * Gives back the professors to the original owners.
     * @param original map of the professor and the original owner's player index.
     */
    void restoreProfsToOriginalPlayer(EnumMap<StudentColor, Integer> original);

    /**
     * Calculates the influence on an island group.
     * @param islandGroupIndex index of the island group.
     */
    void calcInfluenceOnIslandGroup(int islandGroupIndex);

    /**
     * Adds additional movements to the maximum movement of mother nature.
     * @param num additional movement to add.
     */
    void addAdditionalMovement(int num);

    /**
     * Blocks and island group.
     * @param islandGroupIndex island group index to block.
     */
    void blockIslandGroup(int islandGroupIndex);

    /**
     * Ignores the towers when calculating influence this turn.
     */
    void ignoreTowers();

    /**
     * Removes a student from the entrance of current player's school board.
     * @param entranceIndex index of the entrance.
     * @return student at entranceIndex.
     */
    StudentColor getStudentFromEntrance(int entranceIndex);

    /**
     * Adds a student to the entrance of current player's school board.
     * @param entranceIndex index of the entrance.
     */
    void addStudentOnEntrance(int entranceIndex);

    /**
     * Adds additional influence when calculating influence this turn.
     * @param num additional influence to add.
     */
    void addAdditionalInfluence(int num);

    /**
     * Ignores student color when calculating influence this turn.
     * @param s student color to ignore.
     */
    void ignoreStudentColor(StudentColor s);

    /**
     * Removes a student from the current player's hall.
     * @param s student color to remove.
     */
    void removeStudentFromHall(StudentColor s);

    /**
     * Adds a student to the current player's hall.
     * @param s student color to add.
     */
    void addStudentToHall(StudentColor s);

    /**
     * Tries to remove the ideal amount of students from the hall of all players and puts them back in the bag.
     * If there aren't enough students, removes only the available ones.
     * @param s student color of student to remove.
     * @param idealAmount ideal amount of student to remove from halls.
     */
    void tryRemoveStudentsFromHalls(StudentColor s, int idealAmount);
}
