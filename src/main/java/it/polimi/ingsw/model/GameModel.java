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
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

class GameModel implements Game {
    GameMode gameMode;
    GameState gameState;
    RoundManager roundManager;
    final PlayersManager playersManager;
    final IslandsManager islandsManager;
    final Bag bag;
    int motherNatureIndex;
    final ArrayList<Cloud> clouds;
    final GamePreset preset;

    GameModel(GamePreset preset) {
        gameMode = GameMode.EASY;
        this.preset = preset;
        roundManager = new RoundManager(preset.getPlayersNumber());
        playersManager = new PlayersManager(preset.getPlayersNumber());
        clouds = new ArrayList<>(preset.getCloudsNumber());
        bag = new Bag();

        islandsManager = new IslandsManager();


        // initialize clouds
        for (int i = 0; i < preset.getCloudsNumber(); i++) {
            clouds.add(new Cloud(preset.getCloudCapacity()));
        }

        gameState = GameState.UNINITIALIZED;
    }

    /**
     * @return the game mode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * @return the current state of the game
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Calculates the number of available slots for players to enter.
     * @return number of available slots
     */
    public int getAvailablePlayerSlots() {
        return playersManager.getAvailablePlayerSlots();
    }

    /**
     * Adds a new player to the game.
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws AlreadyConnectedException if another player with same nickname is already in the game
     */
    public void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException  {
        playersManager.addPlayer(nickname,preset.getTowersNumber(), preset.getEntranceCapacity());
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

    /**
     * Adds the students to the entrance of each players' school board.
     */
    //FIXME
    private void initializeSchoolBoards() {
        for (Player p : playersManager.getPlayers()) {
            SchoolBoard sb = playersManager.getSchoolBoard(p);
            for (int i = 0; i < preset.getEntranceCapacity(); i++) {
                try {
                    sb.addToEntrance(bag.popRandomStudent());
                } catch (LimitExceededException ignored) {
                    return;
                }
            }
        }
    }



    /**
     * Try to play an assistant card. If someone has already played it check that the current player hasn't other cards playable.
     * @param assistantCard the card the player wants to play
     * @throws Exception Someone has played the same card and the player can play another card
     */
    public void playAssistantCard(AssistantCard assistantCard) throws Exception{
        ArrayList<AssistantCard> played =  new ArrayList<>();
        Player current = playersManager.getCurrentPlayer();
        for(Player p :playersManager.getPlayers() ){
            if(!current.equals(p)){
                if(playersManager.getPlayedCard(p).equals(assistantCard)) {
                    played.add(playersManager.getPlayedCard(p));
                }
            }
        }
        if(!played.isEmpty()){
            for(AssistantCard a : AssistantCard.values()){
                if(!played.contains(a))
                    if(playersManager.getPlayerHand(playersManager.getCurrentPlayer()).contains(a)){
                        throw new Exception();
                    }
                }
            }
        playersManager.currentPlayerPlayed(assistantCard);
        if(playersManager.getCurrentPlayer().equals(playersManager.getLastPlayer())){
            roundManager.startActionPhase();
        }
        playersManager.nextPlayer();
    }

    //TODO from here JavaDOC
    public void moveStudentToHall(int entranceIndex) throws Exception {
            roundManager.addMoves();
            Player current = playersManager.getCurrentPlayer();
            SchoolBoard currSch = playersManager.getSchoolBoard(current);
            StudentColor moved = currSch.moveToHall(entranceIndex);
            checkProfessor(moved);



    }


    public void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) throws Exception {
        roundManager.addMoves();
        StudentColor s = playersManager.getSchoolBoard(playersManager.getCurrentPlayer()).removeFromEntrance(entranceIndex);
        islandsManager.addStudent(islandGroupIndex,s,islandIndex);
    }

    public void moveMotherNature(int num) {
        motherNatureIndex = (motherNatureIndex + num) % 12;
        this.checkInfluence();
        checkWinner();
    }

