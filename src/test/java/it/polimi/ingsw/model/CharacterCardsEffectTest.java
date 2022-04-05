package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.GamePreset;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.SchoolBoard;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests all character effects.
 */
class CharacterCardsEffectTest {
    /**
     * game model expert.
     */
    GameModelExpert gme = new GameModelExpert(new GameModel(GamePreset.THREE));

    /**
     * adds players and initializes the game.
     */
    @BeforeEach
    void prepareModel() {
        gme.addPlayer("1");
        gme.addPlayer("2");
        gme.addPlayer("3");
        gme.initializeGame();
    }

    /**
     * Replaces the character cards in the game and initializes the card.
     * @param card to initialize.
     */
    void initializeCharacterCardOnGameModel(CharacterCard card) {
        gme.characterCards.set(0, card);
        gme.characterCards.set(1, card);
        gme.characterCards.set(2, card);
        card.initialize(gme);
    }

    /**
     * Tests for empty and null values of pairs.
     * @param card to test.
     */
    void testEffectForNullAndEmptyPair(CharacterCard card) {
        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        // test empty pairs
        assertFalse(card.applyEffect(gme, pairs));
        // test null pair
        pairs.add(new Pair<>(null, null));
        assertFalse(card.applyEffect(gme, pairs));
        pairs.clear();
        
    }

    /**
     * Tests for empty and null values of the list with null as the first element of the pair.
     * @param card to test.
     */
    void testEffectForNullAndInvalidInteger(CharacterCard card) {
        testEffectForNullAndInvalidInteger(card, null);
    }

    /**
     * Tests for empty and null values of the list with a valid value as the first element of the pair.
     * @param card to test.
     * @param valid value.
     */
    void testEffectForNullAndInvalidInteger(CharacterCard card, StudentColor valid) {
        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();
        // test null integer
        pairs.add(new Pair<>(valid, null));
        assertFalse(card.applyEffect(gme, pairs));
        pairs.clear();
        // test invalid
        pairs.add(new Pair<>(valid, -1));
        assertFalse(card.applyEffect(gme, pairs));
        pairs.clear();
    }

    /**
     * Tests if the initial and other students are equal.
     * @param initial students.
     * @param other students.
     */
    void checkStudentsAreEqual(EnumMap<StudentColor, Integer> initial, EnumMap<StudentColor, Integer> other) {
        for (StudentColor s: StudentColor.values())
            assertEquals(initial.get(s), other.get(s));
    }

    /**
     * Tests if the other students are the initial ones removing the removedFromCard ones and adding new ones.
     * @param initial students.
     * @param other students.
     * @param removedFromCard students removed.
     */
    void checkIfGotStudentsFromBag(EnumMap<StudentColor, Integer> initial, EnumMap<StudentColor, Integer> other, List<StudentColor> removedFromCard) {
        for (StudentColor s: removedFromCard)
            initial.put(s, initial.get(s) - 1);

        int numToFind = removedFromCard.size();
        for (StudentColor st: StudentColor.values()) {
            if (other.get(st) > initial.get(st) && numToFind > 0) {
                numToFind -= other.get(st) - initial.get(st);
                assertTrue(numToFind >= 0);
            } else
                assertEquals(initial.get(st), other.get(st));
        }

    }

    /**
     * Tests Centaur's effect.
     */
    @Test
    void centaurEffectTest() {
        CharacterCard centaur = new Centaur();
        initializeCharacterCardOnGameModel(centaur);

        gme.skipTowers = false;
        centaur.applyEffect(gme, null);
        assertTrue(gme.skipTowers);
        centaur.endEffect(gme);
        assertFalse(gme.skipTowers);

        assertEquals(1, centaur.getAdditionalCost());
    }

