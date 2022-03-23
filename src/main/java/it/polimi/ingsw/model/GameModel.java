package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.islands.IslandsManager;
import it.polimi.ingsw.model.player.*;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.nio.channels.AlreadyConnectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class GameModel implements Game {
    GameMode gameMode;
    GameState gameState;
    RoundManager roundManager;
    final Bag bag;
    final IslandsManager islandsManager;
    int motherNatureIndex;
    final ArrayList<Cloud> clouds;
    final PlayersManager playersManager;
    final PlayerNumber numPlayers;

    GameModel(PlayerNumber numPlayers) {
        this.gameMode = GameMode.EASY;
        this.playersManager = new PlayersManager(numPlayers.getPlayersValue());
        this.numPlayers = numPlayers;
        this.clouds = new ArrayList<>(numPlayers.getPlayersValue());
        this.bag = new Bag();

        this.islandsManager = new IslandsManager();


        // initialize clouds
        int cloudCapacity;
        if (numPlayers.getPlayersValue() == 3)
            cloudCapacity = 4;
        else
            cloudCapacity = 3;
        for (int i = 0; i < numPlayers.getPlayersValue(); i++) {
            clouds.add(new Cloud(numPlayers.getCloudCapacity()));
        }

        gameState = GameState.UNINITIALIZED;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */

    public int getAvailablePlayerSlots() {
        //FIXME
        return 0;
    }

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws AlreadyConnectedException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException  {
        playersManager.addPlayer(nickname,numPlayers.getTowersValue(), numPlayers.getEntranceValue());
    }

    /**
     * Initializes the game by adding one student on each island except where there is mother nature and on the opposite island.
     * Add the remaining student on the bag.
     * @throws NoPermissionException if game is not UNINITIALIZED or not all players have entered.
     */
    public void initializeGame() throws NoPermissionException{
        if (gameState != GameState.UNINITIALIZED || playersManager.getAvailablePlayerSlots() != 0)
            throw new NoPermissionException();

        for(StudentColor s: StudentColor.values()) {
            List<StudentColor> l = Collections.nCopies(2, s);
            bag.addStudents(l);
        }

        motherNatureIndex = ThreadLocalRandom.current().nextInt(0, islandsManager.size());
        for (int i = 0; i < islandsManager.size(); i++) {
            if (i % 6 != motherNatureIndex % 6) {
                islandsManager.getIslandGroup(i).addStudent(0, bag.popRandomStudent());
            }
        }

        for(StudentColor s: StudentColor.values()) {
            bag.addStudents(Collections.nCopies(24, s));
        }
        initializeSchoolBoards();
        gameState = GameState.INITIALIZED;
    }

    public void startGame() {
        return;
    }

    public void playAssistantCard(AssistantCard assistantCard){
        return;
    }

    public void moveStudentToHall(int entranceIndex) {
        return;
    }


    public void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) {
        return;
    }

    public void moveMotherNature(int num) {
        return;
    }

    public void getStudentsFromCloud(int cloudIndex) {
        return;
    }

    /**
     * Adds the students to the entrance of each players' school board.
     */
    //FIXME
    private void initializeSchoolBoards() {
        for (Player p : playersManager.getPlayers()) {
            SchoolBoard sb = playersManager.getSchoolBoard(p);
            for (int i = 0; i < numPlayers.getEntranceValue(); i++) {
                try {
                    sb.addToEntrance(bag.popRandomStudent());
                } catch (LimitExceededException ignored) {
                    return;
                }
            }
        }
    }

    void nextRound(){}
    void startActionPhase(){}
    void checkProfessor(StudentColor s){}
    void checkInfluence(){}
    void nextTurn(){}
    void checkWinner(){}
}
