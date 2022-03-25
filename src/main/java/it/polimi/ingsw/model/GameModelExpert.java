package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.cards.CharacterCard;
import it.polimi.ingsw.model.cards.EffectHandler;
import it.polimi.ingsw.model.player.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class GameModelExpert implements Game, EffectHandler {

    private final GameModel model;

    private int reserve;
    private final Map<Player, Integer> playerCoins = new HashMap<>();

    private final ArrayList<CharacterCard> characterCards;
    private CharacterCard characterCardActivating;
    private int additionalMotherNatureMovement = 0;
    private boolean skipTowers = false;
    private int additionalInfluence = 0;
    private EnumSet<StudentColor> skipStudentColor = EnumSet.noneOf(StudentColor.class);

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
    public void addPlayer(String nickname) {
        // TODO: remember to add player to playersCoin
    }

    @Override
    public void initializeGame() {
        // FIXME give one coin to each player

    }

    @Override
    public void startGame() {

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
    public void moveMotherNature(int num) {

    }

    @Override
    public void getStudentsFromCloud(int cloudIndex) {

    }

    @Override
    public void activateCharacterCard(int index) {
        characterCardActivating = characterCards.get(index);
    }

    @Override
    public void applyEffect(EnumMap<StudentColor, List<Integer>> pairs) {
        characterCardActivating.applyEffect(this, pairs);
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
     * @param islandGroup      index of the island in the island group.
     */
    @Override
    public void addStudentToIsland(StudentColor s, int islandGroupIndex, int islandGroup) {

    }

    /**
     * Moves the professors to the current player if it has the same number of student in the hall of the current owner.
     *
     * @return map of the professors moves and their original owner's player index.
     */
    @Override
    public EnumMap<StudentColor, Integer> tryGiveProfsToCurrPlayer() {
        return null;
    }

    /**
     * Gives back the professors to the original owners.
     *
     * @param original map of the professor and the original owner's player index.
     */
    @Override
    public void restoreProfsToOriginalPlayer(EnumMap<StudentColor, Integer> original) {

    }

    /**
     * Calculates the influence on an island group.
     *
     * @param islandGroupIndex index of the island group.
     */
    @Override
    public void calcInfluenceOnIslandGroup(int islandGroupIndex) {

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
        return null;
    }

    /**
     * Adds a student to the entrance of current player's school board.
     *
     * @param entranceIndex index of the entrance.
     */
    @Override
    public void addStudentOnEntrance(int entranceIndex) {

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
        if (skipStudentColor.contains(s)) {
            if (!ignore)
                skipStudentColor.remove(s);
        }
        else
            if (ignore)
                skipStudentColor.add(s);

    }

    /**
     * Removes a student from the current player's hall.
     *
     * @param s student color to remove.
     */
    @Override
    public void removeStudentFromHall(StudentColor s) {

    }

    /**
     * Adds a student to the current player's hall.
     *
     * @param s student color to add.
     */
    @Override
    public void addStudentToHall(StudentColor s) {

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

    }
}
