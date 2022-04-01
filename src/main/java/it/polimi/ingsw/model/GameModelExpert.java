package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.cards.CharacterCard;
import it.polimi.ingsw.model.cards.EffectHandler;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.SchoolBoard;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class GameModelExpert implements Game, EffectHandler {

    /**
     * GameModel to change in expert mode.
     */
    private final GameModel model;

    /**
     * Reserve of coins in the game.
     */
    private int reserve;
    /**
     * Number of coins for each player.
     */
    private final Map<Player, Integer> playerCoins = new HashMap<>();

    /**
     * Character cards in the game.
     */
    private final ArrayList<CharacterCard> characterCards;
    /**
     * Character card that is being activated.
     */
    private CharacterCard characterCardActivating;
    /**
     * Additional movement that mother nature can make.
     */
    private int additionalMotherNatureMovement = 0;
    /**
     * If to skip counting towers for influence.
     */
    private boolean skipTowers = false;
    /**
     * Additional influence points to add to current player.
     */
    private int additionalInfluence = 0;
    /**
     * Student colors to skip during influence counting.
     */
    private final EnumSet<StudentColor> skipStudentColors = EnumSet.noneOf(StudentColor.class);

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

    @Override
    public GameMode getGameMode() {
        return model.getGameMode();
    }

    @Override
    public GameState getGameState() {
        return model.getGameState();
    }

    @Override
    public int getAvailablePlayerSlots() {
        return model.getAvailablePlayerSlots();
    }

    @Override
    public void addPlayer(String nickname) throws NameAlreadyBoundException, NoPermissionException {
        model.addPlayer(nickname);
        List<Player> players = model.playersManager.getPlayers();
        Player p = players.get(players.size() - 1);
        playerCoins.put(p, 0);
    }

    @Override
    public void initializeGame() throws NoPermissionException {
        model.initializeGame();
        for (Player p: model.playersManager.getPlayers()) {
            playerCoins.put(p, 1);
            reserve--;
        }
        for (CharacterCard c: characterCards)
            c.initialize(this);
    }

    @Override
    public void startGame() {
        model.startGame();
    }

    @Override
    public void playAssistantCard(AssistantCard assistantCard) throws Exception {
        model.playAssistantCard(assistantCard);
    }

    @Override
    public void moveStudentToHall(int entranceIndex) throws Exception {
        Player p = model.playersManager.getCurrentPlayer();
        StudentColor sc = model.playersManager.getSchoolBoard(p).getStudentInEntrance(entranceIndex);
        model.moveStudentToHall(entranceIndex);
        if (model.playersManager.getSchoolBoard(p).getStudentsInHall(sc) % 3 == 0) {
            if (reserve > 0) {
                playerCoins.put(p, playerCoins.get(p) + 1);
                reserve--;
            }
        }
    }

    @Override
    public void moveStudentToIsland(int entranceIndex, int islandGroupIndex, int islandIndex) throws Exception {
        model.moveStudentToIsland(entranceIndex, islandGroupIndex, islandIndex);
    }

    @Override
    public void moveMotherNature(int num) throws LimitExceededException {
        if (num <= model.playersManager.getPlayedCard(model.playersManager.getCurrentPlayer()).getMoves() + additionalMotherNatureMovement) {
            model.motherNatureIndex = (model.motherNatureIndex + num) % 12;
            calcInfluenceOnIslandGroup(model.motherNatureIndex);
            model.checkWinner();
            if (model.playersManager.getCurrentPlayer().equals(model.playersManager.getLastPlayer())) {
                model.endActionPhase();
            }
        }
        else { throw new LimitExceededException(); }
    }

    @Override
    public void getStudentsFromCloud(int cloudIndex) throws LimitExceededException {
        model.getStudentsFromCloud(cloudIndex);
        for (CharacterCard c: characterCards)
            c.endEffect(this);
    }

    @Override
    public void activateCharacterCard(int index) {
        Player curr = model.playersManager.getCurrentPlayer();
        if (playerCoins.get(curr) < characterCards.get(index).getTotalCost())
            return;
        playerCoins.put(curr, playerCoins.get(curr) - characterCards.get(index).getTotalCost());
        characterCardActivating = characterCards.get(index);
    }

    @Override
    public void applyEffect(EnumMap<StudentColor, List<Integer>> pairs) {
        characterCardActivating.applyEffect(this, pairs);
        characterCardActivating = null;
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
     * @param islandIndex      index of the island in the island group.
     */
    @Override
    public void addStudentToIsland(StudentColor s, int islandGroupIndex, int islandIndex) {
        model.islandsManager.addStudent(s, islandGroupIndex, islandIndex);
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
                        try {
                            sb.removeProfessor(studentColor);
                            currSB.addProfessor(studentColor);
                        } catch (Exception ignored) {}

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
                try {
                    currSB.removeProfessor(sc);
                    model.playersManager.getSchoolBoard(players.get(original.get(sc))).addProfessor(sc);
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Calculates the influence on an island group.
     * @param islandGroupIndex index of the island group.
     */
    @Override
    public void calcInfluenceOnIslandGroup(int islandGroupIndex) {
        if (model.islandsManager.getIslandGroup(islandGroupIndex).isBlocked()) {
            model.islandsManager.getIslandGroup(islandGroupIndex).setBlocked(false);
            for (CharacterCard c: characterCards)
                if (c.canHandleBlocks())
                    c.addNumBlocks(1);
            return;
        }
        Tower tower = model.checkInfluence(islandGroupIndex, skipTowers, additionalInfluence, skipStudentColors);
        if (tower != null && tower != model.islandsManager.getTower(islandGroupIndex)) {
            model.swapTowers(islandGroupIndex, tower);
            int numBlocks = model.checkMergeIslandGroups(islandGroupIndex);
            for (CharacterCard c: characterCards)
                if (c.canHandleBlocks()) {
                    c.addNumBlocks(numBlocks);
                    return;
                }

        }
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
     */
    @Override
    public void blockIslandGroup(int islandGroupIndex) {
        model.islandsManager.getIslandGroup(islandGroupIndex).setBlocked(true);
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
    public StudentColor getStudentFromEntrance(int entranceIndex) {
        return model.playersManager.getSchoolBoard().removeFromEntrance(entranceIndex);
    }

    /**
     * Adds a student to the entrance of current player's school board.
     *
     * @param entranceIndex index of the entrance.
     */
    @Override
    public void addStudentOnEntrance(StudentColor s, int entranceIndex) throws LimitExceededException {
        model.playersManager.getSchoolBoard().addToEntrance(s, entranceIndex);
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
     * Ignores student color when calculating influence this turn.
     *
     * @param s student color to ignore.
     */
    @Override
    public void ignoreStudentColor(StudentColor s, boolean ignore) {
        if (skipStudentColors.contains(s)) {
            if (!ignore)
                skipStudentColors.remove(s);
        }
        else
            if (ignore)
                skipStudentColors.add(s);

    }

    /**
     * Removes a student from the current player's hall.
     *
     * @param s student color to remove.
     */
    @Override
    public void removeStudentFromHall(StudentColor s) {
        SchoolBoard sb = model.playersManager.getSchoolBoard();
        if (sb.getStudentsInHall(s) <= 0)
            throw new NoSuchElementException();
        sb.removeFromHall(s, 1);
    }

    /**
     * Adds a student to the current player's hall.
     *
     * @param s student color to add.
     */
    @Override
    public void addStudentToHall(StudentColor s) throws LimitExceededException {
        SchoolBoard sb = model.playersManager.getSchoolBoard();
        sb.addToHall(s);
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
            sb.removeFromHall(s, idealAmount);
        }
    }
}