    public void getStudentsFromCloud(int cloudIndex) throws LimitExceededException{
        for(StudentColor s : clouds.get(cloudIndex).getStudents()){
            playersManager.getSchoolBoard(playersManager.getCurrentPlayer()).addToEntrance(s);
        }
        if(playersManager.getCurrentPlayer().equals(playersManager.getLastPlayer())){
            nextRound();
        }
        else{playersManager.nextPlayer();}
    }

    void checkProfessor(StudentColor s){
        Player current = playersManager.getCurrentPlayer();
        SchoolBoard currSch = playersManager.getSchoolBoard(current);
        for(Player p: playersManager.getPlayers()){
            if(!p.equals(current)){
                SchoolBoard compSch = playersManager.getSchoolBoard(p);
                if(compSch.getStudentsInHall(s) < currSch.getStudentsInHall(s)){
                    if(compSch.getProfessors().contains(s)){
                        try {
                            compSch.removeProfessor(s);
                            currSch.addProfessor(s);
                            break;
                        }
                        catch (Exception ignored){}
                    }
                }
            }
        }
    }

    void checkInfluence(){
        SchoolBoard max = null;
        Player pMax = null;
        int maxInfluence = 0;
        int currInfluence;
        for(Player p : playersManager.getPlayers()){
            SchoolBoard curr = playersManager.getSchoolBoard(p);
            currInfluence = islandsManager.calcInfluence(curr.getTower(),curr.getProfessors(),motherNatureIndex);
            if( currInfluence > maxInfluence){
                max = curr;
                maxInfluence =currInfluence;
                pMax = p;
            }
        }
        if(maxInfluence != 0)
            swapTowers(max,pMax);

    }

    private void swapTowers(SchoolBoard max, Player pMax){

        if(!max.getTower().equals(islandsManager.getTower(motherNatureIndex))){
            Tower old = islandsManager.getTower(motherNatureIndex);
            try{
                int size = islandsManager.getIslandGroup(motherNatureIndex).size();
                max.removeTowers(size);
                islandsManager.setTower(max.getTower(),motherNatureIndex);
                for(Player p: playersManager.getPlayers()){
                    SchoolBoard s = playersManager.getSchoolBoard(p);
                    if(s.getTower().equals(old)){
                        s.addTowers(size);
                    }
                }
            }
            catch(LimitExceededException e){
                roundManager.setWinner(pMax);
            }
        }
    }

    //TODO
    public void startGame() {

    }
    void nextRound(){
        roundManager.nextRound();
        playersManager.calculatePlayerOrder();
        playersManager.nextPlayer();
    }

    void endActionPhase(){
        playersManager.calculatePlayerOrder();
        playersManager.nextPlayer();
        if(roundManager.isLastRound()){
            checkWinner();
        }
        for(Player p: playersManager.getPlayers()){
            if(p.getHand().size() == 0){
                checkWinner();
            }
        }
    }

    void checkWinner(){
        Player winner = null;
        int minTower = preset.getTowersNumber() + 1;
        ArrayList<Player> toCheck = new ArrayList<>();
        int sup;
        int profs;
        if(islandsManager.getActualSize()<= 3){
            for(Player p: playersManager.getPlayers()){
                sup = playersManager.getSchoolBoard(p).getNumTowers();
                if(sup < minTower){
                    minTower = sup;
                }
            }
            for(Player p : playersManager.getPlayers()){
                if(playersManager.getSchoolBoard(p).getNumTowers() == minTower){
                    toCheck.add(p);
                }
            }
            if(toCheck.size() > 1){
                sup = 0;
                for(Player p : toCheck){
                    if(playersManager.getSchoolBoard(p).getProfessors().size() > sup){
                        winner = p;
                    }
                }
            }
            else{ winner = toCheck.get(0);}
            roundManager.setWinner(winner);
        }
    }

}

//TODO ATTENZIONE MANCANO TUTTI I CONTROLLI SULLO STATO DELLA PARTITA
//TODO MANCA DA IMPLEMENTARE METODO START