    /**
     * Tests Farmer's effect.
     */
    @Test
    void farmerEffectTest() {
        CharacterCard farmer = new Farmer();
        initializeCharacterCardOnGameModel(farmer);

        Player current = gme.model.playersManager.getCurrentPlayer();
        SchoolBoard schoolBoardCurrent = gme.model.playersManager.getSchoolBoard();
        SchoolBoard other = null;
        for (Player p: gme.model.playersManager.getPlayers()) {
            if (!p.equals(current)) {
                other = gme.model.playersManager.getSchoolBoard(p);
                break;
            }
        }
        assert other != null;
        for (StudentColor s: StudentColor.values()) {
            schoolBoardCurrent.removeProfessor(s);
            other.removeProfessor(s);
            schoolBoardCurrent.tryRemoveFromHall(s, 10);
            other.tryRemoveFromHall(s, 10);
        }

        other.addToHall(StudentColor.BLUE);
        schoolBoardCurrent.addToHall(StudentColor.BLUE);
        other.addProfessor(StudentColor.BLUE);

        other.addToHall(StudentColor.RED);
        schoolBoardCurrent.addToHall(StudentColor.RED);
        schoolBoardCurrent.addProfessor(StudentColor.RED);

        other.addToHall(StudentColor.PINK);
        other.addToHall(StudentColor.PINK);
        schoolBoardCurrent.addToHall(StudentColor.PINK);
        other.addProfessor(StudentColor.PINK);

        schoolBoardCurrent.addToHall(StudentColor.GREEN);
        schoolBoardCurrent.addProfessor(StudentColor.GREEN);

        farmer.applyEffect(gme, null);

        assertTrue(schoolBoardCurrent.getProfessors().contains(StudentColor.RED));
        assertTrue(schoolBoardCurrent.getProfessors().contains(StudentColor.BLUE));
        assertFalse(schoolBoardCurrent.getProfessors().contains(StudentColor.PINK));
        assertTrue(schoolBoardCurrent.getProfessors().contains(StudentColor.GREEN));

        farmer.endEffect(gme);

        assertTrue(schoolBoardCurrent.getProfessors().contains(StudentColor.RED));
        assertFalse(schoolBoardCurrent.getProfessors().contains(StudentColor.BLUE));
        assertFalse(schoolBoardCurrent.getProfessors().contains(StudentColor.PINK));
        assertTrue(schoolBoardCurrent.getProfessors().contains(StudentColor.GREEN));

        assertEquals(1, farmer.getAdditionalCost());
    }

    /**
     * Tests Farmer's effect.
     */
    @Test
    void friarEffectTest() {
        CharacterCard friar = new Friar();
        initializeCharacterCardOnGameModel(friar);

        // gets students on friar
        assertTrue(friar.containsStudents());
        EnumMap<StudentColor, Integer> initialStudents = friar.getStudents();
        List<StudentColor> students = new LinkedList<>();
        for (StudentColor s: initialStudents.keySet()) {
            students.addAll(Collections.nCopies(initialStudents.get(s), s));
        }
        assertEquals(4, students.size());

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        StudentColor s = students.get(0);

        
        testEffectForNullAndEmptyPair(friar);
        testEffectForNullAndInvalidInteger(friar, s);
        // test with correct parameters
        int initial = gme.model.islandsManager.getIslandGroup(0).getIslands().get(0).getNumStudents(s);
        pairs.add(new Pair<>(s, 0));
        assertTrue(friar.applyEffect(gme, pairs));
        // check if student added correctly
        assertEquals(initial + 1, gme.model.islandsManager.getIslandGroup(0).getIslands().get(0).getNumStudents(s));
        // check if got student from bag
        List<StudentColor> studentsMoved = new LinkedList<>();
        studentsMoved.add(s);
        checkIfGotStudentsFromBag(initialStudents, friar.getStudents(), studentsMoved);

        assertEquals(1, friar.getAdditionalCost());
    }

    /**
     * Tests Harvester's effect.
     */
    @Test
    void harvesterEffectTest() {
        CharacterCard harvester = new Harvester();
        initializeCharacterCardOnGameModel(harvester);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();
        
        testEffectForNullAndEmptyPair(harvester);
        
        // test adding already skipped color
        gme.skipStudentColors.clear();
        gme.skipStudentColors.add(StudentColor.BLUE);
        pairs.add(new Pair<>(StudentColor.BLUE, null));
        assertFalse(harvester.applyEffect(gme, pairs));
        pairs.clear();

        // test normal effect
        pairs.add(new Pair<>(StudentColor.RED, null));
        assertTrue(harvester.applyEffect(gme, pairs));
        assertTrue(gme.skipStudentColors.contains(StudentColor.RED));
        
        // check end effect
        harvester.endEffect(gme);
        assertFalse(gme.skipStudentColors.contains(StudentColor.BLUE));
        assertFalse(gme.skipStudentColors.contains(StudentColor.RED));

        assertEquals(1, harvester.getAdditionalCost());
    }

