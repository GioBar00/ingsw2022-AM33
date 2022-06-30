package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for the effect of the character cards.
 */
public interface EffectHandler {
    /**
     * Gets a random student from the bag.
     *
     * @return random student got from the bag.
     */
    StudentColor getStudentFromBag();

    /**
     * Adds a student to a specific island.
     *
     * @param s                student to add
     * @param islandGroupIndex index of the island group.
     * @return if the add was successful.
     */
    boolean addStudentToIsland(StudentColor s, int islandGroupIndex);

    /**
     * Moves the professors to the current player if it has the same number of student in the hall of the current owner.
     *
     * @return map of the professors moves and their original owner's player index.
     */
    EnumMap<StudentColor, Integer> tryGiveProfsToCurrPlayer();

    /**
     * Gives back the professors to the original owners.
     *
     * @param original map of the professor and the original owner's player index.
     */
    void restoreProfsToOriginalPlayer(EnumMap<StudentColor, Integer> original);

    /**
     * Calculates the influence on an island group.
     *
     * @param islandGroupIndex index of the island group.
     * @return if the calcInfluence when well.
     */
    boolean calcInfluenceOnIslandGroup(int islandGroupIndex);

    /**
     * Adds additional movements to the maximum movement of mother nature.
     *
     * @param num additional movement to add.
     */
    void addAdditionalMovement(int num);

    /**
     * Blocks and island group.
     *
     * @param islandGroupIndex island group index to block.
     * @return true if the method was executed correctly.
     */
    boolean blockIslandGroup(int islandGroupIndex);

    /**
     * Ignores the towers when calculating influence this turn.
     *
     * @param ignore ignore towers.
     */
    void ignoreTowers(boolean ignore);

    /**
     * Removes a student from the entrance of current player's school board.
     *
     * @param entranceIndex index of the entrance.
     * @return student at entranceIndex.
     */
    StudentColor popStudentFromEntrance(int entranceIndex);

    /**
     * Gets the students in the entrance of the current player's school board.
     *
     * @return the students in the entrance.
     */
    List<StudentColor> getStudentsInEntrance();

    /**
     * Adds a student to the entrance of current player's school board.
     *
     * @param s color of the student to be added to the entrance.
     * @param entranceIndex index of the entrance.
     * @return if the student was added successfully.
     */
    boolean addStudentOnEntrance(StudentColor s, int entranceIndex);

    /**
     * Adds additional influence when calculating influence this turn.
     *
     * @param num additional influence to add.
     */
    void addAdditionalInfluence(int num);

    /**
     * Removes a student from the current player's hall.
     *
     * @param s student color to remove.
     * @return if the remove was successful.
     */
    boolean removeStudentFromHall(StudentColor s);

    /**
     * Adds a student to the current player's hall.
     *
     * @param s student color to add.
     * @return if the add was successful.
     */
    boolean addStudentToHall(StudentColor s);

    /**
     * Gets the number of students of a specific color in the hall.
     *
     * @param s color of the student.
     * @return number of students in the hall.
     */
    int getStudentsInHall(StudentColor s);

    /**
     * @return the student hall of the current player.
     */
    default EnumMap<StudentColor, Integer> getHall() {
        EnumMap<StudentColor, Integer> hallCopy = new EnumMap<>(StudentColor.class);
        for (StudentColor s : StudentColor.values())
            hallCopy.put(s, getStudentsInHall(s));
        return hallCopy;
    }

    /**
     * Tries to remove the ideal amount of students from the hall of all players and puts them back in the bag.
     * If there aren't enough students, removes only the available ones.
     *
     * @param s           student color of student to remove.
     * @param idealAmount ideal amount of student to remove from halls.
     */
    void tryRemoveStudentsFromHalls(StudentColor s, int idealAmount);

    /**
     * @return the current student colors skipped.
     */
    EnumSet<StudentColor> getSkippedStudentColors();

    /**
     * @return the available island indexes.
     */
    Set<Integer> getAvailableIslandIndexes();

    /**
     * This method is used to check the professors when a pawn is removed from the schoolboard.
     *
     * @param s the StudentColor that has been removed.
     */
    void checkProfessorOnRemove(StudentColor s);
}
