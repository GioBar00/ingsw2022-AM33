package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.GameModelExpert;
import it.polimi.ingsw.server.model.PlayerConvertor;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.event.PrintServiceAttributeListener;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

class GameStateAndTeamsTests {
    GameModelExpert gm = new GameModelExpert(new GameModel(GamePreset.FOUR));

    PlayerConvertor pC =new PlayerConvertor();
    /**
     * initialized the gameModel before each test
     */
    @BeforeEach
    void startGame(){
        for(int i = 0; i < GamePreset.FOUR.getPlayersNumber(); i++){
            Tower t;
            if (i%2 == 0)
                t = Tower.WHITE;
            else
                t = Tower.BLACK;
            gm.getModel().getPlayersManager().addPlayer(pC.getPlayer(Integer.toString(i), Wizard.THREE, t));
        }
        gm.startGame();
    }

    /**
     * test for the message CurrentGameState
     */
    @Test
    void CurrentGameStateTest(){
        CurrentGameState original = new CurrentGameState(gm.getGameView(gm.getModel().getPlayersManager().getCurrentPlayer()));
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof CurrentGameState);
        // checks that the information in the message matches
        assertEquals(original.getGameView().getMode(), ((CurrentGameState) m).getGameView().getMode());
        assertEquals(original.getGameView().getPreset(), ((CurrentGameState) m).getGameView().getPreset());
        assertEquals(original.getGameView().getPhase(), ((CurrentGameState) m).getGameView().getPhase());
        assertEquals(original.getGameView().getState(), ((CurrentGameState) m).getGameView().getState());
        for (int i = 0; i < gm.getModel().getPlayersManager().getPlayers().size(); i++) {
            // check for each player
            PlayerView ogpv = original.getGameView().getPlayersView().get(i);
            PlayerView mpv = ((CurrentGameState) m).getGameView().getPlayersView().get(i);
            assertEquals(ogpv.getNickname(), mpv.getNickname());
            assertEquals(ogpv.getNumAssistantCards(), mpv.getNumAssistantCards());
            assertEquals(ogpv.getAssistantCards(), mpv.getAssistantCards());
            assertEquals(ogpv.getWizard(), mpv.getWizard());
            assertEquals(ogpv.getPlayedCard(), mpv.getPlayedCard());
            // check for its schoolboard
            SchoolBoardView ogsb = ogpv.getSchoolBoardView();
            SchoolBoardView msb = mpv.getSchoolBoardView();
            assertEquals(ogsb.getTower(), msb.getTower());
            assertEquals(ogsb.getNumTowers(), msb.getNumTowers());
            assertEquals(ogsb.getStudentsHall(), msb.getStudentsHall());
            assertEquals(ogsb.getProfessors(), msb.getProfessors());
            assertEquals(ogsb.getEntrance(), msb.getEntrance());
        }
        for (int i = 0; i < gm.getModel().getIslandsManager().getNumIslandGroups(); i++) {
            // check each group
            IslandGroupView ogigv = original.getGameView().getIslandsView().get(i);
            IslandGroupView migv = ((CurrentGameState) m).getGameView().getIslandsView().get(i);
            assertEquals(ogigv.getIslands().size(), migv.getIslands().size());
            assertEquals(ogigv.isBlocked(), migv.isBlocked());
            for (int j = 0; j < gm.getModel().getIslandsManager().getIslandGroup(i).size(); j++) {
                assertEquals(ogigv.getIslands().get(j).getTower(), migv.getIslands().get(j).getTower());
                assertEquals(ogigv.getIslands().get(j).getStudents(), migv.getIslands().get(j).getStudents());
            }
        }
        assertEquals(original.getGameView().getMotherNatureIndex(), ((CurrentGameState) m).getGameView().getMotherNatureIndex());
        for (int i = 0; i < 3; i++){
            CharacterCardView ogccv = original.getGameView().getCharacterCardView().get(i);
            CharacterCardView mccv = original.getGameView().getCharacterCardView().get(i);
            assertEquals(ogccv.getType(), mccv.getType());
            assertEquals(ogccv.getOriginalCost(), mccv.getOriginalCost());
            assertEquals(ogccv.getAdditionalCost(), mccv.getAdditionalCost());
            assertEquals(ogccv.getNumBlocks(), mccv.getNumBlocks());
            assertEquals(ogccv.getStudent(), mccv.getStudent());
        }
        assertEquals(original.getGameView().getReserve(), ((CurrentGameState) m).getGameView().getReserve());
        assertEquals(original.getGameView().getPlayerCoins(), ((CurrentGameState) m).getGameView().getPlayerCoins());
        // test null message
        original = new CurrentGameState(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test first if of isValid()
        Player dest = gm.getModel().getPlayersManager().getCurrentPlayer();
        original = new CurrentGameState(new GameView(gm.getGameMode(), null, gm.getGameState(), gm.getPhase(), gm.getModel().getIslandsManager().getIslandsView(), gm.getModel().getPlayersManager().getPlayersView(dest), gm.getModel().getMotherNatureIndex(), gm.getReserve(), gm.getCharacterCardsView(dest.getNickname()), gm.getPlayerCoins()));
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test second if fo isValid()
        original = new CurrentGameState(new GameView(gm.getGameMode(), gm.getModel().getPlayersManager().getPreset(), gm.getGameState(), gm.getPhase(), gm.getModel().getIslandsManager().getIslandsView(), null, gm.getModel().getMotherNatureIndex(), gm.getReserve(), gm.getCharacterCardsView(dest.getNickname()), gm.getPlayerCoins()));
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }


    /*
     * test for the message CurrentTeams
     */
    /*
    @Test
    void CurrentTeamsTest(){
        CurrentTeams original = new CurrentTeams(gm.getModel().getPlayersManager().getTeamsView());
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof CurrentTeams);
        assertEquals(original.getTeamsView().getLobby(), ((CurrentTeams) m).getTeamsView().getLobby());
        assertEquals(original.getTeamsView().getTeams(), ((CurrentTeams) m).getTeamsView().getTeams());
        // test null message
        original = new CurrentTeams(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // there's no test for empty fields because both the teams and the lobby can have null fields
    }
    */
}