package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GameState;
import it.polimi.ingsw.enums.StudentColor;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.EnumMap;
import java.util.List;
import java.util.NoSuchElementException;

public interface Game {
    GameMode getGameMode();
    GameState getGameState();
    int getAvailablePlayerSlots();
    void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException;
    void initializeGame() throws NoPermissionException;
    void startGame();
    void playAssistantCard(AssistantCard assistantCard) throws Exception;
    void moveStudentToHall(int entranceIndex)  throws Exception;
    void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) throws Exception;
    void moveMotherNature(int num);
    void getStudentsFromCloud(int cloudIndex) throws LimitExceededException;


    default void activateCharacterCard(int index) throws Exception {
        throw new Exception();
    }

    default void applyEffect(EnumMap<StudentColor, List<Integer>> pairs) throws Exception {
        throw new Exception();
    }
}
