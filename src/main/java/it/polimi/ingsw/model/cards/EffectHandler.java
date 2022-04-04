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
     * @param islandIndex index of the island in the island group.
     * @return if the add was successful.
     */
    boolean addStudentToIsland(StudentColor s, int islandGroupIndex, int islandIndex);

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
     * @return if the calcInfluence when well.
     */
    boolean calcInfluenceOnIslandGroup(int islandGroupIndex);

    /**
     * Adds additional movements to the maximum movement of mother nature.
     * @param num additional movement to add.
     */
    void addAdditionalMovement(int num);

    /**
     * Blocks and island group.
     * @param islandGroupIndex island group index to block.
     */
    boolean blockIslandGroup(int islandGroupIndex);

    /**
     * Ignores the towers when calculating influence this turn.
     * @param ignore ignore towers.
     */
    void ignoreTowers(boolean ignore);

    /**
     * Removes a student from the entrance of current player's school board.
     * @param entranceIndex index of the entrance.
     * @return student at entranceIndex.
     */
    StudentColor popStudentFromEntrance(int entranceIndex);

    /**
     * Adds a student to the entrance of current player's school board.
     * @param entranceIndex index of the entrance.
     * @return if the student was added successfully.
     */
    boolean addStudentOnEntrance(StudentColor s, int entranceIndex);

    /**
     * Adds additional influence when calculating influence this turn.
     * @param num additional influence to add.
     */
    void addAdditionalInfluence(int num);

    /**
     * Ignores student color when calculating influence this turn.
     * @param s student color to ignore.
     * @param ignore ignore student color.
     */
    void ignoreStudentColor(StudentColor s, boolean ignore);

    /**
     * Removes a student from the current player's hall.
     * @param s student color to remove.
     * @return if the remove was successful.
     */
    boolean removeStudentFromHall(StudentColor s);

    /**
     * Adds a student to the current player's hall.
     * @param s student color to add.
     * @return if the add was successful.
     */
    boolean addStudentToHall(StudentColor s);

    /**
     * Tries to remove the ideal amount of students from the hall of all players and puts them back in the bag.
     * If there aren't enough students, removes only the available ones.
     * @param s student color of student to remove.
     * @param idealAmount ideal amount of student to remove from halls.
     */
    void tryRemoveStudentsFromHalls(StudentColor s, int idealAmount);
}