    /**
     * Tests Herald's effect.
     */
    @Test
    void heraldEffectTest() {
        CharacterCard herald = new Herald();
        initializeCharacterCardOnGameModel(herald);
        
        testEffectForNullAndInvalidInteger(herald);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();
        // test wrong island group index
        pairs.add(new Pair<>(null, 13));
        assertFalse(herald.applyEffect(gme, pairs));
        pairs.clear();
        // test normal effect
        pairs.add(new Pair<>(null, 0));
        assertTrue(herald.applyEffect(gme, pairs));

        assertEquals(1, herald.getAdditionalCost());
    }

    /**
     * Tests Herbalist's effect.
     */
    @Test
    void herbalistEffectTest() {
        Herbalist herbalist = new Herbalist();
        initializeCharacterCardOnGameModel(herbalist);

        assertEquals(4, herbalist.getNumBlocks());
        
        testEffectForNullAndInvalidInteger(herbalist);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        // test wrong island group index
        pairs.add(new Pair<>(null, 13));
        assertFalse(herbalist.applyEffect(gme, pairs));
        pairs.clear();
        // test normal effect
        for (int i = 0; i < 4; i++) {
            gme.model.islandsManager.getIslandGroup(0).setBlocked(false);
            pairs.add(new Pair<>(null, 0));
            assertTrue(herbalist.applyEffect(gme, pairs));
            assertTrue(gme.model.islandsManager.getIslandGroup(0).isBlocked());
            assertEquals(i + 1, herbalist.getAdditionalCost());
            assertEquals(4 - i - 1, herbalist.getNumBlocks());
            pairs.clear();
        }
        // test already blocked
        pairs.add(new Pair<>(null, 0));
        assertFalse(herbalist.applyEffect(gme, pairs));
        pairs.clear();
        // test no more blocks
        gme.model.islandsManager.getIslandGroup(0).setBlocked(false);
        assertFalse(herbalist.applyEffect(gme, pairs));
        
        assertEquals(herbalist.getCost() + 4, herbalist.getTotalCost());
    }

