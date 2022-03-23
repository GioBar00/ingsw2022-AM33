package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.Tower;
import it.polimi.ingsw.enums.Wizard;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayersManager{

    private Integer currentPlayerOrderIndex;
    private final Integer[] playerOrderIndexes;
    private final ArrayList<Player> players;
    private final Integer numPlayers;

    public PlayersManager(int numPlayers){
            this.numPlayers = numPlayers;
            this.currentPlayerOrderIndex = 0;
            this.playerOrderIndexes = new Integer[numPlayers];
            this.players = new ArrayList<>(numPlayers);
    }
    public void nextPlayer(){
        currentPlayerOrderIndex = (currentPlayerOrderIndex + 1) % numPlayers;
    }
    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */
    public int getAvailablePlayerSlots() {return numPlayers - players.size();}

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws NameAlreadyBoundException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname, int numTowers, int entranceCapacity) throws NoPermissionException, NameAlreadyBoundException {
        if (getAvailablePlayerSlots() == 0)
            throw new NoPermissionException();
        // get random tower and wizard from available ones
        List<Tower> availableTowers = new LinkedList<>(Arrays.asList(Tower.values()));
        List<Wizard> availableWizards = new LinkedList<>(Arrays.asList(Wizard.values()));
        for (Player p: players) {
            if (p.getNickname().equals(nickname))
                throw new NameAlreadyBoundException();
            availableTowers.removeIf(x -> x.equals(p.getSchoolBoard().getTower()));
            availableWizards.removeIf(x -> x.equals(p.getWizard()));
        }

        int towerIndex = ThreadLocalRandom.current().nextInt(0, availableTowers.size());
        int wizardIndex = ThreadLocalRandom.current().nextInt(0, availableWizards.size());

        SchoolBoard sb = new SchoolBoard(entranceCapacity, availableTowers.get(towerIndex), numTowers);
        players.add(new Player(nickname, availableWizards.get(wizardIndex), sb));
    }

    public SchoolBoard getSchoolBoard(Player p){ return p.getSchoolBoard();}

    public Player getCurrentPlayer() {
            return players.get(currentPlayerOrderIndex);
    }

    /**
     * Calculate clockwise order starting from playerOrderIndex[0].
     * Ex: 3 2 0 1 --> 3 0 1 2
     */
    private void calculateClockwiseOrder() {
        for(int i = 1; i < playerOrderIndexes.length; i++) {
            playerOrderIndexes[i] = (playerOrderIndexes[i - 1] + 1) % playerOrderIndexes.length;
        }
    }

    /**
     * Calculates players' order based on the assistant card they played.
     * If they played the same assistant card, goes first the one who played it.
     * @param players array of players in the game.
     */
    public void calculatePlayerOrder(Player[] players) {
        List<Integer> ordered = Arrays.asList(playerOrderIndexes);
        ordered.sort((i1, i2) -> {
            int r = players[i1].getAssistantCard().getValue().compareTo(players[i2].getAssistantCard().getValue());
            if(r == 0)
                return Integer.compare(ordered.indexOf(i1), ordered.indexOf(i1));
            return r;
        });
        ordered.toArray(playerOrderIndexes);
    }

    public void currentPlayerPlayed(AssistantCard c) {
        players.get(currentPlayerOrderIndex).playAssistantCard(c);
    }

    public ArrayList<Player> getPlayers(){ return new ArrayList<>(players);}
}
