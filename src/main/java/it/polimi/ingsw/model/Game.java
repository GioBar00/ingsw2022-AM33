package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GameState;
import it.polimi.ingsw.enums.StudentColor;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;

public interface Game {
    GameMode getGameMode();
    GameState getGameState();
    int getAvailablePlayerSlots();
    void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException;
    void initializeGame() throws NoPermissionException;
    void startGame();
    void playAssistantCard(AssistantCard assistantCard);
    void moveStudentToHall(int entranceIndex);
    void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex);
    void moveMotherNature(int num);
    void getStudentsFromCloud(int cloudIndex);

    default void activateCharacterCard(int index) throws Exception {
        throw new Exception();
    }

    default void applyEffect(StudentColor s, int i1, int i2) throws Exception {
        throw new Exception();
    }
}
