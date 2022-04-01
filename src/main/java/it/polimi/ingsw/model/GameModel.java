package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.islands.IslandsManager;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayersManager;
import it.polimi.ingsw.model.player.SchoolBoard;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.nio.channels.AlreadyConnectedException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class GameModel implements Game {
    GameMode gameMode;
    GameState gameState;
    final GamePreset preset;
    RoundManager roundManager;
    final PlayersManager playersManager;
    final IslandsManager islandsManager;
    final Bag bag;
    int motherNatureIndex;
    final ArrayList<Cloud> clouds;

    //TODO JavaDOC
    GameModel(GamePreset preset) {
        gameMode = GameMode.EASY;
        this.preset = preset;
        roundManager = new RoundManager(preset);
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
        if(playersManager.getAvailablePlayerSlots() == 0){
            startGame();
        }
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

        motherNatureIndex = ThreadLocalRandom.current().nextInt(0, islandsManager.getNumIslandGroups());
        for (int i = 0; i < islandsManager.getNumIslandGroups(); i++) {
            if (i % 6 != motherNatureIndex % 6) {
                islandsManager.getIslandGroup(i).addStudent(0, bag.popRandomStudent());
            }
        }

        for(StudentColor s: StudentColor.values()) {
            bag.addStudents(Collections.nCopies(24, s));
        }
        for(Cloud c: clouds){
            for(int i = 0; i < preset.getCloudCapacity(); i++){
                try {
                    c.addStudent(bag.popRandomStudent(), i);
                }
                catch(NoSuchElementException e ){
                    e.printStackTrace();
                }
            }
        }
        initializeSchoolBoards();
        gameState = GameState.INITIALIZED;
    }

    /**
     * Adds the students to the entrance of each players' school board.
     */
    private void initializeSchoolBoards() {
        for (Player p : playersManager.getPlayers()) {
            SchoolBoard sb = playersManager.getSchoolBoard(p);
            for (int i = 0; i < preset.getEntranceCapacity(); i++) {
                sb.addToEntrance(bag.popRandomStudent());
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
        Player currentPlayer = playersManager.getCurrentPlayer();
        if(roundManager.getGamePhase().equals(GamePhase.PLANNING)) {
            if(playersManager.getPlayerHand(playersManager.getCurrentPlayer()).size() == 1)
                roundManager.setLastRound();
            for (Player p : playersManager.getPlayers()) {
                if (!currentPlayer.equals(p)) {
                    if (playersManager.getPlayedCard(p) != null) {
                        played.add(playersManager.getPlayedCard(p));
                    }
                }
            }
            if (played.contains(assistantCard)) {
                for (AssistantCard a : playersManager.getPlayerHand(currentPlayer)) {
                    if (!played.contains(a))
                        throw new Exception();
                }
            }
            try{ playersManager.currentPlayerPlayed(assistantCard);}
            catch (NoSuchElementException e){ throw new Exception ();}
            if (currentPlayer.equals(playersManager.getLastPlayer())) {
                roundManager.startActionPhase();
            }
            playersManager.nextPlayer();
            roundManager.clearMoves();
        }
    }

    //TODO from here JavaDOC
    public void moveStudentToHall(int entranceIndex) throws Exception {
        if(roundManager.canPlay()) {
            Player current = playersManager.getCurrentPlayer();
            SchoolBoard currSch = playersManager.getSchoolBoard(current);
            try {
                StudentColor moved = currSch.moveToHall(entranceIndex);
                checkProfessor(moved);
                roundManager.addMoves();
                roundManager.canPlay();
            }
            catch (LimitExceededException e){
                throw new Exception();
            }
        }
        else{ throw new Exception();}
    }


    public void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) throws Exception {
        if(roundManager.canPlay()) {
            try {
                StudentColor s = playersManager.getSchoolBoard(playersManager.getCurrentPlayer()).removeFromEntrance(entranceIndex);
                islandsManager.addStudent(s, islandGroupIndex, islandIndex);
                roundManager.addMoves();
                roundManager.canPlay();
            } catch (NoSuchElementException e) {
                throw new Exception();
            }
        }
        else{ throw new Exception();}
    }

    public void moveMotherNature(int num) throws LimitExceededException {
        if(roundManager.getGamePhase().equals(GamePhase.MOVE_MOTHER_NATURE)) {
            if (num <= playersManager.getPlayedCard(playersManager.getCurrentPlayer()).getMoves() && num > 0) {
                motherNatureIndex = (motherNatureIndex + num) % 12;
                checkInfluence(motherNatureIndex);
                checkWinner();
                if (playersManager.getCurrentPlayer().equals(playersManager.getLastPlayer())) {
                    endActionPhase();
                }
            } else {
                throw new LimitExceededException();
            }
        }else {throw new LimitExceededException();} //TODO this must be another type of exception
    }

    public void getStudentsFromCloud(int cloudIndex) throws LimitExceededException {
        if (cloudIndex >= 0 && cloudIndex < preset.getCloudsNumber()){
            for (StudentColor s : clouds.get(cloudIndex).popStudents()) {
                playersManager.getSchoolBoard(playersManager.getCurrentPlayer()).addToEntrance(s);
            }
            if (playersManager.getCurrentPlayer().equals(playersManager.getLastPlayer())) {
                nextRound();
            } else {
                playersManager.nextPlayer();
                roundManager.clearMoves();
            }
        }else {throw new LimitExceededException();}
    }

    void checkProfessor(StudentColor s){
        Player current = playersManager.getCurrentPlayer();
        SchoolBoard currSch = playersManager.getSchoolBoard(current);
        boolean found = false;
        for(Player p: playersManager.getPlayers()){
            if(!p.equals(current)){
                SchoolBoard compSch = playersManager.getSchoolBoard(p);
                if(compSch.getStudentsInHall(s) < currSch.getStudentsInHall(s)){
                    if(compSch.getProfessors().contains(s)){
                        found = true;
                        compSch.removeProfessor(s);
                        currSch.addProfessor(s);
                    }
                }
            }
        }
        if(!found && !currSch.getProfessors().contains(s)){
            try {
                currSch.addProfessor(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void checkInfluence(int islandGroupIndex) {
        Tower tower = checkInfluence(islandGroupIndex, false, 0, EnumSet.noneOf(StudentColor.class));
        if (tower != null && tower != islandsManager.getTower(islandGroupIndex)) {
            swapTowers(islandGroupIndex, tower);
            checkMergeIslandGroups(islandGroupIndex);
        }

    }

    Tower checkInfluence(int islandGroupIndex, boolean skipTowers, int additionalInfluence, EnumSet<StudentColor> skipStudentColor) {
        // group players by Tower
        EnumMap<Tower, List<Player>> playersByTower = new EnumMap<>(Tower.class);
        for (Player p: playersManager.getPlayers()) {
            Tower tower = playersManager.getSchoolBoard(p).getTower();
            if (!playersByTower.containsKey(tower)) {
                LinkedList<Player> pl = new LinkedList<>();
                pl.add(p);
                playersByTower.put(tower, pl);
            }
            else {
                playersByTower.get(tower).add(p);
            }
        }

        int maxInfluence;
        SchoolBoard sb;
        EnumSet<StudentColor> profs;

        Tower tower = islandsManager.getTower(islandGroupIndex);
        if (tower != null) {
            sb = playersManager.getSchoolBoard(playersByTower.get(tower).get(0));
            profs = sb.getProfessors();
            profs.removeAll(skipStudentColor);
            maxInfluence = skipTowers ?
                    islandsManager.calcInfluence(profs, islandGroupIndex):
                    islandsManager.calcInfluence(sb.getTower(), profs, islandGroupIndex);
        }
        else
            maxInfluence = 0;


        for (Player p: playersManager.getPlayers()) {
            sb = playersManager.getSchoolBoard(p);
            profs = sb.getProfessors();
            profs.removeAll(skipStudentColor);
            int influence = skipTowers ?
                    islandsManager.calcInfluence(profs, islandGroupIndex):
                    islandsManager.calcInfluence(sb.getTower(), profs, islandGroupIndex);
            if (p.equals(playersManager.getCurrentPlayer()))
                maxInfluence += additionalInfluence;
            if (influence > maxInfluence) {
                maxInfluence = influence;
                tower = sb.getTower();
            }
        }
        return tower;
    }

    void swapTowers(int islandGroupIndex, Tower newTower){
        Player newPlayer = null;
        SchoolBoard oldSchoolBoard = null;
        SchoolBoard newSchoolBoard = null;
        Tower oldTower = islandsManager.getTower(islandGroupIndex);
        if(oldTower == null){
            for(Player p : playersManager.getPlayers()){
                if(playersManager.getSchoolBoard(p).getTower().equals(newTower)){
                    newPlayer = p;
                    newSchoolBoard = playersManager.getSchoolBoard(p);
                    try {
                            newSchoolBoard.removeTowers(islandsManager.getIslandGroup(islandGroupIndex).size());
                            islandsManager.setTower(newTower,islandGroupIndex);
                    } catch (LimitExceededException e) {
                        roundManager.setWinner(newPlayer);
                    }
                }
            }
        }else {
            if (!newTower.equals(oldTower)) {
                for (Player p : playersManager.getPlayers()) {
                    if (newTower.equals(playersManager.getSchoolBoard(p).getTower())) {
                        newPlayer = p;
                        newSchoolBoard = playersManager.getSchoolBoard(p);
                    } else if (oldTower.equals(playersManager.getSchoolBoard(p).getTower())) {
                        oldSchoolBoard = playersManager.getSchoolBoard(p);
                    }
                }
                if (newPlayer != null && oldSchoolBoard != null && newSchoolBoard != null) {
                    islandsManager.setTower(newTower, islandGroupIndex);

                    int size = islandsManager.getIslandGroup(islandGroupIndex).size();
                    try {
                        oldSchoolBoard.addTowers(size);
                        newSchoolBoard.removeTowers(size);
                    } catch (LimitExceededException e) {
                        roundManager.setWinner(newPlayer);
                    }
                }
            }
        }
    }

    int checkMergeIslandGroups(int islandGroupIndex) {
        int numBlocks = 0;
        boolean bothBlocked;

        int previous = Math.floorMod(islandGroupIndex - 1, islandsManager.getNumIslandGroups());
        int next = Math.floorMod(islandGroupIndex + 1, islandsManager.getNumIslandGroups());

        bothBlocked = islandsManager.getIslandGroup(islandGroupIndex).isBlocked() && islandsManager.getIslandGroup(next).isBlocked();

        if (islandsManager.checkMergeNext(islandGroupIndex)) {
            if (bothBlocked)
                numBlocks++;
            if (motherNatureIndex > islandGroupIndex) {
                motherNatureIndex--;
                if (motherNatureIndex < 0)
                    motherNatureIndex = islandsManager.getNumIslandGroups() - 1;
            }
        }

        bothBlocked = islandsManager.getIslandGroup(islandGroupIndex).isBlocked() && islandsManager.getIslandGroup(previous).isBlocked();

        if (islandsManager.checkMergePrevious(islandGroupIndex)) {
            if (bothBlocked)
                numBlocks++;
            if (motherNatureIndex >= islandGroupIndex) {
                motherNatureIndex--;
                if (motherNatureIndex < 0)
                    motherNatureIndex = islandsManager.getNumIslandGroups() - 1;
            }
        }

        return numBlocks;
    }

    public void startGame(){
        if(gameState.equals(GameState.INITIALIZED)) {
            int i = ThreadLocalRandom.current().nextInt(0, preset.getPlayersNumber());
            playersManager.setFirstPlayer(i);
            roundManager.nextRound();
            gameState = GameState.STARTED;
        }
        //TODO Throws an exception
    }

    void nextRound(){
        roundManager.nextRound();
        playersManager.calculateClockwiseOrder();
        playersManager.nextPlayer();
        roundManager.clearMoves();
        playersManager.clearAllPlayedCards();
    }

    void endActionPhase(){
        roundManager.nextRound();
        playersManager.calculateClockwiseOrder();
        if(roundManager.isLastRound()){
            checkWinner();
        }
        for(Player p: playersManager.getPlayers()){
            if(p.getHand().size() == 0){
                checkWinner();
            }
        }


        for(Cloud c :clouds){
            for(int i = 0; i < preset.getCloudsNumber();i++){
                for(int j = 0; j < preset.getCloudCapacity(); j++){
                    try{
                        c.addStudent(bag.popRandomStudent(),j);
                    }
                    catch (NoSuchElementException e){
                        roundManager.setLastRound();
                    }
                }
            }
        }
    }

    void checkWinner(){
        Player winner = null;
        int minTower = preset.getTowersNumber() + 1;
        ArrayList<Player> toCheck = new ArrayList<>();
        int sup;
        int profs;
        if(islandsManager.getNumIslandGroups()<= 3){
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
                profs = 0;
                for(Player p : toCheck){
                    if(playersManager.getSchoolBoard(p).getProfessors().size() > profs){
                        winner = p;
                    }
                }
            }
            else{ winner = toCheck.get(0);}
            roundManager.setWinner(winner);
        }
        else{
            for(Player p : playersManager.getPlayers()){
                if(playersManager.getSchoolBoard(p).getNumTowers() < minTower){
                    minTower = playersManager.getSchoolBoard(p).getNumTowers();
                    winner = p;
                }
            }
            roundManager.setWinner(winner);

        }
    }

}
