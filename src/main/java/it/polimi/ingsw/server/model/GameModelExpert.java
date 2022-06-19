package it.polimi.ingsw.server.model;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.server.Winners;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.SchoolBoard;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Changes mode of the Game to expert mode.
 * Composite pattern.
 */
public class GameModelExpert implements Game, EffectHandler, ProfessorChecker {

    /**
     * The model of the game
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
    final List<CharacterCard> characterCards;

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
     * Flag set if the farmer card has been activated
     */
    private boolean farmerActivated = false;

    /**
     * Changes mode to expert, gets 3 random character cards and initialises reserve.
     *
     * @param model GameModel to make expert.
     */
    public GameModelExpert(GameModel model) {
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
     *
     * @param playerDetails unique class with details for a player
     * @return if the player was added successfully.
     */
    @Override
    public boolean addPlayer(PlayerDetails playerDetails) {
        if (model.addPlayer(playerDetails)) {
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
        for (Player p : model.playersManager.getPlayers()) {
            playerCoins.put(p.getNickname(), 1);
            reserve--;
        }
        for (CharacterCard c : characterCards)
            c.initialize(this);
    }

    /**
     * Starts the game.
     * Notifies the listeners.
     *
     * @return if the game started successfully.
     */
    @Override
    public boolean startGame() {
        if (model.executeStartGame()) {
            this.initializeGame();
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Current player plays the specified assistant card.
     * Notifies the listeners.
     *
     * @param assistantCard the card the player wants to play.
     * @return if the card was played successfully.
     */
    @Override
    public boolean playAssistantCard(AssistantCard assistantCard) {
        if (model.executePlayAssistantCard(assistantCard)) {
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Moves a student from the Entrance to the Hall of the current player if the player is not activating a card.
     * If the student was added in a "coin space" of the hall and there are enough coins in the reserve then one coin is given to the player.
     * Notifies the listeners.
     *
     * @param entranceIndex of the student that will be moved to the Hall.
     * @return if the student was successfully moved to the hall.
     */
    @Override
    public boolean moveStudentToHall(int entranceIndex) {
        if (characterCardActivating != null)
            return false;

        Player p = model.playersManager.getCurrentPlayer();
        StudentColor sc = model.playersManager.getSchoolBoard(p).getStudentInEntrance(entranceIndex);
        if (sc != null && model.executeMoveStudentToHall(entranceIndex, this)) {

            if (model.playersManager.getSchoolBoard(p).getStudentsInHall(sc) % 3 == 0) {
                if (reserve > 0) {
                    playerCoins.put(p.getNickname(), playerCoins.get(p.getNickname()) + 1);
                    reserve--;
                }
            }
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Selects a student from the Entrance of the current Player's SchoolBoard, removes it from there and
     * moves it to a selected island that is part of a selected IslandGroup only if the player is not activating a card.
     * Notifies the listeners.
     *
     * @param entranceIndex    of the slot occupied by the student that will be moved.
     * @param islandGroupIndex of the IslandGroup that contains the selected island.
     * @return if the student was moved successfully.
     */
    @Override
    public boolean moveStudentToIsland(int entranceIndex, int islandGroupIndex) {
        if (characterCardActivating != null)
            return false;
        if (model.executeMoveStudentToIsland(entranceIndex, islandGroupIndex)) {
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Moves mother nature by num + additional movement if the player is not activating a card.
     * If there are no clouds with students it skips the "choose cloud" phase.
     * Notifies the listeners.
     *
     * @param num of moves that MotherNature should make.
     * @return if the move ended successfully.
     */
    @Override
    public boolean moveMotherNature(int num) {
        if (characterCardActivating != null)
            return false;

        if (model.moveMotherNature(num, model.playersManager.getPlayedCard().getMoves() + additionalMotherNatureMovement, this::calcInfluenceOnIslandGroup)) {
            if (!model.atLeastOneCloudWithStudents()) {
                endTurn();
            }
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Ends the effects of all cards and clears the activated card.
     */
    void endTurn() {
        for (CharacterCard c : characterCards)
            c.revertEffect(this);
        activatedACharacterCard = false;
        additionalMotherNatureMovement = 0;
    }

    /**
     * Moves the student currently residing on a cloud to the Entrance of the current Player if the player is not activating a card.
     * Notifies the listeners.
     *
     * @param cloudIndex of the selected cloud.
     * @return if the students were taken correctly from the cloud and added to the entrance.
     */
    @Override
    public boolean getStudentsFromCloud(int cloudIndex) {
        if (characterCardActivating != null)
            return false;

        if (model.executeGetStudentsFromCloud(cloudIndex)) {
            endTurn();
            model.nextTurn();
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * used to get the current player
     *
     * @return the nickname of the current player
     */
    public String getCurrentPlayer() {
        return model.getCurrentPlayer();
    }

    /**
     * Return the phase of the Game
     *
     * @return GamePhase
     */
    @Override
    public GamePhase getPhase() {
        return model.getPhase();
    }

    /**
     * Activates character card at index if the player has not already activated another card, the index is valid and the player has enough coins.
     * Notifies the listeners.
     *
     * @param index of the character card to activate.
     * @return if the activation was successful.
     */
    @Override
    public boolean activateCharacterCard(int index) {
        if (activatedACharacterCard)
            return false;
        if (index < 0 || index >= characterCards.size())
            return false;
        if (model.getPhase() == GamePhase.PLANNING)
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
        notifyPersonalizedGameState();
        notifyPossibleActions();
        return true;
    }

    /**
     * Applies the effect of the activated character card if the player activated a card.
     * Notifies the listeners.
     *
     * @param parameters to use in the effect.
     * @return if the effect was applied successfully.
     */
    public boolean applyEffect(CharacterParameters parameters) {
        if (characterCardActivating == null)
            return false;

        if (characterCardActivating.applyEffect(this, parameters)) {
            if (characterCardActivating.hasAppliedEffect())
                characterCardActivating = null;
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * Ends the effect of the activated character card.
     * Notifies the listeners.
     *
     * @return if the effect was ended correctly.
     */
    @Override
    public boolean endEffect() {
        if (characterCardActivating == null)
            return false;
        if (characterCardActivating.endEffect()) {
            characterCardActivating = null;
            notifyPersonalizedGameState();
            notifyPossibleActions();
            return true;
        }
        return false;
    }

    /**
     * the method changes the team to which the player belongs; if the player didn't previously belong to any team,
     * it's added as a new member
     *
     * @param nickname of the player
     * @param tower    of the new team
     * @return true if the change was successful
     */
    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        return model.changeTeam(nickname, tower);
    }

    /**
     * Skips the current player's turn.
     */
    @Override
    public void skipCurrentPlayerTurn() {
        if (model.executeSkipTurn()) {
            if (characterCardActivating != null) {
                characterCardActivating.forceEndEffect();
                characterCardActivating = null;
            }
            activatedACharacterCard = false;
            notifyPersonalizedGameState();
            notifyPossibleActions();
        }
    }

    /**
     * Remove a player from the game
     *
     * @param nickname of the player
     * @return if the player was removed
     */
    @Override
    public boolean removePlayer(String nickname) {
        return model.removePlayer(nickname);
    }

    /**
     * Notifies the current game state to a player
     *
     * @param nickname of the player
     */
    @Override
    public void notifyCurrentGameStateToPlayer(String nickname) {
        List<Player> players = model.playersManager.getPlayers();
        for (Player player : players)
            if (player.getNickname().equals(nickname)) {
                notifyPersonalizedGameState(player);
                return;
            }
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
        for (Player p : players) {
            if (p != curr) {
                SchoolBoard sb = model.playersManager.getSchoolBoard(p);
                EnumSet<StudentColor> playerProfs = sb.getProfessors();
                for (StudentColor studentColor : StudentColor.values()) {
                    if (playerProfs.contains(studentColor) && currSB.getStudentsInHall(studentColor) >= sb.getStudentsInHall(studentColor)) {
                        profs.put(studentColor, players.indexOf(p));
                        sb.removeProfessor(studentColor);
                        currSB.addProfessor(studentColor);
                    }
                }
            }
        }
        farmerActivated = true;
        return profs;
    }

    /**
     * Gives back the professors to the original owners.
     *
     * @param original map of the professor and the original owner's player index.
     */
    @Override
    public void restoreProfsToOriginalPlayer(EnumMap<StudentColor, Integer> original) {
        if (farmerActivated) {
            List<Player> players = model.playersManager.getPlayers();
            SchoolBoard currSB = model.playersManager.getSchoolBoard();
            for (StudentColor sc : StudentColor.values()) {
                if (original.containsKey(sc)) {
                    if (model.playersManager.getSchoolBoard(players.get(original.get(sc))).getStudentsInHall(sc) >= currSB.getStudentsInHall(sc)) {
                        currSB.removeProfessor(sc);
                        model.playersManager.getSchoolBoard(players.get(original.get(sc))).addProfessor(sc);
                    }
                }
            }
        }
        farmerActivated = false;
    }

    /**
     * Calculates the influence on an island group.
     *
     * @param islandGroupIndex index of the island group.
     * @return if the calcInfluence when well.
     */
    @Override
    public boolean calcInfluenceOnIslandGroup(int islandGroupIndex) {
        if (islandGroupIndex < 0 || islandGroupIndex >= model.islandsManager.getNumIslandGroups())
            return false;
        CharacterCard herbalist = null;
        for (CharacterCard c : characterCards)
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
        if (sb.addToHall(s)) {
            checkProfessor(s);
            return true;
        }
        return false;
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
        for (Player p : model.playersManager.getPlayers()) {
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

    /**
     * Notifies each player about the new game state.
     */
    private void notifyPersonalizedGameState() {
        for (Player p : model.playersManager.getPlayers())
            notifyPersonalizedGameState(p);
    }

    /**
     * Notifies a player about the new game state.
     *
     * @param p player to notify.
     */
    private void notifyPersonalizedGameState(Player p) {
        if(model.roundManager.getWinners().isEmpty()){
            notifyMessageListener(p.getNickname(), new MessageEvent(this, getCurrentGameState(p)));
        }
        else  model.notifyWinner(p);
    }

    /**
     * @param destPlayer the player to whom the gameView will be sent
     * @return the current game view
     */
    public CurrentGameState getCurrentGameState(Player destPlayer) {
        return new CurrentGameState(new GameView(model.gameMode, model.playersManager.getPreset(), model.gameState, model.roundManager.getGamePhase(),
                getCurrentPlayer(), model.islandsManager.getIslandsView(), model.playersManager.getPlayersView(destPlayer), model.motherNatureIndex,
                reserve, getCharacterCardsView(destPlayer.getNickname()), playerCoins, model.getCloudsView()));
    }

    /**
     * creates the character card view
     *
     * @param destPlayerNick the nickname of player to which the characterCardsView will be sent
     * @return data on the current available characterCards
     */
    public List<CharacterCardView> getCharacterCardsView(String destPlayerNick) {
        List<CharacterCardView> characterCardsView = new ArrayList<>(3);
        for (CharacterCard cc : characterCards) {
            int ogCost = cc.getCost();
            int addCost = cc.getAdditionalCost();
            boolean canUse = false;
            int numBlocks = 0;
            if (cc.canHandleBlocks())
                numBlocks = cc.getNumBlocks();
            EnumMap<StudentColor, Integer> students = null;
            if (cc.containsStudents())
                students = cc.getStudents();
            if (model.playersManager.getCurrentPlayer().getNickname().equals(destPlayerNick)
                    && !model.roundManager.getGamePhase().equals(GamePhase.PLANNING)
                    && cc.getTotalCost() <= playerCoins.get(destPlayerNick)
                    && !activatedACharacterCard
                    && characterCardActivating == null)
                canUse = true;
            if (cc.getType().equals(CharacterType.MINSTREL)) {
                SchoolBoard currentSchoolBoard = model.playersManager.getSchoolBoard();
                boolean isEmpty = true;
                for (StudentColor s : StudentColor.values()) {
                    if (currentSchoolBoard.getStudentsInHall(s) > 0) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty)
                    canUse = false;
            }

            boolean isActivating = characterCardActivating != null && characterCardActivating.equals(cc);
            CharacterCardView ccv = new CharacterCardView(cc.getType(), canUse, ogCost, addCost, numBlocks, students, isActivating, cc.canEndEffect());

            characterCardsView.add(ccv);
        }
        return characterCardsView;
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
     * @return the characterCards arraylist for test purposes
     */
    public List<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    /**
     * @return coins reserve, for test purposes
     */
    public int getReserve() {
        return reserve;
    }

    /**
     * @return map of player coins, for test purposes
     */
    public Map<String, Integer> getPlayerCoins() {
        return playerCoins;
    }

    /**
     * @return model, for test purposes
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Adds a message listener.
     *
     * @param listener the listener to add
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        model.addMessageListener(listener);
    }

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        model.removeMessageListener(listener);
    }

    /**
     * Notifies all listeners.
     *
     * @param event of the message to notify
     */
    @Override
    public void notifyMessageListeners(MessageEvent event) {
        model.notifyMessageListeners(event);
    }

    /**
     * Notifies a specific listener.
     *
     * @param identifier of the listener to notify
     * @param event      of the message to notify
     */
    @Override
    public void notifyMessageListener(String identifier, MessageEvent event) {
        model.notifyMessageListener(identifier, event);
    }

    /**
     * Notifies the current player to perform an action.
     */
    void notifyPossibleActions() {
        if (characterCardActivating != null) {
            Message m = characterCardActivating.getRequiredAction(this);
            if (m != null) {
                Player curr = model.playersManager.getCurrentPlayer();
                notifyMessageListener(curr.getNickname(), new MessageEvent(this, m));
            }
        } else {
            notifyActions();
        }
    }

    /**
     * Notifies the current player to perform an action.
     */
    private void notifyActions() {
        if (model.gameState == GameState.STARTED && model.roundManager.getWinners().isEmpty()) {
            switch (model.roundManager.getGamePhase()) {
                case PLANNING -> model.notifyPlayAssistantCard();
                case MOVE_STUDENTS -> model.notifyMultiplePossibleMoves();
                case MOVE_MOTHER_NATURE -> model.notifyMoveMotherNature(additionalMotherNatureMovement);
                case CHOOSE_CLOUD -> model.notifyChooseCloud();
            }
        }
    }

    /**
     * This method is used to check the professors when a pawn is removed from the schoolboard.
     *
     * @param s the StudentColor that has been removed.
     */
    @Override
    public void checkProfessorOnRemove(StudentColor s) {
        SchoolBoard schoolBoard;
        for (Player p : model.playersManager.getPlayers()) {
            schoolBoard = model.playersManager.getSchoolBoard(p);
            if (schoolBoard.getProfessors().contains(s))
                if (schoolBoard.getStudentsInHall(s) <= 0)
                    schoolBoard.removeProfessor(s);
        }
        Player max = null;
        Player oldOwner = null;
        int maxStudents;
        for (Player p : model.playersManager.getPlayers()) {
            schoolBoard = model.playersManager.getSchoolBoard(p);
            if (schoolBoard.getProfessors().contains(s)) {
                oldOwner = p;
                break;
            }
        }
        if (oldOwner != null) {
            maxStudents = model.playersManager.getSchoolBoard(oldOwner).getStudentsInHall(s);
        } else maxStudents = 0;

        for (Player p : model.playersManager.getPlayers()) {
            schoolBoard = model.playersManager.getSchoolBoard(p);
            if (schoolBoard.getStudentsInHall(s) > maxStudents) {
                maxStudents = schoolBoard.getStudentsInHall(s);
                max = p;
            }
        }

        if (max != null) {
            model.playersManager.getSchoolBoard(max).addProfessor(s);
            if (oldOwner != null)
                model.playersManager.getSchoolBoard(oldOwner).removeProfessor(s);
        }
    }

    /**
     * the method checks that che professor of type s is assigned to the correct SchoolBoard and changes its position
     * in case that the current assignment is incorrect
     *
     * @param s : the color of the professor to be checked
     */
    @Override
    public void checkProfessor(StudentColor s) {
        if (farmerActivated) {
            Player current = model.playersManager.getCurrentPlayer();
            List<Player> players = model.playersManager.getPlayers();
            SchoolBoard currSch = model.playersManager.getSchoolBoard();
            for (Player p : model.playersManager.getPlayers()) {
                if (!p.equals(current)) {
                    SchoolBoard compSch = model.playersManager.getSchoolBoard(p);
                    if (compSch.getStudentsInHall(s) > currSch.getStudentsInHall(s)) return;
                }
            }
            boolean found = false;
            for (Player p : model.playersManager.getPlayers()) {
                if (!p.equals(current) && !found) {
                    SchoolBoard compSch = model.playersManager.getSchoolBoard(p);
                    if (compSch.getStudentsInHall(s) == currSch.getStudentsInHall(s)) {
                        if (compSch.getProfessors().contains(s)) {
                            CharacterCard farmer = null;
                            for (CharacterCard c : characterCards) {
                                if (c.canHandleHistory()) {
                                    farmer = c;
                                    break;
                                }
                            }
                            found = true;
                            compSch.removeProfessor(s);
                            currSch.addProfessor(s);
                            assert farmer != null;
                            farmer.addToOriginal(s, players.indexOf(p));
                        }
                    }
                    if (compSch.getStudentsInHall(s) < currSch.getStudentsInHall(s)) {
                        if (compSch.getProfessors().contains(s)) {
                            found = true;
                            compSch.removeProfessor(s);
                            currSch.addProfessor(s);
                        }
                    }
                }
            }
            if (!found && !currSch.getProfessors().contains(s)) {
                currSch.addProfessor(s);
            }
            return;
        }

        model.checkProfessor(s);
    }
}
