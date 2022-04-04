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
    boolean addPlayer(String nickname);
    boolean initializeGame();
    boolean startGame();
    boolean playAssistantCard(AssistantCard assistantCard);
    boolean moveStudentToHall(int entranceIndex);
    boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex);
    boolean moveMotherNature(int num);
    boolean getStudentsFromCloud(int cloudIndex);


    default boolean activateCharacterCard(int index) {
        return false;
    }

    default boolean applyEffect(EnumMap<StudentColor, List<Integer>> pairs) {
        return false;
    }
}
