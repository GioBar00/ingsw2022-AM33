package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.GameModelExpert;
import it.polimi.ingsw.server.model.PlayerConvertor;
import it.polimi.ingsw.server.model.cards.CharacterCard;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.islands.IslandsManager;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.PlayersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {
    /**
     * game and its components
     */
    GameModelExpert gmTest = new GameModelExpert(new GameModel(GamePreset.THREE));
    PlayersManager pmTest;
    IslandsManager imTest;
    ArrayList<Tower> playerTowers;
    ArrayList<CharacterType> allCharacters;
    /**
     *  gameView and its components
     */
    GameView gameView;
    GameMode mode;
    GamePreset preset;
    GameState state;
    GamePhase phase;
    List<IslandGroupView> islandsView;
    List<PlayerView> playersView;
    Integer motherNatureIndex;
    Integer reserve;
    List<CharacterCardView> characterCardView;
    Map<String, Integer> playerCoins;

    PlayerConvertor pC = new PlayerConvertor();

    /**
     * set up of the gameModel, before each test
     */
    @BeforeEach
    void setUpInitial(){
        pmTest = gmTest.getModel().getPlayersManager();
        for(int i = 0; i < GamePreset.THREE.getPlayersNumber(); i++){
            pmTest.addPlayer(pC.getPlayer(Integer.toString(i),Wizard.SENSEI)) ;
        }
        imTest = gmTest.getModel().getIslandsManager();
        playerTowers = new ArrayList<>();
        allCharacters = new ArrayList<>();
        allCharacters.addAll(Arrays.asList(CharacterType.values()));
    }

    /**
     * Replaces the character cards in the game and initializes the card.
     * @param card to initialize.
     */
    void initializeCharacterCardOnGameModel(CharacterCard card, int index) {
        gmTest.getCharacterCards().set(index, card);
        card.initialize(gmTest);
    }

    /**
     * @return a random CharacterType
     */
    CharacterType getRandomCC(){
       int random = new Random().nextInt(allCharacters.size());
       CharacterType ct = allCharacters.get(random);
       allCharacters.remove(random);
       return ct;
    }

    /**
     * sets up the gameView and its components
     * @param destPlayer the player to whom the gameView will be sent
     */
    void setUpGameView(Player destPlayer){
        gameView = gmTest.getGameView(destPlayer);

        mode = gameView.getMode();
        preset = gameView.getPreset();
        state = gameView.getState();
        phase = gameView.getPhase();
        islandsView = gameView.getIslandsView();
        playersView = gameView.getPlayersView();
        motherNatureIndex = gameView.getMotherNatureIndex();
        reserve = gameView.getReserve();
        characterCardView = gameView.getCharacterCardView();
        playerCoins = gameView.getPlayerCoins();
    }

    /**
     * test for current gameView
     */
    @Test
    void gameViewTest1(){
        // -------------------------------players + their schoolboard---------------------------------------------------
        // add students to the schoolBoards and keep track of the players' towers
        ArrayList<Tower> playerTowers = new ArrayList<>();
        for (Player p : pmTest.getPlayers()) {
            for (StudentColor s : StudentColor.values()) {
                pmTest.getSchoolBoard(p).addToEntrance(s);
                pmTest.getSchoolBoard(p).addToHall(s);
            }
            playerTowers.add(pmTest.getSchoolBoard(p).getTower());
        }

        // player "0" will have blue and red profs
        pmTest.getSchoolBoard(pmTest.getPlayers().get(0)).addProfessor(StudentColor.BLUE);
        pmTest.getSchoolBoard(pmTest.getPlayers().get(0)).addProfessor(StudentColor.RED);
        // player "1" will have yellow professor
        pmTest.getSchoolBoard(pmTest.getPlayers().get(1)).addProfessor(StudentColor.YELLOW);

        // player "2" will miss two towers
        pmTest.getSchoolBoard(pmTest.getPlayers().get(2)).removeTowers(2);

        // -------------------------------------------islands-----------------------------------------------------------
        // set a tower
        imTest.setTower(playerTowers.get(2), 0);
        imTest.setTower(playerTowers.get(2), 1);
        imTest.setTower(playerTowers.get(0), 2);
        // set some students
        imTest.addStudent(StudentColor.RED, 3);
        imTest.addStudent(StudentColor.BLUE, 4);
        // merge
        imTest.checkMergeNext(1);
        imTest.checkMergePrevious(1);
        // set blocked
        imTest.getIslandGroup(7).setBlocked(true);

        // ----------------------------------------character cards------------------------------------------------------
        gmTest.startGame();
        gmTest.getModel().getRoundManager().startActionPhase();
        // initialize 3 random cards
        ArrayList<CharacterCard> charCards = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            charCards.add(i, getRandomCC().instantiate());
            initializeCharacterCardOnGameModel(charCards.get(i), i);
        }

        // currentPlayer plays an assistant card
        Player current = pmTest.getCurrentPlayer();
        pmTest.currentPlayerPlayed(AssistantCard.ONE);

        // player current will have enough coins to play any CharacterCard (3)
        gmTest.getPlayerCoins().put(current.getNickname(), 10);
        int mn_pose = gmTest.getModel().getMotherNatureIndex();
        setUpGameView(current);

        // --------------------------------------------TESTS------------------------------------------------------------
        assertEquals(mode, gmTest.getModel().getGameMode());
        assertEquals(preset, gmTest.getModel().getPlayersManager().getPreset());
        assertEquals(phase, gmTest.getModel().getPhase());
        assertEquals(state, gmTest.getModel().getGameState());
        assertEquals(reserve, gmTest.getReserve());
        assertEquals(mn_pose, gameView.getMotherNatureIndex());
        // players
        assertEquals(playersView.size(), 3);

        for (int i = 0; i < 3; i++){
            assertEquals(playersView.get(i).getNickname(), Integer.toString(i));
        }

        for (int i = 1; i < preset.getPlayersNumber(); i++){
            if(current.getNickname().equals(playersView.get(i).getNickname())){
                assertEquals(9, playersView.get(i).getNumAssistantCards());
                assertNotNull(playersView.get(i).getAssistantCards());
                assertEquals(AssistantCard.ONE, playersView.get(i).getPlayedCard());
            } else {
                assertEquals(10, playersView.get(i).getNumAssistantCards());
                assertNull(playersView.get(i).getAssistantCards());
                assertNull(playersView.get(i).getPlayedCard());
            }
        }

        for (PlayerView pv: playersView) {
            assertNotNull(pv.getWizard());
            assertNotNull(pv.getSchoolBoardView());
        }

        // schoolboards
        for (PlayerView pv : playersView) {
            for (StudentColor s : StudentColor.values()) {
                assertTrue(pv.getSchoolBoardView().getEntrance().contains(s));
                assertEquals(pv.getSchoolBoardView().getStudentsHall().get(s), 1);
            }
        }

        for (int i = 0; i < 3; i++){
            assertEquals(playersView.get(i).getSchoolBoardView().getTower(), playerTowers.get(i));
        }

        for(PlayerView pv : playersView){
            for(StudentColor s : StudentColor.values()){
                if (pv.getNickname().equals("0")){
                    if (s.equals(StudentColor.BLUE) || s.equals(StudentColor.RED))
                        assertTrue(pv.getSchoolBoardView().getProfessors().contains(s));
                    else
                        assertFalse(pv.getSchoolBoardView().getProfessors().contains(s));
                }
                if (pv.getNickname().equals("1")) {
                    if (s.equals(StudentColor.YELLOW))
                        assertTrue(pv.getSchoolBoardView().getProfessors().contains(s));
                    else
                        assertFalse(pv.getSchoolBoardView().getProfessors().contains(s));
                }
                if (pv.getNickname().equals("2"))
                    assertFalse(pv.getSchoolBoardView().getProfessors().contains(s));
            }
            for (Tower t : Tower.values()){
                if (pv.getNickname().equals("2"))
                    assertEquals(4, pv.getSchoolBoardView().getNumTowers());
                else
                    assertEquals(6, pv.getSchoolBoardView().getNumTowers());
            }
            if (pv.getNickname().equals(current.getNickname()))
                assertEquals(10, playerCoins.get(pv.getNickname()));
            else
                assertEquals(1, playerCoins.get(pv.getNickname()));
        }

        // islands
        assertEquals(2, islandsView.get(0).getIslands().size());
        assertEquals(11, islandsView.size());
        assertEquals(playerTowers.get(2), islandsView.get(0).getIslands().get(0).getTower());
        assertEquals(playerTowers.get(2), islandsView.get(0).getIslands().get(1).getTower());
        assertEquals(playerTowers.get(0), islandsView.get(1).getIslands().get(0).getTower());
        assertTrue(islandsView.get(2).getIslands().get(0).getStudents().get(StudentColor.RED) >= 1);
        assertTrue(islandsView.get(3).getIslands().get(0).getStudents().get(StudentColor.BLUE) >= 1);
        for (int i = 0; i < islandsView.size(); i++) {
            if (i > 1) {
                for (Tower t : Tower.values())
                    assertNull(islandsView.get(i).getIslands().get(0).getTower());
            }
            if (i != 7){
                assertFalse(islandsView.get(i).isBlocked());
            }
        }
        assertTrue(islandsView.get(7).isBlocked());

        // characterCards

        assertEquals(3, characterCardView.size());
        for (CharacterCardView ccv : characterCardView) {
            for (CharacterCard ccog : charCards){
                if (ccv.getType().equals(ccog.getType())){
                    assertTrue(ccv.canBeUsed());
                    assertEquals(ccog.getCost(), ccv.getOriginalCost());
                    assertEquals(0, ccv.getAdditionalCost());
                    if (ccog.canHandleBlocks())
                        assertEquals(4, ccv.getNumBlocks());
                    else
                        assertEquals(0, ccv.getNumBlocks());
                    if (ccog.containsStudents())
                        assertNotNull(ccv.getStudent());
                    else
                        assertNull(ccv.getStudent());
                }
            }
        }
        for (int i = 0; i < 3; i++){
            CharacterCardView ccv1 = characterCardView.get(i);
            for (int j = 0; j < 3; j++)
                if (j != i)
                    assertNotEquals(ccv1.getType(), characterCardView.get(j).getType());
        }
    }
}