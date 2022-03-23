package it.polimi.ingsw.model;

import it.polimi.ingsw.model.islands.IslandsManager;

import javax.naming.LimitExceededException;
import javax.naming.NoPermissionException;
import java.nio.channels.AlreadyConnectedException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameModel {
    GameMode gameMode;
    GameState gameState;
    RoundManager roundManager;
    final Bag bag;
    final IslandsManager islandsManager;
    int motherNatureIndex;
    final ArrayList<Cloud> clouds;
    final int numPlayers;
    final ArrayList<Player> players;

    GameModel(int numPlayers) {
        gameMode = GameMode.EASY;
        this.numPlayers = numPlayers;
        if (numPlayers == 0) {
            bag = null;
            players = null;
            clouds = null;
            islandsManager = null;
            return;
        }
        islandsManager = new IslandsManager();
        players = new ArrayList<>(numPlayers);
        clouds = new ArrayList<>(numPlayers);
        bag = new Bag();

        // initialize clouds
        int cloudCapacity;
        if (numPlayers == 3)
            cloudCapacity = 4;
        else
            cloudCapacity = 3;
        for (int i = 0; i < numPlayers; i++) {
            clouds.add(new Cloud(cloudCapacity));
        }

        gameState = GameState.UNINITIALIZED;
    }

    public GameMode getGameMode() {return gameMode;}
    public GameState getGameState() {return gameState;}

    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */
    public int getAvailablePlayerSlots() {return numPlayers - players.size();}

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws AlreadyConnectedException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname) throws NoPermissionException, AlreadyConnectedException {
        if (gameState != GameState.UNINITIALIZED || getAvailablePlayerSlots() == 0)
            throw new NoPermissionException();
        // get random tower and wizard from available ones
        List<Tower> availableTowers = new LinkedList<>(Arrays.asList(Tower.values()));
        List<Wizard> availableWizards = new LinkedList<>(Arrays.asList(Wizard.values()));
        for (Player p: players) {
            if (p.getNickname().equals(nickname))
                throw new AlreadyConnectedException();
            availableTowers.removeIf(x -> x.equals(p.getSchoolBoard().getTower()));
            availableWizards.removeIf(x -> x.equals(p.getWizard()));
        }
        int numTowers;
        int entranceCapacity;
        if(numPlayers == 3) {
            numTowers = 6;
            entranceCapacity = 9;
        }
        else {
            numTowers = 8;
            entranceCapacity = 7;
        }
        int towerIndex = ThreadLocalRandom.current().nextInt(0, availableTowers.size());
        int wizardIndex = ThreadLocalRandom.current().nextInt(0, availableWizards.size());

        SchoolBoard sb = new SchoolBoard(entranceCapacity, availableTowers.get(towerIndex), numTowers);
        players.add(new Player(nickname, availableWizards.get(wizardIndex), sb));
    }

    /**
     * Initializes the game by adding one student on each island except where there is mother nature and on the opposite island.
     * Add the remaining student on the bag.
     * @throws NoPermissionException if game is not UNINITIALIZED or not all players have entered.
     */
    public void initializeGame() throws NoPermissionException {
        if (gameState != GameState.UNINITIALIZED || getAvailablePlayerSlots() != 0)
            throw new NoPermissionException();

        for(StudentColor s: StudentColor.values()) {
            bag.addStudents(Collections.nCopies(2, s));
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

    /**
     * Adds the students to the entrance of each players' school board.
     */
    private void initializeSchoolBoards() {
        for (Player player : players) {
            SchoolBoard sb = player.getSchoolBoard();
            for (int j = 0; j < sb.getEntranceCapacity(); j++) {
                try {
                    sb.addToEntrance(bag.popRandomStudent());
                } catch (LimitExceededException ignored) {
                    return;
                }
            }
        }
    }

}