    /**
     * Tests Jester's effect.
     */
    @Test
    void jesterEffectTest() {
        CharacterCard jester = new Jester();
        initializeCharacterCardOnGameModel(jester);

        // gets students on jester
        assertTrue(jester.containsStudents());
        EnumMap<StudentColor, Integer> initialStudents = jester.getStudents();
        List<StudentColor> students = new LinkedList<>();
        for (StudentColor s: initialStudents.keySet()) {
            students.addAll(Collections.nCopies(initialStudents.get(s), s));
        }
        assertEquals(6, students.size());

        StudentColor s = students.get(0);
        testEffectForNullAndEmptyPair(jester);
        testEffectForNullAndInvalidInteger(jester, s);

        SchoolBoard sb = gme.model.playersManager.getSchoolBoard();
        for (int i = 0; i < sb.getEntranceCapacity(); i++) {
            sb.removeFromEntrance(i);
        }
        sb.addToEntrance(StudentColor.GREEN);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.RED);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.YELLOW);
        sb.addToEntrance(StudentColor.RED);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();
        // test with one move
        pairs.add(new Pair<>(students.get(0), 0));
        assertTrue(jester.applyEffect(gme, pairs));
        pairs.clear();
        // test with two moves
        pairs.add(new Pair<>(students.get(1), 1));
        pairs.add(new Pair<>(students.get(2), 2));
        assertTrue(jester.applyEffect(gme, pairs));
        pairs.clear();
        // test with three moves
        pairs.add(new Pair<>(students.get(3), 3));
        pairs.add(new Pair<>(students.get(4), 4));
        pairs.add(new Pair<>(students.get(5), 5));
        assertTrue(jester.applyEffect(gme, pairs));
        pairs.clear();

        for (int i = 0; i < sb.getEntranceCapacity(); i++) {
            sb.removeFromEntrance(i);
        }
        sb.addToEntrance(StudentColor.PINK);
        sb.addToEntrance(StudentColor.RED);
        sb.addToEntrance(StudentColor.GREEN);

        initialStudents = jester.getStudents();
        // test with four moves
        pairs.add(new Pair<>(StudentColor.GREEN, 2));
        pairs.add(new Pair<>(StudentColor.GREEN, 2));
        pairs.add(new Pair<>(StudentColor.GREEN, 2));
        pairs.add(new Pair<>(StudentColor.GREEN, 2));
        assertFalse(jester.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, jester.getStudents());
        pairs.clear();
        // test invalid move sequence
        pairs.add(new Pair<>(StudentColor.YELLOW, 2));
        pairs.add(new Pair<>(StudentColor.YELLOW, 0));
        assertFalse(jester.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, jester.getStudents());
        pairs.clear();

        assertEquals(3, jester.getAdditionalCost());
    }

    /**
     * Tests Minstrel's effect.
     */
    @Test
    void minstrelEffectTest() {
        CharacterCard minstrel = new Minstrel();
        initializeCharacterCardOnGameModel(minstrel);

        SchoolBoard sb = gme.model.playersManager.getSchoolBoard();
        for (StudentColor s: StudentColor.values())
            sb.tryRemoveFromHall(s, 10);
        for (int i = 0; i < 10; i++)
            sb.addToHall(StudentColor.PINK);

        sb.addToHall(StudentColor.BLUE);
        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        testEffectForNullAndEmptyPair(minstrel);
        testEffectForNullAndInvalidInteger(minstrel, StudentColor.BLUE);

        for (int i = 0; i < sb.getEntranceCapacity(); i++) {
            sb.removeFromEntrance(i);
        }
        sb.addToEntrance(StudentColor.GREEN);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.RED);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.YELLOW);

        // test invalid student in hall
        pairs.add(new Pair<>(StudentColor.RED, 0));
        assertFalse(minstrel.applyEffect(gme, pairs));
        pairs.clear();

        // test invalid entrance index
        pairs.add(new Pair<>(StudentColor.BLUE, 5));
        assertFalse(minstrel.applyEffect(gme, pairs));
        pairs.clear();


        EnumMap<StudentColor, Integer> initialHall = gme.getHall();
        // test over max size
        pairs.add(new Pair<>(StudentColor.PINK, 0));
        pairs.add(new Pair<>(StudentColor.PINK, 0));
        pairs.add(new Pair<>(StudentColor.PINK, 0));
        assertFalse(minstrel.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialHall, gme.getHall());
        pairs.clear();
        // test invalid second move
        pairs.add(new Pair<>(StudentColor.BLUE, 0));
        pairs.add(new Pair<>(StudentColor.YELLOW, 0));
        assertFalse(minstrel.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialHall, gme.getHall());
        pairs.clear();

        pairs.add(new Pair<>(StudentColor.BLUE, 0));
        pairs.add(new Pair<>(StudentColor.YELLOW, 0));
        assertFalse(minstrel.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialHall, gme.getHall());
        pairs.clear();
        // test valid effect
        pairs.add(new Pair<>(StudentColor.PINK, 0));
        pairs.add(new Pair<>(StudentColor.GREEN, 0));
        assertTrue(minstrel.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialHall, gme.getHall());
        pairs.clear();
        // test valid effect with only one move
        pairs.add(new Pair<>(StudentColor.PINK, 4));
        assertTrue(minstrel.applyEffect(gme, pairs));
        assertEquals(1, gme.getStudentsInHall(StudentColor.YELLOW));
        assertEquals(initialHall.get(StudentColor.PINK) - 1, gme.getStudentsInHall(StudentColor.PINK));

        assertEquals(2, minstrel.getAdditionalCost());
    }

    /**
     * Tests Knight's effect.
     */
    @Test
    void knightEffectTest() {
        CharacterCard knight = new Knight();
        initializeCharacterCardOnGameModel(knight);

        int initialAdditionalInfluence = gme.additionalInfluence;
        assertTrue(knight.applyEffect(gme, null));
        assertEquals(initialAdditionalInfluence + 2, gme.additionalInfluence);
        knight.endEffect(gme);
        assertEquals(initialAdditionalInfluence, gme.additionalInfluence);

        assertEquals(1, knight.getAdditionalCost());
    }

    /**
     * Tests Mailman's effect.
     */
    @Test
    void mailmanEffectTest() {
        CharacterCard mailman = new Mailman();
        initializeCharacterCardOnGameModel(mailman);

        int initialAdditionalMotherNatureMovement = gme.additionalMotherNatureMovement;
        assertTrue(mailman.applyEffect(gme, null));
        assertEquals(initialAdditionalMotherNatureMovement + 2, gme.additionalMotherNatureMovement);
        mailman.endEffect(gme);
        assertEquals(initialAdditionalMotherNatureMovement, gme.additionalMotherNatureMovement);

        assertEquals(1, mailman.getAdditionalCost());
    }

    /**
     * Tests Princess's effect.
     */
    @Test
    void princessEffectTest() {
        CharacterCard princess = new Princess();
        initializeCharacterCardOnGameModel(princess);

        testEffectForNullAndEmptyPair(princess);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        // gets students on princess
        assertTrue(princess.containsStudents());
        EnumMap<StudentColor, Integer> initialStudents = princess.getStudents();
        List<StudentColor> students = new LinkedList<>();
        for (StudentColor s: initialStudents.keySet()) {
            students.addAll(Collections.nCopies(initialStudents.get(s), s));
        }
        assertEquals(4, students.size());

        // test student princess doesn't have
        List<StudentColor> notOnPrincess = Arrays.stream(StudentColor.values()).filter(s -> !students.contains(s)).toList();
        pairs.add(new Pair<>(notOnPrincess.get(0), null));
        assertFalse(princess.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, princess.getStudents());
        pairs.clear();
        // test full hall
        SchoolBoard current = gme.model.playersManager.getSchoolBoard();
        for (StudentColor s: StudentColor.values())
            current.tryRemoveFromHall(s, 10);
        StudentColor s = students.get(0);
        for (int i = 0; i < 10; i++)
            current.addToHall(s);
        pairs.add(new Pair<>(s, null));
        assertFalse(princess.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, princess.getStudents());
        pairs.clear();
        // test normal effect
        current.tryRemoveFromHall(s, 10);
        pairs.add(new Pair<>(s, null));
        assertTrue(princess.applyEffect(gme, pairs));
        assertEquals(1, current.getStudentsInHall(s));

        // check if got student from bag
        List<StudentColor> studentsMoved = new LinkedList<>();
        studentsMoved.add(s);
        checkIfGotStudentsFromBag(initialStudents, princess.getStudents(), studentsMoved);

        assertEquals(1, princess.getAdditionalCost());
    }

    /**
     * Tests Thief's effect.
     */
    @Test
    void thiefEffectTest() {
        CharacterCard thief = new Thief();
        initializeCharacterCardOnGameModel(thief);

        testEffectForNullAndEmptyPair(thief);

        LinkedPairList<StudentColor, Integer> pairs = new LinkedPairList<>();

        // test normal effect
        StudentColor s = StudentColor.BLUE;
        Map<SchoolBoard, Integer> initialValues = new HashMap<>();
        for (Player p: gme.model.playersManager.getPlayers()) {
            SchoolBoard sb = gme.model.playersManager.getSchoolBoard(p);
            sb.tryRemoveFromHall(s, 10);
            int num = p != gme.model.playersManager.getCurrentPlayer() ?
                    ThreadLocalRandom.current().nextInt(0, 11):
                    ThreadLocalRandom.current().nextInt(4, 11);
            for (int i = 0; i < num; i++)
                sb.addToHall(s);
            initialValues.put(sb, sb.getStudentsInHall(s));
        }
        pairs.add(new Pair<>(s, null));
        assertTrue(thief.applyEffect(gme, pairs));
        // check final values
        for (Player p: gme.model.playersManager.getPlayers()) {
            SchoolBoard sb = gme.model.playersManager.getSchoolBoard(p);
            if (initialValues.get(sb) > 3)
                assertEquals(initialValues.get(sb) - 3, sb.getStudentsInHall(s));
            else
                assertEquals(0, sb.getStudentsInHall(s));
        }

        assertEquals(1, thief.getAdditionalCost());
    }
}