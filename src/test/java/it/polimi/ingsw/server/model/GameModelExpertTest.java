package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.cards.Friar;
import it.polimi.ingsw.server.model.cards.Herbalist;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link GameModelExpert} class.
 */
class GameModelExpertTest {

    final PlayerConvertor pC = new PlayerConvertor();

    /**
     * Simulates an expert game match.
     * Inserts the player, each player plays a card and the first player plays his round.
     *
     * @param preset Game Presets
     */
    @ParameterizedTest
    @EnumSource(value = GamePreset.class, names = {"TWO", "THREE"})
    void GameModelTestCreation(GamePreset preset) {
        GameModel gameModel = new GameModel(preset);
        GameModelExpert m = new GameModelExpert(gameModel);

        assertFalse(m.changeTeam("se", Tower.WHITE));
        assertEquals(GameMode.EXPERT, m.getGameMode());
        assertEquals(20, m.reserve);
        assertFalse(m.startGame());
        for (int i = 0; i < preset.getPlayersNumber(); i++) {
            String nick = Integer.toString(i);
            assertTrue(m.addPlayer(pC.getPlayer(nick, Wizard.KING)));
            assertEquals(preset.getPlayersNumber() - i - 1, m.getAvailablePlayerSlots());
        }

        assertFalse(m.addPlayer(pC.getPlayer(": (", Wizard.KING)));

        int currentReserve = 20 - preset.getPlayersNumber();

        assertEquals(GameState.UNINITIALIZED, m.getGameState());
        assertTrue(m.startGame());
        Friar friar = new Friar();
        m.characterCards.set(0, friar);
        m.characterCards.get(0).initialize(m);
        assertEquals(currentReserve, m.reserve);
        assertFalse(m.startGame());
        assertEquals(GameState.STARTED, m.getGameState());

        ArrayList<AssistantCard> played = new ArrayList<>(preset.getPlayersNumber());
        ArrayList<AssistantCard> available = new ArrayList<>(Arrays.asList(AssistantCard.values()));
        for (int i = 0; i < preset.getPlayersNumber(); i++) {
            do {
                int sel = ThreadLocalRandom.current().nextInt(0, available.size());
                if (!played.contains(available.get(sel))) {
                    assertTrue(m.playAssistantCard(available.get(sel)));
                    played.add(available.get(sel));
                    break;
                } else {
                    assertFalse(m.playAssistantCard(available.get(sel)));
                }
            } while (true);
        }

        do {
            int sel = ThreadLocalRandom.current().nextInt(0, available.size());
            if (!played.contains(available.get(sel))) {
                assertFalse(m.playAssistantCard(available.get(sel)));
                break;
            }
        } while (true);

        for (int i = 0; i < preset.getEntranceCapacity(); i++) {
            assertNotNull(m.popStudentFromEntrance(i));
            assertTrue(m.addStudentOnEntrance(StudentColor.BLUE, i));
        }
        assertFalse(m.moveMotherNature(1));
        for (int i = 0; i < preset.getMaxNumMoves(); i++) {
            assertTrue(m.moveStudentToHall(i));
        }
        assertNull(m.popStudentFromEntrance(0));

        currentReserve--;
        assertEquals(currentReserve, m.reserve);
        assertFalse(m.moveStudentToHall(preset.getEntranceCapacity() - 1));

        int firstMotherNature = gameModel.motherNatureIndex;
        Tower expected = gameModel.playersManager.getSchoolBoard().getTower();

        assertFalse(m.getStudentsFromCloud(0));
        assertTrue(m.moveMotherNature(1));
        assertTrue(m.getStudentsFromCloud(0));

        for (int i = 0; i < preset.getEntranceCapacity(); i++) {
            assertTrue(gameModel.playersManager.getSchoolBoard().removeFromEntrance(i));
            assertTrue(gameModel.playersManager.getSchoolBoard().addToEntrance(StudentColor.BLUE));
        }
        for (int i = 0; i < preset.getMaxNumMoves(); i++) {
            assertTrue(m.moveStudentToIsland(i, firstMotherNature));
        }
        gameModel.motherNatureIndex = (firstMotherNature + 11) % gameModel.islandsManager.getNumIslandGroups();

        assertTrue(m.moveMotherNature(1));
        assertEquals(expected, gameModel.islandsManager.getTower(gameModel.motherNatureIndex));
        assertFalse(m.getStudentsFromCloud(0));
        assertTrue(m.getStudentsFromCloud(1));

        if (preset.equals(GamePreset.THREE)) {
            Map<Player, Integer> oldValues = new HashMap<>();
            for (Player p : gameModel.playersManager.getPlayers()) {
                oldValues.put(p, gameModel.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
            }
            m.tryRemoveStudentsFromHalls(StudentColor.BLUE, 3);
            for (Player p : gameModel.playersManager.getPlayers()) {
                switch (oldValues.get(p)) {
                    case 0, 1, 3 ->
                            assertEquals(0, gameModel.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
                    default ->
                            assertEquals(oldValues.get(p) - 3, gameModel.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
                }
            }

            assertFalse(m.applyEffect(null));
            assertFalse(m.activateCharacterCard(10));
            assertFalse(m.activateCharacterCard(-1));
            m.playerCoins.remove(gameModel.playersManager.getCurrentPlayer().getNickname());
            m.playerCoins.put(gameModel.playersManager.getCurrentPlayer().getNickname(), 0);

            assertFalse(m.activateCharacterCard(0));

            m.playerCoins.remove(gameModel.playersManager.getCurrentPlayer().getNickname());
            m.playerCoins.put(gameModel.playersManager.getCurrentPlayer().getNickname(), 10);

            assertTrue(m.activateCharacterCard(0));
            assertFalse(m.activateCharacterCard(0));
            assertEquals(10 - m.characterCards.get(0).getCost(), m.playerCoins.get(gameModel.playersManager.getCurrentPlayer().getNickname()));
            assertEquals(currentReserve + m.characterCards.get(0).getCost() - 1, m.reserve);

            assertFalse(m.moveStudentToIsland(0, 0));
            assertFalse(m.moveStudentToHall(1));
            assertFalse(m.getStudentsFromCloud(2));
        }
    }

    /**
     * Check the implementation of {@link Herbalist} card effect.
     */
    @Test
    void returnBlocksToHerbalist() {
        GameModel gameModel = new GameModel(GamePreset.THREE);
        GameModelExpert m = new GameModelExpert(gameModel);
        Herbalist herbalist = new Herbalist();
        m.characterCards.set(0, herbalist);

        for (int i = 0; i < GamePreset.THREE.getPlayersNumber(); i++) {
            assertTrue(m.addPlayer(pC.getPlayer(i + "", Wizard.KING)));
        }


        assertTrue(m.startGame());

        gameModel.islandsManager.getIslandGroup(0).setBlocked(true);

        int oldBlocks = herbalist.getNumBlocks();

        // make players play an assistant card
        for (int i = 0; i < m.model.playersManager.getPreset().getPlayersNumber(); i++) {
            m.playAssistantCard(AssistantCard.values()[i]);
        }

        Player current = gameModel.playersManager.getCurrentPlayer();
        m.playerCoins.put(current.getNickname(), herbalist.getCost());
        m.activateCharacterCard(0);

        CharacterParameters parameters;

        parameters = new CharacterParameters(2);

        assertTrue(m.applyEffect(parameters));
        assertTrue(gameModel.islandsManager.getIslandGroup(2).isBlocked());

        assertEquals(oldBlocks - 1, herbalist.getNumBlocks());
        for (int i = 0; i < GamePreset.THREE.getEntranceCapacity(); i++) {
            gameModel.playersManager.getSchoolBoard().removeFromEntrance(i);
            gameModel.playersManager.getSchoolBoard().addToEntrance(StudentColor.BLUE, i);

        }
        for (int i = 0; i < GamePreset.THREE.getMaxNumMoves(); i++) {
            m.moveStudentToHall(i);
        }
        Tower currTower = gameModel.playersManager.getSchoolBoard().getTower();
        gameModel.islandsManager.setTower(currTower, 0);
        gameModel.islandsManager.setTower(currTower, 2);
        gameModel.islandsManager.addStudent(StudentColor.BLUE, 1);
        m.calcInfluenceOnIslandGroup(1);
        assertEquals(oldBlocks, herbalist.getNumBlocks());

    }
}