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
        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();

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
    void testEffectForNullAndEmptyList(CharacterCard card) {
        testEffectForNullAndEmptyList(card, null);
    }

    /**
     * Tests for empty and null values of the list with a valid value as the first element of the pair.
     * @param card to test.
     * @param valid value.
     */
    void testEffectForNullAndEmptyList(CharacterCard card, StudentColor valid) {
        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        LinkedList<Integer> second = new LinkedList<>();
        
        // test null list
        pairs.add(new Pair<>(valid, null));
        assertFalse(card.applyEffect(gme, pairs));
        pairs.clear();
        // test empty list
        pairs.add(new Pair<>(valid, second));
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
        Centaur centaur = new Centaur();
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
        Farmer farmer = new Farmer();
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
        Friar friar = new Friar();
        initializeCharacterCardOnGameModel(friar);

        // gets students on friar
        assertTrue(friar.containsStudents());
        EnumMap<StudentColor, Integer> initialStudents = friar.getStudents();
        List<StudentColor> students = new LinkedList<>();
        for (StudentColor s: initialStudents.keySet()) {
            students.addAll(Collections.nCopies(initialStudents.get(s), s));
        }
        assertEquals(4, students.size());

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        LinkedList<Integer> second = new LinkedList<>();
        StudentColor s = students.get(0);
        pairs.add(new Pair<>(s, second));
        
        testEffectForNullAndEmptyPair(friar);
        testEffectForNullAndEmptyList(friar, s);
        // test only one index
        second.add(0);
        assertFalse(friar.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, friar.getStudents());
        second.clear();
        // test wrong island group index
        second.add(-1);
        second.add(0);
        assertFalse(friar.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, friar.getStudents());
        second.clear();
        // test wrong island index
        second.add(0);
        second.add(-1);
        assertFalse(friar.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialStudents, friar.getStudents());
        second.clear();
        // test with correct parameters
        int initial = gme.model.islandsManager.getIslandGroup(0).getIslands().get(0).getNumStudents(s);
        second.add(0);
        second.add(0);
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
        Harvester harvester = new Harvester();
        initializeCharacterCardOnGameModel(harvester);

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        
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
        Herald herald = new Herald();
        initializeCharacterCardOnGameModel(herald);
        
        testEffectForNullAndEmptyList(herald);

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        LinkedList<Integer> second = new LinkedList<>();
        pairs.add(new Pair<>(null, second));
        // test wrong island group index
        second.add(13);
        assertFalse(herald.applyEffect(gme, pairs));
        second.clear();
        // test normal effect
        second.add(0);
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
        
        testEffectForNullAndEmptyList(herbalist);

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        LinkedList<Integer> second = new LinkedList<>();
        pairs.add(new Pair<>(null, second));
        // test wrong island group index
        second.add(13);
        assertFalse(herbalist.applyEffect(gme, pairs));
        second.clear();
        // test normal effect
        for (int i = 0; i < 4; i++) {
            gme.model.islandsManager.getIslandGroup(0).setBlocked(false);
            second.add(0);
            assertTrue(herbalist.applyEffect(gme, pairs));
            assertTrue(gme.model.islandsManager.getIslandGroup(0).isBlocked());
            assertEquals(i + 1, herbalist.getAdditionalCost());
            second.clear();
        }
        // test already blocked
        second.add(0);
        assertFalse(herbalist.applyEffect(gme, pairs));
        second.clear();
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
        Jester jester = new Jester();
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
        testEffectForNullAndEmptyList(jester, s);

        //TODO
        
    }

    /**
     * @return a random student color.
     */
    StudentColor getRandomStudentColor() {
        StudentColor[] values = StudentColor.values();
        return values[ThreadLocalRandom.current().nextInt(0, values.length)];
    }

    /**
     * Tests Minstrel's effect.
     */
    @Test
    void minstrelEffectTest() {
        Minstrel minstrel = new Minstrel();
        initializeCharacterCardOnGameModel(minstrel);

        SchoolBoard sb = gme.model.playersManager.getSchoolBoard();
        for (StudentColor s: StudentColor.values())
            sb.tryRemoveFromHall(s, 10);

        sb.addToHall(StudentColor.BLUE);
        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();
        LinkedList<Integer> second = new LinkedList<>();

        testEffectForNullAndEmptyPair(minstrel);
        testEffectForNullAndEmptyList(minstrel, StudentColor.BLUE);

        for (int i = 0; i < sb.getEntranceCapacity(); i++) {
            sb.removeFromEntrance(i);
            //sb.addToEntrance(getRandomStudentColor(), i);
        }
        sb.addToEntrance(StudentColor.GREEN);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.RED);
        sb.addToEntrance(StudentColor.BLUE);
        sb.addToEntrance(StudentColor.YELLOW);

        // test invalid student in hall
        second.add(0);
        pairs.add(new Pair<>(StudentColor.RED, second));
        assertFalse(minstrel.applyEffect(gme, pairs));
        pairs.clear();
        second.clear();

        // test invalid entrance index
        second.add(5);
        pairs.add(new Pair<>(StudentColor.BLUE, second));
        assertFalse(minstrel.applyEffect(gme, pairs));
        pairs.clear();
        second.clear();


        EnumMap<StudentColor, Integer> initialHall = gme.getHall();
        // test over max size
        pairs.add(new Pair<>(StudentColor.BLUE, second));
        pairs.add(new Pair<>(StudentColor.BLUE, second));
        pairs.add(new Pair<>(StudentColor.BLUE, second));
        assertFalse(minstrel.applyEffect(gme, pairs));
        checkStudentsAreEqual(initialHall, gme.getHall());
        pairs.clear();
        // test invalid second move
        //TODO
    }

    /**
     * Tests Knight's effect.
     */
    @Test
    void knightEffectTest() {
        Knight knight = new Knight();
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
        Mailman mailman = new Mailman();
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
        Princess princess = new Princess();
        initializeCharacterCardOnGameModel(princess);

        testEffectForNullAndEmptyPair(princess);

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();

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
        Thief thief = new Thief();
        initializeCharacterCardOnGameModel(thief);

        testEffectForNullAndEmptyPair(thief);

        LinkedPairList<StudentColor, List<Integer>> pairs = new LinkedPairList<>();

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