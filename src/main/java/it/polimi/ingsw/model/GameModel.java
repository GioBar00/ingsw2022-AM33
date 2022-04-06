package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.islands.IslandsManager;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayersManager;
import it.polimi.ingsw.model.player.SchoolBoard;

import java.util.*;
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

    //TODO JavaDOC
    GameModel(GamePreset preset) {
        gameMode = GameMode.EASY;
        roundManager = new RoundManager(preset);
        playersManager = new PlayersManager(preset);
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
     * Adds a new player to the game only if the game is uninitialized, there is at lest an empty player slot and there isn't any other player with that nickname.
     * @param nickname unique identifier of a player
     * @return if the player was added successfully.
     */
    public boolean addPlayer(String nickname) {
        if (gameState != GameState.UNINITIALIZED)
            return false;
        return playersManager.addPlayer(nickname);
    }

    /**
     * Initializes the game by adding one student on each island except where there is mother nature and on the opposite island.
     * Add the remaining student on the bag.
     * @return if game is UNINITIALIZED or all players have entered.
     */
    public boolean initializeGame() {
        if (gameState != GameState.UNINITIALIZED || playersManager.getAvailablePlayerSlots() != 0)
            return false;

        for(StudentColor s: StudentColor.values()) {
            List<StudentColor> l = Collections.nCopies(2, s);
            bag.addStudents(l);
        }

        motherNatureIndex = ThreadLocalRandom.current().nextInt(0, islandsManager.getNumIslandGroups());
        for (int i = 0; i < islandsManager.getNumIslandGroups(); i++) {
            if (i % 6 != motherNatureIndex % 6) {
                islandsManager.getIslandGroup(i).addStudent(bag.popRandomStudent());
            }
        }

        for(StudentColor s: StudentColor.values()) {
            bag.addStudents(Collections.nCopies(24, s));
        }
        for(Cloud c: clouds){
            for(int i = 0; i < playersManager.getPreset().getCloudCapacity(); i++){
                c.addStudent(bag.popRandomStudent(), i);
            }
        }
        initializeSchoolBoards();
        gameState = GameState.INITIALIZED;
        return true;
    }

    /**
     * Adds the students to the entrance of each players' school board.
     */
    private void initializeSchoolBoards() {
        for (Player p : playersManager.getPlayers()) {
            SchoolBoard sb = playersManager.getSchoolBoard(p);
            for (int i = 0; i < playersManager.getPreset().getEntranceCapacity(); i++) {
                sb.addToEntrance(bag.popRandomStudent());
            }
        }
    }

    /**
     * Starts the Game and randomly selects the first Player if the game is initialized.
     * @return if the game started successfully.
     */
    public boolean startGame(){
        if (gameState != GameState.INITIALIZED)
            return false;

        int i = ThreadLocalRandom.current().nextInt(0, playersManager.getPreset().getPlayersNumber());
        playersManager.setFirstPlayer(i);
        roundManager.nextRound();
        gameState = GameState.STARTED;
        return true;
    }

    /**
     * Try to play an assistant card. If someone has already played it check that the current player hasn't other cards playable.
     * Checks if the player had that card in hand and if it is the correct phase to play a card.
     * @param assistantCard the card the player wants to play
     * @return if the card was played successfully.
     */
    public boolean playAssistantCard(AssistantCard assistantCard) {
        if (gameState != GameState.STARTED || roundManager.getGamePhase() != GamePhase.PLANNING)
            return false;

        ArrayList<AssistantCard> played = new ArrayList<>();
        Player currentPlayer = playersManager.getCurrentPlayer();
        ArrayList<AssistantCard> playerHand = playersManager.getPlayerHand(currentPlayer);

        for (Player p : playersManager.getPlayers()) {
            if (!currentPlayer.equals(p)) {
                AssistantCard card = playersManager.getPlayedCard(p);
                if (card != null) {
                    played.add(card);
                }
            }
        }

        if (played.contains(assistantCard)) {
            for (AssistantCard a : playerHand) {
                if (!played.contains(a))
                    return false;
            }
        }

        if (!playersManager.currentPlayerPlayed(assistantCard))
            return false;

        if (currentPlayer.equals(playersManager.getLastPlayer())) {
            roundManager.startActionPhase();
            playersManager.calculatePlayerOrder();
        }

        if(playersManager.getPlayerHand(currentPlayer).size() == 0)
            roundManager.setLastRound();


        playersManager.nextPlayer();
        return true;
    }

    /**
     * Moves a student from the Entrance to the Hall of the current player if the player can play, the hall is not full and the index is valid.
     * then proceeds to check if the professors need to be re-distributed between the players
     * @param entranceIndex of the student that will be moved to the Hall
     * @return if the student was successfully moved to the hall.
     */
    public boolean moveStudentToHall(int entranceIndex) {
        if (gameState != GameState.STARTED || !roundManager.canMoveStudents())
            return false;

        SchoolBoard currSch = playersManager.getSchoolBoard();
        StudentColor moved = currSch.getStudentInEntrance(entranceIndex);
        if (moved != null) {
            if (currSch.moveToHall(entranceIndex)) {
                currSch.removeFromEntrance(entranceIndex);
                checkProfessor(moved);
                roundManager.addMoves();
                return true;
            }
        }

        return false;
    }

    /**
     * Selects a student from the Entrance of the current Player's SchoolBoard, removes it from there and
     * moves it to a selected island that is part of a selected IslandGroup. Checks if the player can play,
     * if it is the correct game state and if the indexes are valid.
     * @param entranceIndex of the slot occupied by the student that will be moved
     * @param islandGroupIndex of the IslandGroup that contains the selected island
     * @return if the student was moved successfully.
     */
    public boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex) {
        if (gameState != GameState.STARTED || !roundManager.canMoveStudents())
            return false;

        SchoolBoard currSch = playersManager.getSchoolBoard();
        StudentColor moved = currSch.getStudentInEntrance(entranceIndex);
        if (moved != null) {
            if (islandsManager.addStudent(moved, islandGroupIndex)) {
                currSch.removeFromEntrance(entranceIndex);
                roundManager.addMoves();
                return true;
            }
        }

        return false;
    }

    /**
     * Moves mother nature with a maximum movement of the number in the assistant card played.
     * @param num of moves that MotherNature should make.
     * @return if the move ended successfully.
     */
    public boolean moveMotherNature(int num) {
        return moveMotherNature(num, playersManager.getPlayedCard().getMoves());
    }

    /**
     * Moves MotherNature of a selected number of moves, following the order of IslandGroups if the game phase is correct and the num is valid.
     * If there are no clouds with students it skips the "choose cloud" phase.
     * @param num of moves that MotherNature makes.
     * @return if the move ended successfully.
     */
    boolean moveMotherNature(int num, int maxNum) {
        if (gameState != GameState.STARTED)
            return false;
        if (roundManager.getGamePhase() != GamePhase.MOVE_MOTHER_NATURE)
            return false;

        if (num > maxNum || num <= 0)
            return false;

        motherNatureIndex = (motherNatureIndex + num) % islandsManager.getNumIslandGroups();
        checkInfluence(motherNatureIndex);

        if (roundManager.getWinners().isEmpty()) {
            boolean cloudWithStud = atLeastOneCloudWithStudents();
            if (cloudWithStud) {
                roundManager.startChooseCloudPhase();
            } else {
                nextTurn();
            }
        }
        return true;
    }

    /**
     * Checks if at least one cloud has at least one student.
     * @return if at least one cloud has at least one student.
     */
    boolean atLeastOneCloudWithStudents() {
        boolean b = false;
        for (Cloud c: clouds)
            if (c.getStudents().size() > 0) {
                b = true;
                break;
            }
        return b;
    }

    /**
     * Moves the student currently residing on a cloud to the Entrance of the current Player if the game state and phase are correct,
     * if the index is valid and if the cloud is not empty.
     * @param cloudIndex of the selected cloud
     * @return if the students were taken correctly from the cloud and added to the entrance.
     */
    public boolean getStudentsFromCloud(int cloudIndex) {
        if (gameState != GameState.STARTED)
            return false;
        if (roundManager.getGamePhase() != GamePhase.CHOOSE_CLOUD)
            return false;
        if (cloudIndex < 0 || cloudIndex >= playersManager.getPreset().getCloudsNumber())
            return false;

        List<StudentColor> studs = clouds.get(cloudIndex).popStudents();

        if (studs.size() == 0)
            return false;

        for (StudentColor s : studs) {
            playersManager.getSchoolBoard().addToEntrance(s);
        }

        nextTurn();

        return true;
    }

    /**
     * Starts the action phase of the next player. If it is the last players it ends the round.
     */
    void nextTurn() {
        if (playersManager.getCurrentPlayer().equals(playersManager.getLastPlayer())) {
            nextRound();
        } else {
            playersManager.nextPlayer();
            roundManager.startActionPhase();
        }
    }

    /**
     * the method checks that che professor of type s is assigned to the correct SchoolBoard and changes its position
     * in case that the current assignment is incorrect
     * @param s: the color of the professor to be checked
     */
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

    /**
     * The method calculated the Influence that each Player has on a specific IslandGroup, selects the most influential
     * Player and, in the casa that the Tower currently present on the IslandGroup is not the same one of the most
     * influential Player, swaps the tower with the correct one, then proceeds to check whether the IslandGroup
     * can be merged with the ones next to it.
     * In order to calculate the influence of the various Players, the method calls an overloaded version
     * of checkInfluence.
     * @param islandGroupIndex of the IslandGroup in question
     */
    void checkInfluence(int islandGroupIndex) {
        Tower tower = checkInfluence(islandGroupIndex, false, 0, EnumSet.noneOf(StudentColor.class));
        if (tower != null && tower != islandsManager.getTower(islandGroupIndex)) {
            swapTowers(islandGroupIndex, tower);
            checkMergeIslandGroups(islandGroupIndex);
        }

    }

    /**
     * Overloaded version of checkInfluence that uses particular parameters to handel the effects of the CharacterCards.
     * Groups players by tower color then finds the tower color that has the highest influence.
     *
     * @param islandGroupIndex of the IslandGroup on which the influence is calculated
     * @param skipTowers true in case that the CharacterCard "Centaur" is activated (during the turn the
     *                   towers of the IslandGroup will not be counted for the computation on the influence)
     * @param additionalInfluence adds two extra points to the Player that has activated the CharacterCard
     *                            of the Knight
     * @param skipStudentColor during the computation of the influence, the students of the colors decided by the Player
     *                         that has activated the CharacterCard "Harvester" will not be counted
     * @return the tower that belongs to the player/players with the most influence
     */
    Tower checkInfluence(int islandGroupIndex, boolean skipTowers, int additionalInfluence, EnumSet<StudentColor> skipStudentColor) {
        EnumMap<Tower, List<Player>> playersByTower = groupPlayersByTower();

        int maxInfluence;
        EnumSet<StudentColor> profs;

        // calculate influence of the current owner
        Tower tower = islandsManager.getTower(islandGroupIndex);
        if (tower != null) {
            profs = EnumSet.noneOf(StudentColor.class);
            for (Player p: playersByTower.get(tower)) {
                profs.addAll(playersManager.getSchoolBoard(p).getProfessors());
            }
            profs.removeAll(skipStudentColor);
            maxInfluence = skipTowers ?
                    islandsManager.calcInfluence(profs, islandGroupIndex):
                    islandsManager.calcInfluence(tower, profs, islandGroupIndex);
            if (playersByTower.get(tower).contains(playersManager.getCurrentPlayer()))
                maxInfluence += additionalInfluence;
        }
        else
            maxInfluence = 0;

        if (tower != null)
            playersByTower.remove(tower);

        // check influence for others
        for (Map.Entry<Tower, List<Player>> entry: playersByTower.entrySet()) {
            profs = EnumSet.noneOf(StudentColor.class);
            for (Player p: entry.getValue()) {
                profs.addAll(playersManager.getSchoolBoard(p).getProfessors());
            }
            profs.removeAll(skipStudentColor);
            int influence = skipTowers ?
                    islandsManager.calcInfluence(profs, islandGroupIndex):
                    islandsManager.calcInfluence(entry.getKey(), profs, islandGroupIndex);
            if (entry.getValue().contains(playersManager.getCurrentPlayer()))
                influence += additionalInfluence;
            if (influence > maxInfluence) {
                maxInfluence = influence;
                tower = entry.getKey();
            }
        }

        return tower;
    }

    /**
     * Groups players by tower color.
     * @return players grouped by tower color.
     */
    EnumMap<Tower, List<Player>> groupPlayersByTower() {
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
        return playersByTower;
    }

    /**
     * the method is called after calculating the influence, whenever there is a change in the Player that
     * holds the most influence on the IslandGroup; the method removes the current towers from the IslandGroup, puts
     * them back on the SchoolBoard of the Player that they belong to, selects the current most influential Player
     * and puts its towers on the islands. If the IslandGroup originally didn't have any tower, the first part of the
     * method is skipped.
     * @param islandGroupIndex index of the IslandGroup considered
     * @param newTower to be put on the IslandGroup
     */
    void swapTowers(int islandGroupIndex, Tower newTower){
        Tower oldTower = islandsManager.getTower(islandGroupIndex);

        if (newTower != oldTower) {
            EnumMap<Tower, List<Player>> playersByTower = groupPlayersByTower();
            SchoolBoard newSchoolBoard = playersManager.getSchoolBoard(playersByTower.get(newTower).get(0));
            int size = islandsManager.getIslandGroup(islandGroupIndex).size();
            if (oldTower != null) {
                SchoolBoard oldSchoolBoard = playersManager.getSchoolBoard(playersByTower.get(oldTower).get(0));
                oldSchoolBoard.addTowers(size);
            }
            islandsManager.setTower(newTower, islandGroupIndex);
            if (!newSchoolBoard.removeTowers(size)) {
                roundManager.setWinner(newTower);
                gameState = GameState.ENDED;
            }

        }
    }

    /**
     * The method selects the IslandGroups that come before and after the one in the position islandGroupIndex
     * and checks whether they can be merged. The method also handles the effects of the CharacterCard "Herbalist",
     * which can put blocks on an IslandGroup to stop Players from positioning a Tower there: if the method is called
     * on a blocked IslandGroup, the blocks need to be removed.
     * If there are 3 or less island groups the game ends.
     * @param islandGroupIndex of the islandGroup considered
     * @return the number of blocks (prohibit cards) that need to be removed from the IslandGroup and put
     * back on the "Herbalist" card
     */
    int checkMergeIslandGroups(int islandGroupIndex) {
        int numBlocks = 0;
        boolean bothBlocked;

        int previous = Math.floorMod(islandGroupIndex - 1, islandsManager.getNumIslandGroups());
        int next = Math.floorMod(islandGroupIndex + 1, islandsManager.getNumIslandGroups());

        bothBlocked = islandsManager.getIslandGroup(previous).isBlocked() && islandsManager.getIslandGroup(next).isBlocked();

        if (islandsManager.checkMergeNext(islandGroupIndex)) {
            numBlocks += fixIslandGroupsIndexes(bothBlocked, islandGroupIndex, next);
            if (next == 0)
                islandGroupIndex--;
        }

        bothBlocked = islandsManager.getIslandGroup(islandGroupIndex).isBlocked() && islandsManager.getIslandGroup(previous).isBlocked();

        if (islandsManager.checkMergePrevious(islandGroupIndex)) {
            numBlocks += fixIslandGroupsIndexes(bothBlocked, previous, islandGroupIndex);
        }

        if (islandsManager.getNumIslandGroups() <= 3)
            calculateWinners();

        return numBlocks;
    }

    private int fixIslandGroupsIndexes(boolean bothBlocked, int index, int next) {
        int numBlocks = 0;
        if (bothBlocked)
            numBlocks++;
        if (next == 0) {
            index--;
            motherNatureIndex--;
            if (motherNatureIndex < 0)
                motherNatureIndex = islandsManager.getNumIslandGroups() - 1;
        }
        if (motherNatureIndex > index) {
            motherNatureIndex--;
        }
        return numBlocks;
    }

    /**
     * Advances the Game to the next round.
     * If it is the last round it checks the winner.
     */
    void nextRound(){
        if (roundManager.isLastRound()) {
            calculateWinners();
            return;
        }
        roundManager.nextRound();
        playersManager.calculateClockwiseOrder();
        playersManager.nextPlayer();
        playersManager.clearAllPlayedCards();
        refillClouds();

    }

    /**
     * Fills the clouds by getting the students from the bag.
     * If there are no more students this will be the last round.
     */
    void refillClouds() {
        for (Cloud c: clouds) {
            for (int i = 0; i < playersManager.getPreset().getCloudCapacity(); i++) {
                StudentColor s = bag.popRandomStudent();
                if (s == null) {
                    roundManager.setLastRound();
                    return;
                }
                c.addStudent(s, i);
            }
        }
    }

    /**
     * Calculates winners by finding who build the most towers. If equal, who has the most professors.
     * If still equal then there is a draw.
     */
    void calculateWinners() {
        EnumMap<Tower, List<Player>> playersByTower = groupPlayersByTower();

        EnumSet<Tower> winners = EnumSet.noneOf(Tower.class);
        int minNumTower = playersManager.getPreset().getTowersNumber() + 1;
        for (Map.Entry<Tower, List<Player>> entry: playersByTower.entrySet()) {
            int numTower = 0;
            for (Player p: entry.getValue())
                numTower += playersManager.getSchoolBoard(p).getNumTowers();
            if (numTower < minNumTower) {
                minNumTower = numTower;
                winners = EnumSet.of(entry.getKey());
            } else if (numTower == minNumTower)
                winners.add(entry.getKey());
        }
        if (winners.size() > 1) {

            Map<Integer, EnumSet<Tower>> towersByNumProfs = new HashMap<>();
            for (Tower t: winners) {
                int numProfs = 0;
                for (Player p: playersByTower.get(t))
                    numProfs += playersManager.getSchoolBoard(p).getProfessors().size();
                if (towersByNumProfs.containsKey(numProfs))
                    towersByNumProfs.get(numProfs).add(t);
                else {
                    towersByNumProfs.put(numProfs, EnumSet.of(t));
                }
            }

            winners = towersByNumProfs.get(Collections.max(towersByNumProfs.keySet()));
        }

        roundManager.setWinners(winners);
        gameState = GameState.ENDED;
    }

}
