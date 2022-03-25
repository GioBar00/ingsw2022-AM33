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
    /**
     * Constructor of PlayerManager
     * @param numPlayers is the number of player that can be allocated
     */
    public PlayersManager(int numPlayers){
            this.numPlayers = numPlayers;
            this.currentPlayerOrderIndex = 0;
            this.playerOrderIndexes = new Integer[numPlayers];
            this.players = new ArrayList<>(numPlayers);
            for(int i= 0; i < numPlayers; i++ ){
                playerOrderIndexes[i] = i;
            }
    }

    /**
     * Change state of current player to his next
     */
    public void nextPlayer(){
        currentPlayerOrderIndex = (currentPlayerOrderIndex + 1) % numPlayers;
    }

    /**
     * Starting from a random Player calcute a clockwise order
     */
    public void setFirstPlayer(int firstPlayer){
        for(int i = 0; i < numPlayers; i ++){
            playerOrderIndexes[i] = firstPlayer;
            firstPlayer = (firstPlayer + 1) % numPlayers;
        }
    }
    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */
    public int getAvailablePlayerSlots() {return numPlayers - players.size();}

    /**
     * Method for getting the last Player of the current round
     * @return last Player
     */
    public Player getLastPlayer(){
        return players.get(playerOrderIndexes[numPlayers]);
    }

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws NameAlreadyBoundException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname, int numTowers, int entranceCapacity) throws NoPermissionException, NameAlreadyBoundException {
        if (getAvailablePlayerSlots() == 0)
            throw new NoPermissionException();

        // get random tower from available ones
        List<Tower> availableTowers = new LinkedList<>(Arrays.asList(Tower.values()));
        for (Player p: players)
            availableTowers.removeIf(x -> x.equals(p.getSchoolBoard().getTower()));

        int towerIndex = ThreadLocalRandom.current().nextInt(0, availableTowers.size());

        addPlayer(nickname, availableTowers.get(towerIndex), numTowers, entranceCapacity);
    }

    /**
     * Adds a new player to the game with a specific tower.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws NameAlreadyBoundException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname, Tower tower, int numTowers, int entranceCapacity) throws NoPermissionException, NameAlreadyBoundException {
        if (getAvailablePlayerSlots() == 0)
            throw new NoPermissionException();
        // get random wizard from available ones
        List<Wizard> availableWizards = new LinkedList<>(Arrays.asList(Wizard.values()));
        for (Player p: players) {
            if (p.getNickname().equals(nickname))
                throw new NameAlreadyBoundException();
            availableWizards.removeIf(x -> x.equals(p.getWizard()));
        }

        int wizardIndex = ThreadLocalRandom.current().nextInt(0, availableWizards.size());

        SchoolBoard sb = new SchoolBoard(entranceCapacity, tower, numTowers);
        players.add(new Player(nickname, availableWizards.get(wizardIndex), sb));
    }

    /**
     * Use to get the schoolBoard related to a player
     * @param p is the player
     * @return the schoolBoard of player p
     */
    public SchoolBoard getSchoolBoard(Player p){ return p.getSchoolBoard();}

    public SchoolBoard getSchoolBoard(){ return getCurrentPlayer().getSchoolBoard();}
    /**
     * Returns the player who is playing
     * @return current Player
     */
    public Player getCurrentPlayer() {
            return players.get(playerOrderIndexes[currentPlayerOrderIndex]);
    }

    /**
     * Calculate clockwise order starting from playerOrderIndex[0].
     * Ex: 3 2 0 1 --> 3 0 1 2
     */
    public void calculateClockwiseOrder() {
        for(int i = 1; i < playerOrderIndexes.length; i++) {
            playerOrderIndexes[i] = (playerOrderIndexes[i - 1] + 1) % playerOrderIndexes.length;
        }
    }

    /**
     * Calculates players' order based on the assistant card they played.
     * If they played the same assistant card, goes first the one who played it.
     */
    public void calculatePlayerOrder() {
        List<Integer> ordered = Arrays.asList(playerOrderIndexes);
        ordered.sort((i1, i2) -> {
            int r = players.get(i1).getAssistantCard().getValue().compareTo(players.get(i2).getAssistantCard().getValue());
            if(r == 0)
                return Integer.compare(ordered.indexOf(i1), ordered.indexOf(i1));
            return r;
        });

        ordered.toArray(playerOrderIndexes);
    }

    /**
     * Assigns to the current Player the card he wants to play
     * @param c the card a Player is trying to play
     */
    public void currentPlayerPlayed(AssistantCard c) {
         players.get(playerOrderIndexes[currentPlayerOrderIndex]).playAssistantCard(c);
    }

    /**
     * Calculates the remaining AssistantCard in the hand of one Player
     * @param p the Player
     * @return a list of remaining cards
     */
    public ArrayList<AssistantCard> getPlayerHand(Player p){
        return p.getHand();
    }

    /**
     * Calculates a list that contains all the players
     * @return  a list of all the players
     */
    public ArrayList<Player> getPlayers(){
        ArrayList<Player> ret = new ArrayList<>(numPlayers);
        for(Integer i : playerOrderIndexes){
            ret.add(players.get(i));
        }
        return ret;
    }

    /**
     * Method for getting the last card played by one Player
     * @param p the Player
     * @return the last played card
     */
    public AssistantCard getPlayedCard(Player p){
        return p.getAssistantCard();
    }

    /**
     * deletes all the old played cards
     */
    public void clearAllPlayedCards(){
        for(Player p: players){
            p.clearPlayedCard();
        }
    }
}
