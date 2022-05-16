package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.network.listeners.MessageListenerSubscriber;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.*;

/**
 * Game interface. It exposes the methods to change the state of the game.
 */
public interface Game extends MessageListenerSubscriber {
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
     *
     * @param playerDetails unique class with details for a player
     * @return if the player was added successfully.
     */
    boolean addPlayer(PlayerDetails playerDetails);


    /**
     * Starts the game.
     *
     * @return if the game started successfully.
     */
    boolean startGame();

    /**
     * Current player plays the specified assistant card.
     *
     * @param assistantCard the card the player wants to play.
     * @return if the card was played successfully.
     */
    boolean playAssistantCard(AssistantCard assistantCard);

    /**
     * Moves a student from the Entrance to the Hall of the current player.
     *
     * @param entranceIndex of the student that will be moved to the Hall.
     * @return if the student was successfully moved to the hall.
     */
    boolean moveStudentToHall(int entranceIndex);

    /**
     * Selects a student from the Entrance of the current Player's SchoolBoard, removes it from there and
     * moves it to a selected island that is part of a selected IslandGroup.
     *
     * @param entranceIndex    of the slot occupied by the student that will be moved.
     * @param islandGroupIndex of the IslandGroup that contains the selected island.
     * @return if the student was moved successfully.
     */
    boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex);

    /**
     * Moves mother nature by num.
     *
     * @param num of moves that MotherNature should make.
     * @return if the move ended successfully.
     */
    boolean moveMotherNature(int num);

    /**
     * Moves the student currently residing on a cloud to the Entrance of the current Player.
     *
     * @param cloudIndex of the selected cloud.
     * @return if the students were taken correctly from the cloud and added to the entrance.
     */
    boolean getStudentsFromCloud(int cloudIndex);

    /**
     * Used to get the current player
     *
     * @return the nickname of the current player
     */
    String getCurrentPlayer();

    /**
     * Return the phase of the Game
     *
     * @return GamePhase
     */
    GamePhase getPhase();

    /**
     * Activates character card at index.
     *
     * @param index of the character card to activate.
     * @return if the activation was successful.
     */
    default boolean activateCharacterCard(int index) {
        return false;
    }

    /**
     * Applies the effect of the activated character card.
     *
     * @param parameters to use in the effect.
     * @return if the effect was applied successfully.
     */
    default boolean applyEffect(CharacterParameters parameters) {
        return false;
    }

    /**
     * Ends the effect of the activated character card.
     *
     * @return if the effect was ended correctly.
     */
    default boolean endEffect() {
        return false;
    }

    /**
     * Changes team of the player.
     *
     * @param nickname of the player.
     * @param tower    team to change to.
     * @return if the team was changed successfully.
     */
    default boolean changeTeam(String nickname, Tower tower) {
        return false;
    }

    /**
     * Skips the current player's turn.
     */
    void skipCurrentPlayerTurn();

    /**
     * Remove a player from the game
     *
     * @param nickname of the player
     * @return if the player was removed
     */
    boolean removePlayer(String nickname);

    /**
     * Notifies the current game state to a player
     *
     * @param nickname of the player
     */
    void notifyCurrentGameStateToPlayer(String nickname);
}
