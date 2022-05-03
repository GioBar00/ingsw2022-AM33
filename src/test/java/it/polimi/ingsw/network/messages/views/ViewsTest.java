package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.GameModelTeams;
import it.polimi.ingsw.server.model.PlayerConvertor;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.islands.IslandsManager;
import it.polimi.ingsw.server.model.player.PlayersManager;
import it.polimi.ingsw.server.model.player.SchoolBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ViewsTest {
    PlayerConvertor pC = new PlayerConvertor();

    /**
     * test for the playersView for the standard GameModel with two and three players
     */
    @Test
    void playersViewTwoAndThreeTest(){
        for (GamePreset preset : GamePreset.values()) {
            if (!preset.equals(GamePreset.FOUR)){
                PlayersManager pmTest = new PlayersManager(preset);
                for(int i = 0; i< preset.getPlayersNumber(); i++){
                    assertTrue(pmTest.addPlayer(pC.getPlayer(Integer.toString(i), Wizard.SENSEI, Tower.WHITE)));
                }

                ArrayList<PlayerView> playerViewTest = pmTest.getPlayersView(pmTest.getPlayers().get(0));

                assertEquals(playerViewTest.size(), preset.getPlayersNumber());

                for (int i = 0; i < preset.getPlayersNumber(); i++){
                    assertEquals(playerViewTest.get(i).getNickname(), Integer.toString(i));
                }

                assertNotNull(playerViewTest.get(0).getAssistantCards());
                for (int i = 1; i < preset.getPlayersNumber(); i++){
                    assertNull(playerViewTest.get(i).getAssistantCards());
                }

                for (PlayerView pv: playerViewTest) {
                    assertNotNull(pv.getWizard());
                    assertNotNull(pv.getSchoolBoardView());
                    assertEquals(pv.getNumAssistantCards(), 10);
                    assertNull(pv.getPlayedCard());
                }

                pmTest.currentPlayerPlayed(AssistantCard.ONE);

                ArrayList<PlayerView> playerViewTest_2 = pmTest.getPlayersView(pmTest.getCurrentPlayer());

                assertEquals(playerViewTest_2.size(), preset.getPlayersNumber());
                for (int i = 0; i < preset.getPlayersNumber(); i++){
                    assertEquals(playerViewTest_2.get(i).getNickname(), Integer.toString(i));
                }

                assertNotNull(playerViewTest.get(0).getAssistantCards());
                assertFalse(playerViewTest_2.get(0).getAssistantCards().contains(AssistantCard.ONE));
                assertEquals(playerViewTest_2.get(0).getNumAssistantCards(), 9);
                for (int i = 1; i < preset.getPlayersNumber(); i++){
                    assertNull(playerViewTest.get(i).getAssistantCards());
                    assertEquals(playerViewTest_2.get(i).getNumAssistantCards(), 10);
                }

                for (PlayerView pv: playerViewTest) {
                    assertNotNull(pv.getWizard());
                    assertNotNull(pv.getSchoolBoardView());
                    assertNull(pv.getPlayedCard());
                }
            }
        }
    }

    /**
     * test for the playersView in the GameModel with four players; it also checks on the teamsView
     */
    @Test
    void playerFourTest(){
        GameModel gmTeams = new GameModelTeams();
        PlayersManager pmTest = gmTeams.getPlayersManager();

        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteLeader", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackLeader", Wizard.SENSEI, Tower.BLACK)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteOther", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackOther", Wizard.SENSEI, Tower.BLACK)));


        ArrayList<PlayerView> playerViewTest = pmTest.getPlayersView(pmTest.getPlayers().get(0));

        assertEquals(playerViewTest.size(), GamePreset.FOUR.getPlayersNumber());

        assertEquals(playerViewTest.get(0).getNickname(), "whiteLeader");
        assertEquals(playerViewTest.get(1).getNickname(), "blackLeader");
        assertEquals(playerViewTest.get(2).getNickname(), "whiteOther");
        assertEquals(playerViewTest.get(3).getNickname(), "blackOther");

        assertNotNull(playerViewTest.get(0).getAssistantCards());
        for (int i = 1; i < GamePreset.FOUR.getPlayersNumber(); i++){
            assertNull(playerViewTest.get(i).getAssistantCards());
        }

        assertEquals(playerViewTest.get(0).getNumAssistantCards(), 10);

        for (PlayerView pv: playerViewTest) {
            assertNotNull(pv.getWizard());
            assertNotNull(pv.getSchoolBoardView());
            assertEquals(pv.getNumAssistantCards(), 10);
            assertNull(pv.getPlayedCard());
        }
    }

    /**
     * test for the SchoolBoardView, iterated for every Preset and for every Tower type
     */
    @Test
    void schoolBoardViewTest(){
        for (GamePreset preset : GamePreset.values()) {
            for (Tower tower : Tower.values()) {
                SchoolBoard schoolBoardTest = new SchoolBoard(preset.getEntranceCapacity(), tower, preset.getTowersNumber());

                for (StudentColor s : StudentColor.values()) {
                    schoolBoardTest.addToEntrance(s);
                    schoolBoardTest.addToHall(s);
                }

                schoolBoardTest.addProfessor(StudentColor.BLUE);

                schoolBoardTest.removeTowers(3);

                SchoolBoardView schoolBoardView = schoolBoardTest.getSchoolBoardView();

                for (StudentColor s : StudentColor.values()) {
                    assertTrue(schoolBoardView.getEntrance().contains(s));
                    assertEquals(schoolBoardView.getStudentsHall().get(s), 1);
                    if (s.equals(StudentColor.BLUE))
                        assertTrue(schoolBoardView.getProfessors().contains(s));
                    else
                        assertFalse(schoolBoardView.getProfessors().contains(s));
                }

                assertEquals(schoolBoardView.getTower(), tower);
                assertEquals(schoolBoardView.getNumTowers(), preset.getTowersNumber() - 3);
            }
        }
    }

    /**
     * test for the islandsView: check tower colors, students numbers, correct merging and blocks
     */
    @Test
    void islandsViewTest() {
        IslandsManager im = new IslandsManager();

        // set a tower
        im.setTower(Tower.WHITE, 0);
        im.setTower(Tower.WHITE, 1);
        im.setTower(Tower.BLACK, 2);
        // set some students
        im.addStudent(StudentColor.RED, 3);
        im.addStudent(StudentColor.BLUE, 4);
        // merge (the black tower moves in 1)
        im.checkMergeNext(1);
        im.checkMergePrevious(1);
        // set blocked
        im.getIslandGroup(7).setBlocked(true);

        // get the view
        ArrayList<IslandGroupView> iv = im.getIslandsView();

        // check that the view corresponds to the actual islands
        assertEquals(2, iv.get(0).getIslands().size());
        assertEquals(11, iv.size());
        assertEquals(Tower.WHITE, iv.get(0).getIslands().get(0).getTower());
        assertEquals(Tower.WHITE, iv.get(0).getIslands().get(1).getTower());
        assertEquals(Tower.BLACK, iv.get(1).getIslands().get(0).getTower());
        assertEquals(1, iv.get(2).getIslands().get(0).getStudents().get(StudentColor.RED));
        assertEquals(1, iv.get(3).getIslands().get(0).getStudents().get(StudentColor.BLUE));
        for (int i = 0; i < iv.size(); i++) {
            if (i != 2 && i != 3) {
                for (StudentColor s : StudentColor.values())
                    assertEquals(0, iv.get(i).getIslands().get(0).getStudents().get(s));
            }
            if (i > 1) {
                for (Tower t : Tower.values())
                    assertNull(iv.get(i).getIslands().get(0).getTower());
            }
            if (i != 7){
                assertFalse(iv.get(i).isBlocked());
            }
        }
        assertTrue(iv.get(7).isBlocked());
    }
}