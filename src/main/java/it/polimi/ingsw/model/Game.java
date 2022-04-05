package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GameState;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.List;

/**
 * Game interface. It exposes the methods to change the state of the game.
 */
public interface Game {
    /**
     * @return current game mode.
     */
    GameMode getGameMode();

    /**
     * @return current game state.
     */
    GameState getGameState();

    /**
     * @return number of available player slots.
     */
    int getAvailablePlayerSlots();

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @return if the player was added successfully.
     */
    boolean addPlayer(String nickname);

    /**
     * Initializes the game.
     * Add the remaining student on the bag.
     * @return if the initialization was successful.
     */
    boolean initializeGame();

    /**
     * Starts the game.
     * @return if the game started successfully.
     */
    boolean startGame();

    /**
     * Current player plays the specified assistant card.
     * @param assistantCard the card the player wants to play.
     * @return if the card was played successfully.
     */
    boolean playAssistantCard(AssistantCard assistantCard);

    /**
     * Moves a student from the Entrance to the Hall of the current player.
     * @param entranceIndex of the student that will be moved to the Hall.
     * @return if the student was successfully moved to the hall.
     */
    boolean moveStudentToHall(int entranceIndex);

    /**
     * Selects a student from the Entrance of the current Player's SchoolBoard, removes it from there and
     * moves it to a selected island that is part of a selected IslandGroup.
     * @param entranceIndex of the slot occupied by the student that will be moved.
     * @param islandGroupIndex of the IslandGroup that contains the selected island.
     * @return if the student was moved successfully.
     */
    boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex);

    /**
     * Moves mother nature by num.
     * @param num of moves that MotherNature should make.
     * @return if the move ended successfully.
     */
    boolean moveMotherNature(int num);

    /**
     * Moves the student currently residing on a cloud to the Entrance of the current Player.
     * @param cloudIndex of the selected cloud.
     * @return if the students were taken correctly from the cloud and added to the entrance.
     */
    boolean getStudentsFromCloud(int cloudIndex);

    /**
     * Activates character card at index.
     * @param index of the character card to activate.
     * @return if the activation was successful.
     */
    default boolean activateCharacterCard(int index) {
        return false;
    }

    /**
     * Applies the effect of the activated character card.
     * @param pairs parameters to use in the effect.
     * @return if the effect was applied successfully.
     */
    default boolean applyEffect(LinkedPairList<StudentColor, List<Integer>> pairs) {
        return false;
    }
}
