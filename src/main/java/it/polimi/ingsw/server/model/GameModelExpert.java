package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.listeners.MessageEvent;
import it.polimi.ingsw.server.listeners.MessageListener;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.cards.CharacterCard;
import it.polimi.ingsw.server.model.cards.EffectHandler;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.SchoolBoard;
import it.polimi.ingsw.network.messages.messagesView.CharacterCardView;
import it.polimi.ingsw.network.messages.messagesView.GameView;
import it.polimi.ingsw.network.messages.messagesView.PlayerView;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Changes mode of the Game to expert mode.
 * Composite pattern.
 */
class GameModelExpert implements Game, EffectHandler {

    /**
     * GameModel to change in expert mode.
     */
    final GameModel model;

    /**
     * Reserve of coins in the game.
     */
    int reserve;
    /**
     * Number of coins for each player.
     */
    final Map<String, Integer> playerCoins = new HashMap<>();

    /**
     * Character cards in the game.
     */
    final ArrayList<CharacterCard> characterCards;
    /**
     * Character card that is being activated.
     */
    CharacterCard characterCardActivating;
    /**
     * If a card was activated this turn.
     */
    boolean activatedACharacterCard = false;
    /**
     * Additional movement that mother nature can make.
     */
    int additionalMotherNatureMovement = 0;
    /**
     * If to skip counting towers for influence.
     */
    boolean skipTowers = false;
    /**
     * Additional influence points to add to current player.
     */
    int additionalInfluence = 0;
    /**
     * Student colors to skip during influence counting.
     */
    final EnumSet<StudentColor> skipStudentColors = EnumSet.noneOf(StudentColor.class);

    /**
     * Changes mode to expert, gets 3 random character cards and initialises reserve.
     * @param model GameModel to make expert.
     */
    GameModelExpert(GameModel model) {
        this.model = model;
        model.gameMode = GameMode.EXPERT;

        characterCards = new ArrayList<>(3);
        LinkedList<CharacterType> types = new LinkedList<>(Arrays.asList(CharacterType.values()));
        for (int i = 0; i < 3; i++) {
            int sel = ThreadLocalRandom.current().nextInt(0, types.size());
            characterCards.add(types.get(sel).instantiate());
            types.remove(sel);
        }

        reserve = 20;

    }

    /**
     * @return current game mode.
     */
    @Override
    public GameMode getGameMode() {
        return model.getGameMode();
    }

    /**
     * @return current game state.
     */
    @Override
    public GameState getGameState() {
        return model.getGameState();
    }

    /**
     * @return number of available player slots.
     */
    @Override
    public int getAvailablePlayerSlots() {
        return model.getAvailablePlayerSlots();
    }

    /**
     * Adds a new player to the game. Initializes the coins of the player if the player was added successfully.
     * @param nickname unique identifier of a player
     * @return if the player was added successfully.
     */
    @Override
    public boolean addPlayer(String nickname) {
        if (model.addPlayer(nickname)) {
            List<Player> players = model.playersManager.getPlayers();
            Player p = players.get(players.size() - 1);
            playerCoins.put(p.getNickname(), 0);
            return true;
        }
        return false;
    }

    /**
     * Initializes the game. Initializes the cards and gives one coin to each player.
     * Add the remaining student on the bag.
     */
     void initializeGame() {
        for (Player p: model.playersManager.getPlayers()) {
            playerCoins.put(p.getNickname(), 1);
            reserve--;
        }
        for (CharacterCard c: characterCards)
            c.initialize(this);
    }

    /**
     * Starts the game.
     * @return if the game started successfully.
     */
    @Override
    public boolean startGame() {
        if(model.startGame()){
            this.initializeGame();
            return true;
        }
        return false;
    }

    /**
     * Current player plays the specified assistant card.
     * @param assistantCard the card the player wants to play.
     * @return if the card was played successfully.
     */
    @Override
    public boolean playAssistantCard(AssistantCard assistantCard) {
        return model.playAssistantCard(assistantCard);
    }

