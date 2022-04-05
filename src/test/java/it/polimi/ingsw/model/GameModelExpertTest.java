package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.cards.Herbalist;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class GameModelExpertTest {

    /**
     * Tests the simulation of an expert game. Stress out the correct
     * @param preset Game Presets
     */
    @ParameterizedTest
    @EnumSource(value = GamePreset.class, names = {"TWO", "THREE"})
    void GameModelTestCreation(GamePreset preset) {
        GameModel gameModel = new GameModel(preset);
        GameModelExpert m = new GameModelExpert(gameModel);

        assertEquals(GameMode.EXPERT, m.getGameMode());
        assertEquals(20, m.reserve);
        assertFalse(m.initializeGame());
        for (int i = 0; i < preset.getPlayersNumber(); i++) {
            String nick = Integer.toString(i);
            assertTrue(m.addPlayer(nick));
            assertEquals(preset.getPlayersNumber() - i - 1, m.getAvailablePlayerSlots());
        }

        assertFalse(m.addPlayer(":("));

        int currentReserve = 20 - preset.getPlayersNumber();

        assertEquals(GameState.UNINITIALIZED, m.getGameState());
        assertFalse(m.startGame());
        assertTrue(m.initializeGame());
        assertEquals(currentReserve, m.reserve);
        assertEquals(GameState.INITIALIZED, m.getGameState());
        assertFalse(m.playAssistantCard(AssistantCard.FOUR));
        assertTrue(m.startGame());
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
        gameModel.motherNatureIndex = (firstMotherNature + 11) % 12;

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
                    case 0, 1, 3 -> assertEquals(0, gameModel.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
                    default -> assertEquals(oldValues.get(p) - 3, gameModel.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
                }
            }

            assertFalse(m.applyEffect(new LinkedPairList<>()));
            assertFalse(m.activateCharacterCard(10));
            assertFalse(m.activateCharacterCard(-1));
            m.playerCoins.remove(gameModel.playersManager.getCurrentPlayer());
            m.playerCoins.put(gameModel.playersManager.getCurrentPlayer(), 0);

            assertFalse(m.activateCharacterCard(0));

            m.playerCoins.remove(gameModel.playersManager.getCurrentPlayer());
            m.playerCoins.put(gameModel.playersManager.getCurrentPlayer(), 10);

            assertTrue(m.activateCharacterCard(0));
            assertFalse(m.activateCharacterCard(0));
            assertEquals(10 - m.characterCards.get(0).getCost(), m.playerCoins.get(gameModel.playersManager.getCurrentPlayer()));
            assertEquals(currentReserve + m.characterCards.get(0).getCost() - 1, m.reserve);

            assertFalse(m.moveStudentToIsland(0, 0));
            assertFalse(m.moveStudentToHall(1));
            assertFalse(m.getStudentsFromCloud(2));
            //LinkedPairList<> test
            //assertFalse(m.applyEffect(new LinkedPairList<>()));
        }
    }

    @Test
    void returnBlocksToHerbalist() {
        GameModel gameModel = new GameModel(GamePreset.THREE);
        GameModelExpert m = new GameModelExpert(gameModel);
        Herbalist herbalist = new Herbalist();
        m.characterCards.set(0, herbalist);

        for (int i = 0; i < GamePreset.THREE.getPlayersNumber(); i++) {
            assertTrue(m.addPlayer("" + i));
        }

        assertTrue(m.initializeGame());
        assertTrue(m.startGame());


        gameModel.islandsManager.getIslandGroup(0).setBlocked(true);

        int oldBlocks = herbalist.getNumBlocks();

        gameModel.roundManager.startActionPhase();

        Player current = gameModel.playersManager.getCurrentPlayer();
        m.playerCoins.remove(current);
        m.playerCoins.put(current, herbalist.getCost());
        m.activateCharacterCard(0);
        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();


        pairs.add(new Pair<>(null, 2));

        assertTrue(m.applyEffect(pairs));
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