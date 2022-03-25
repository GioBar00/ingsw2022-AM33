package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GameState;
import it.polimi.ingsw.enums.StudentColor;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public interface Game {
    GameMode getGameMode();
    GameState getGameState();
    int getAvailablePlayerSlots();
    void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException;
    void initializeGame() throws NoPermissionException;
    void playAssistantCard(AssistantCard assistantCard) throws Exception;
    void moveStudentToHall(int entranceIndex)  throws LimitExceededException , Exception;
    void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) throws NoSuchElementException,Exception;
    void moveMotherNature(int num);
    void getStudentsFromCloud(int cloudIndex) throws LimitExceededException;

    void startGame();
    default void activateCharacterCard(int index) throws Exception {
        throw new Exception();
    }

    default void applyEffect(StudentColor s, int i1, int i2) throws Exception {
        throw new Exception();
    }
}