    /**
     * Moves a student from the Entrance to the Hall of the current player if the player is not activating a card.
     * If the student was added in a "coin space" of the hall and there are enough coins in the reserve then one coin is given to the player.
     * @param entranceIndex of the student that will be moved to the Hall.
     * @return if the student was successfully moved to the hall.
     */
    @Override
    public boolean moveStudentToHall(int entranceIndex) {
        if (characterCardActivating != null)
            return false;

        Player p = model.playersManager.getCurrentPlayer();
        StudentColor sc = model.playersManager.getSchoolBoard(p).getStudentInEntrance(entranceIndex);
        if (sc != null && model.moveStudentToHall(entranceIndex)) {

            if (model.playersManager.getSchoolBoard(p).getStudentsInHall(sc) % 3 == 0) {
                if (reserve > 0) {
                    playerCoins.put(p.getNickname(), playerCoins.get(p.getNickname()) + 1);
                    reserve--;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Selects a student from the Entrance of the current Player's SchoolBoard, removes it from there and
     * moves it to a selected island that is part of a selected IslandGroup only if the player is not activating a card.
     * @param entranceIndex of the slot occupied by the student that will be moved.
     * @param islandGroupIndex of the IslandGroup that contains the selected island.
     * @return if the student was moved successfully.
     */
    @Override
    public boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex) {
        if (characterCardActivating != null)
            return false;
        return model.moveStudentToIsland(entranceIndex, islandGroupIndex);
    }

    /**
     * Moves mother nature by num + additional movement if the player is not activating a card.
     * If there are no clouds with students it skips the "choose cloud" phase.
     * @param num of moves that MotherNature should make.
     * @return if the move ended successfully.
     */
    @Override
    public boolean moveMotherNature(int num) {
        if (characterCardActivating != null)
            return false;

        if (model.moveMotherNature(num, model.playersManager.getPlayedCard().getMoves() + additionalMotherNatureMovement)) {
            if (model.atLeastOneCloudWithStudents()) {
                endTurn();
            }
            return true;
        }
        return false;
    }

    /**
     * Ends the effects of all cards and clears the activated card.
     */
    void endTurn() {
        for (CharacterCard c: characterCards)
            c.revertEffect(this);
        activatedACharacterCard = false;
    }

    /**
     * Moves the student currently residing on a cloud to the Entrance of the current Player if the player is not activating a card.
     * @param cloudIndex of the selected cloud.
     * @return if the students were taken correctly from the cloud and added to the entrance.
     */
    @Override
    public boolean getStudentsFromCloud(int cloudIndex) {
        if (characterCardActivating != null)
            return false;

        if (model.getStudentsFromCloud(cloudIndex)) {
            endTurn();
            return true;
        }
        return false;
    }

    /**
     * used to get the current player
     * @return the nickname of the current player
     */
    public String getCurrentPlayer(){
        return model.getCurrentPlayer();
    }

    /**
     * Return the phase of the Game
     * @return GamePhase
     */
    @Override
    public GamePhase getPhase() {
        return model.getPhase();
    }

    /**
     * Activates character card at index if the player has not already activated another card, the index is valid and the player has enough coins.
     * @param index of the character card to activate.
     * @return if the activation was successful.
     */
    @Override
    public boolean activateCharacterCard(int index) {
        if (activatedACharacterCard)
            return false;
        if (index < 0 || index >= characterCards.size())
            return false;
        Player curr = model.playersManager.getCurrentPlayer();
        int totalCost = characterCards.get(index).getTotalCost();
        if (playerCoins.get(curr.getNickname()) < totalCost)
            return false;

        playerCoins.put(curr.getNickname(), playerCoins.get(curr.getNickname()) - totalCost);
        reserve += totalCost - 1;
        characterCardActivating = characterCards.get(index);
        activatedACharacterCard = true;
        if (characterCardActivating.getRequiredChoicesNumber() == 0)
            applyEffect(null);
        return true;
    }

    /**
     * Applies the effect of the activated character card if the player activated a card.
     * @param parameters to use in the effect.
     * @return if the effect was applied successfully.
     */
    public boolean applyEffect(CharacterParameters parameters) {
        if (characterCardActivating == null)
            return false;

        if (characterCardActivating.applyEffect(this, parameters)) {
            if (characterCardActivating.hasAppliedEffect())
                characterCardActivating = null;
            return true;
        }
        return false;
    }

    /**
     * Ends the effect of the activated character card.
     * @return if the effect was ended correctly.
     */
    @Override
    public boolean endEffect() {
        if (characterCardActivating == null)
            return false;
        if (characterCardActivating.endEffect()) {
            characterCardActivating = null;
            return true;
        }
        return false;
    }

    /**
     * the method changes the team to which the player belongs; if the player didn't previously belong to any team,
     * it's added as a new member
     * @param nickname of the player
     * @param tower of the new team
     * @return true if the change was successful
     */
    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        return model.changeTeam(nickname, tower);
    }

    /**
     * Gets a random student from the bag.
     *
     * @return random student got from the bag.
     */
    @Override
    public StudentColor getStudentFromBag() {
        return model.bag.popRandomStudent();
    }

    /**
     * Adds a student to a specific island.
     *
     * @param s                student to add
     * @param islandGroupIndex index of the island group.
     * @return if the add was successful.
     */
    @Override
    public boolean addStudentToIsland(StudentColor s, int islandGroupIndex) {
        return model.islandsManager.addStudent(s, islandGroupIndex);
    }

    /**
     * Moves the professors to the current player if it has the same number of student in the hall of the current owner.
     *
     * @return map of the professors moves and their original owner's player index.
     */
    @Override
    public EnumMap<StudentColor, Integer> tryGiveProfsToCurrPlayer() {
        EnumMap<StudentColor, Integer> profs = new EnumMap<>(StudentColor.class);
        List<Player> players = model.playersManager.getPlayers();
        Player curr = model.playersManager.getCurrentPlayer();
        SchoolBoard currSB = model.playersManager.getSchoolBoard();
        for (Player p: players) {
            if (p != curr) {
                SchoolBoard sb = model.playersManager.getSchoolBoard(p);
                EnumSet<StudentColor> playerProfs = sb.getProfessors();
                for (StudentColor studentColor: StudentColor.values()) {
                    if (playerProfs.contains(studentColor) && currSB.getStudentsInHall(studentColor) >= sb.getStudentsInHall(studentColor)) {
                        profs.put(studentColor, players.indexOf(p));
                        sb.removeProfessor(studentColor);
                        currSB.addProfessor(studentColor);
                    }
                }
            }
        }
        return profs;
    }

    /**
     * Gives back the professors to the original owners.
     *
     * @param original map of the professor and the original owner's player index.
     */
    @Override
    public void restoreProfsToOriginalPlayer(EnumMap<StudentColor, Integer> original) {
        List<Player> players = model.playersManager.getPlayers();
        SchoolBoard currSB = model.playersManager.getSchoolBoard();
        for (StudentColor sc: StudentColor.values()) {
            if (original.containsKey(sc)) {
                currSB.removeProfessor(sc);
                model.playersManager.getSchoolBoard(players.get(original.get(sc))).addProfessor(sc);
            }
        }
    }

    /**
     * Calculates the influence on an island group.
     * @param islandGroupIndex index of the island group.
     * @return if the calcInfluence when well.
     */
    @Override
    public boolean calcInfluenceOnIslandGroup(int islandGroupIndex) {
        if (islandGroupIndex < 0 || islandGroupIndex >= model.islandsManager.getNumIslandGroups())
            return false;
        CharacterCard herbalist = null;
        for (CharacterCard c: characterCards)
            if (c.canHandleBlocks()) {
                herbalist = c;
                break;
            }
        if (model.islandsManager.getIslandGroup(islandGroupIndex).isBlocked()) {
            if (herbalist == null)
                return false;
            model.islandsManager.getIslandGroup(islandGroupIndex).setBlocked(false);
            herbalist.addNumBlocks(1);
            return true;
        }
        Tower newTower = model.checkInfluence(islandGroupIndex, skipTowers, additionalInfluence, skipStudentColors);
        if (newTower != null && newTower != model.islandsManager.getTower(islandGroupIndex)) {
            model.swapTowers(islandGroupIndex, newTower);
            int numBlocks = model.checkMergeIslandGroups(islandGroupIndex);
            if (numBlocks > 0) {
                if (herbalist == null)
                    return false;
                herbalist.addNumBlocks(numBlocks);
            }
        }
        return true;
    }

    /**
     * Adds additional movements to the maximum movement of mother nature.
     *
     * @param num additional movement to add.
     */
    @Override
    public void addAdditionalMovement(int num) {
        additionalMotherNatureMovement += num;
    }

    /**
     * Blocks and island group.
     *
     * @param islandGroupIndex island group index to block.
     * @return if the island was blocked.
     */
    @Override
    public boolean blockIslandGroup(int islandGroupIndex) {
        if (islandGroupIndex < 0 || islandGroupIndex >= model.islandsManager.getNumIslandGroups())
            return false;
        if (model.islandsManager.getIslandGroup(islandGroupIndex).isBlocked())
            return false;
        model.islandsManager.getIslandGroup(islandGroupIndex).setBlocked(true);
        return true;
    }

    /**
     * Ignores the towers when calculating influence this turn.
     */
    @Override
    public void ignoreTowers(boolean ignore) {
        skipTowers = ignore;
    }

    /**
     * Removes a student from the entrance of current player's school board.
     *
     * @param entranceIndex index of the entrance.
     * @return student at entranceIndex.
     */
    @Override
    public StudentColor popStudentFromEntrance(int entranceIndex) {
        StudentColor s = model.playersManager.getSchoolBoard().getStudentInEntrance(entranceIndex);
        if (s != null)
            model.playersManager.getSchoolBoard().removeFromEntrance(entranceIndex);
        return s;
    }

    /**
     * Gets the students in the entrance of the current player's school board.
     *
     * @return the students in the entrance.
     */
    @Override
    public ArrayList<StudentColor> getStudentsInEntrance() {
        return model.playersManager.getSchoolBoard().getStudentsInEntrance();
    }

    /**
     * Adds a student to the entrance of current player's school board.
     *
     * @param entranceIndex index of the entrance.
     * @return if the student was added successfully.
     */
    @Override
    public boolean addStudentOnEntrance(StudentColor s, int entranceIndex) {
        return model.playersManager.getSchoolBoard().addToEntrance(s, entranceIndex);
    }

    /**
     * Adds additional influence when calculating influence this turn.
     *
     * @param num additional influence to add.
     */
    @Override
    public void addAdditionalInfluence(int num) {
        additionalInfluence += num;
    }

    /**
     * Removes a student from the current player's hall.
     *
     * @param s student color to remove.
     * @return if the remove was successful.
     */
    @Override
    public boolean removeStudentFromHall(StudentColor s) {
        SchoolBoard sb = model.playersManager.getSchoolBoard();
        return sb.removeFromHall(s, 1);
    }

    /**
     * Adds a student to the current player's hall.
     *
     * @param s student color to add.
     * @return if the add was successful.
     */
    @Override
    public boolean addStudentToHall(StudentColor s) {
        SchoolBoard sb = model.playersManager.getSchoolBoard();
        return sb.addToHall(s);
    }

    /**
     * Gets the number of students of a specific color in the hall.
     *
     * @param s color of the student.
     * @return number of students in the hall.
     */
    @Override
    public int getStudentsInHall(StudentColor s) {
        return model.playersManager.getSchoolBoard().getStudentsInHall(s);
    }

    /**
     * Tries to remove the ideal amount of students from the hall of all players and puts them back in the bag.
     * If there aren't enough students, removes only the available ones.
     *
     * @param s           student color of student to remove.
     * @param idealAmount ideal amount of student to remove from halls.
     */
    @Override
    public void tryRemoveStudentsFromHalls(StudentColor s, int idealAmount) {
        for (Player p: model.playersManager.getPlayers()) {
            SchoolBoard sb = model.playersManager.getSchoolBoard(p);
            sb.tryRemoveFromHall(s, idealAmount);
        }
    }

    /**
     * @return the current student colors skipped.
     */
    @Override
    public EnumSet<StudentColor> getSkippedStudentColors() {
        return skipStudentColors;
    }

    public GameView getGameView(Player destPlayer){
        GameView gameView = new GameView(getGameMode(), getGameState(), model.playersManager, model.roundManager, model.islandsManager, destPlayer, reserve, model.motherNatureIndex);
        setCoinsInView(gameView);
        setCharacterCardsView(gameView, destPlayer);
        return gameView;
    }

    public void setCoinsInView(GameView gameView){
        for (PlayerView pv: gameView.getPlayersView()) {
            pv.setCoins(playerCoins.get(pv.getNickname()));
        }
    }

    public void setCharacterCardsView(GameView gameView, Player destPlayer){
        for (CharacterCard cc: characterCards) {
            CharacterCardView ccv = new CharacterCardView(cc.getType());
            ccv.setOriginalCost(cc.getCost());
            ccv.setAdditionalCost(cc.getAdditionalCost());
            // if it's the destPlayer turn and the game is not in the planning stage and the destPlayer has enough coins to
            // use the CharacterCard, then the card is available for use
            if (model.playersManager.getCurrentPlayer().equals(destPlayer) && !model.roundManager.getGamePhase().equals(GamePhase.PLANNING) && cc.getTotalCost() <= playerCoins.get(destPlayer.getNickname()))
                ccv.setCanBeUsed(true);
            gameView.getCharacterCardView().add(ccv);
        }
    }

    /**
     * @return the available island indexes.
     */
    @Override
    public Set<Integer> getAvailableIslandIndexes() {
        int num = model.islandsManager.getNumIslandGroups();
        Set<Integer> availableIslandIndexes = new HashSet<>();
        for (int i = 0; i < num; i++)
            availableIslandIndexes.add(i);
        return availableIslandIndexes;
    }

    /**
     * Adds a message listener.
     *
     * @param listener the listener to add
     */
    @Override
    public void addListener(MessageListener listener) {
        model.addListener(listener);
    }

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeListener(MessageListener listener) {
        model.removeListener(listener);
    }

    /**
     * Notifies all listeners.
     *
     * @param event of the message to notify
     */
    @Override
    public void notifyListeners(MessageEvent event) {
        model.notifyListeners(event);
    }
}
