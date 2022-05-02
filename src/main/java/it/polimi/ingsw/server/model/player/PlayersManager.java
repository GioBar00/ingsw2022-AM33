package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.actions.requests.PlayAssistantCard;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PlayersManager {
    /**
     * preset of the current game
     */
    private final GamePreset preset;

    /**
     * index of the current player in the array playerOrderIndexes
     */
    private Integer currentPlayerOrderIndex;
    /**
     * array to keep track of the order of the player
     */
    private final Integer[] playerOrderIndexes;
    /**
     * players that are part of the game
     */
    private final ArrayList<Player> players;

    /**
     * map of the teams corresponding to each tower
     */
    private final EnumMap<Tower, List<Player>> teams = new EnumMap<>(Tower.class);

    /**
     * Constructor of PlayerManager
     * @param preset of the current game
     */
    public PlayersManager(GamePreset preset){
            this.preset = preset;
            this.currentPlayerOrderIndex = 0;
            this.playerOrderIndexes = new Integer[preset.getPlayersNumber()];
            this.players = new ArrayList<>(preset.getPlayersNumber());
            for (int i= 0; i < preset.getPlayersNumber(); i++ ) {
                playerOrderIndexes[i] = i;
            }
            for (Tower t: preset.getTowers()) {
                teams.put(t, new LinkedList<>());
            }
    }

    /**
     * @return current preset
     */
    public GamePreset getPreset() {
        return preset;
    }

    /**
     * @return teams.
     */
    public EnumMap<Tower, List<Player>> getTeams() {
        return new EnumMap<>(teams);
    }

    /**
     * Change state of current player to his next
     */
    public void nextPlayer(){
        currentPlayerOrderIndex = (currentPlayerOrderIndex + 1) % preset.getPlayersNumber();
    }

    /**
     * Starting from a random Player, calculate a clockwise order
     */
    public void setFirstPlayer(int firstPlayer){
        for(int i = 0; i < preset.getPlayersNumber(); i ++){
            playerOrderIndexes[i] = firstPlayer;
            firstPlayer = (firstPlayer + 1) % preset.getPlayersNumber();
        }
    }
    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */
    public int getAvailablePlayerSlots() {
        return preset.getPlayersNumber() - players.size();
    }

    /**
     * Method for getting the last Player of the current round
     * @return last Player
     */
    public Player getLastPlayer(){
        return players.get(playerOrderIndexes[preset.getPlayersNumber() - 1]);
    }

    /**
     * Adds a new player to the game if there are no other players with the same nickname.
     * @param playerDetails unique class with details for a player
     * @return if the player was added successfully.
     */
    public boolean addPlayer(PlayerDetails playerDetails) {
        // get random tower from available ones
        if(playerDetails.getTower() == null){
            List<Tower> availableTowers = new LinkedList<>(preset.getTowers().stream().toList());

            for (Player p: players)
                availableTowers.removeIf(x -> x.equals(p.getSchoolBoard().getTower()));

            if (availableTowers.size() > 0) {
                int towerIndex = ThreadLocalRandom.current().nextInt(0, availableTowers.size());
                return addPlayer(playerDetails.getNickname(), availableTowers.get(towerIndex), playerDetails.getWizard());
            }
            return false;
        }
        return addPlayer(playerDetails.getNickname(), playerDetails.getTower(), playerDetails.getWizard());
    }

    /**
     * Adds a new player to the game with a specific tower if there are no other players with the same nickname.
     * @param nickname unique identifier of a player
     * @return if the player was added successfully.
     */
    public boolean addPlayer(String nickname, Tower tower, Wizard wizard) {
        if (getAvailablePlayerSlots() > 0) {

            int numTowers = teams.get(tower).size() > 0 ?
                    0 :
                    preset.getTowersNumber();

            SchoolBoard sb = new SchoolBoard(preset.getEntranceCapacity(), tower, numTowers);
            Player p = new Player(nickname, wizard, sb);
            players.add(p);
            teams.get(tower).add(p);
            return true;
        }
        return false;
    }

    /**
     * Use to get the schoolBoard related to a player
     * @param p is the player
     * @return the schoolBoard of player p
     */
    public SchoolBoard getSchoolBoard(Player p) {
        return p.getSchoolBoard();
    }

    /**
     * Use to get the schoolBoard of current player
     * @return the schoolBoard of current player
     */
    public SchoolBoard getSchoolBoard() {
        return getCurrentPlayer().getSchoolBoard();
    }

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
     * @return if the card was played successfully.
     */
    public boolean currentPlayerPlayed(AssistantCard c) {
         return getCurrentPlayer().playAssistantCard(c);
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
        ArrayList<Player> ret = new ArrayList<>(preset.getPlayersNumber());
        for (int i = 0; i < players.size(); i++) {
            ret.add(players.get(playerOrderIndexes[i]));
        }
        return ret;
    }

    /**
     * Method for getting the last card played by one Player.
     * @param p the Player
     * @return the last played card
     */
    public AssistantCard getPlayedCard(Player p){
        return p.getAssistantCard();
    }

    /**
     * Method for getting the last card played the current player.
     * @return the last played card
     */
    public AssistantCard getPlayedCard(){
        return getCurrentPlayer().getAssistantCard();
    }

    /**
     * deletes all the old played cards
     */
    public void clearAllPlayedCards(){
        for(Player p: players){
            p.clearPlayedCard();
        }
    }



    /**
     * @param destPlayer the player to whom the view is going to be sent
     * @return the array of current playerViews
     */
    public ArrayList<PlayerView> getPlayersView(Player destPlayer){
        ArrayList<PlayerView> playersView = new ArrayList<>();
        for(Player p : players){
            PlayerView addition = p.getPlayerView(p.equals(destPlayer));
            playersView.add(addition);
        }
        return playersView;
    }


    /**
     * Calculates the playable cards for the current player.
     *
     * @return play assistant card message.
     */
    public PlayAssistantCard getPossibleAssistantCards() {
        EnumSet<AssistantCard> possibleCards = EnumSet.copyOf(getCurrentPlayer().getHand());
        for (int i = currentPlayerOrderIndex - 1; i >= 0; i--) {
            Player p = players.get(playerOrderIndexes[i]);
            possibleCards.remove(p.getAssistantCard());
        }
        return new PlayAssistantCard(possibleCards);
    }

    /**
     * Remove a player from the party
     * @param nickname of the player
     * @return if the player has been removed
     */
    public boolean removePlayer(String nickname){
        for(Player p : players){
            if(p.getNickname().equals(nickname)){
                players.remove(p);
                teams.get(p.getSchoolBoard().getTower()).remove(p);

                return true;
            }
        }
        return false;
    }

}
