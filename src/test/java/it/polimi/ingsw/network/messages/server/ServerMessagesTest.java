package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests server messages.
 */
class ServerMessagesTest {

    /**
     * Tests the {@link ChooseCloud} message.
     */
    @Test
    void chooseCloud() {
        Set<Integer> indexes = new HashSet<>();
        indexes.add(0);
        indexes.add(1);
        ChooseCloud original = new ChooseCloud(indexes);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChooseCloud);
        assertEquals(original.getAvailableCloudIndexes(), ((ChooseCloud) m).getAvailableCloudIndexes());
        // test null indexes
        original = new ChooseCloud(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty indexes
        original = new ChooseCloud(new HashSet<>());
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link ChooseIsland} message.
     */
    @Test
    void chooseIsland() {
        Set<Integer> indexes = new HashSet<>();
        indexes.add(0);
        indexes.add(1);
        ChooseIsland original = new ChooseIsland(indexes);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChooseIsland);
        assertEquals(original.getAvailableIslandIndexes(), ((ChooseIsland) m).getAvailableIslandIndexes());
        // test null indexes
        original = new ChooseIsland(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty indexes
        original = new ChooseIsland(new HashSet<>());
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link ChooseStudentColor} message.
     */
    @Test
    void chooseStudentColor() {
        EnumSet<StudentColor> students = EnumSet.of(StudentColor.BLUE, StudentColor.PINK);
        ChooseStudentColor original = new ChooseStudentColor(students);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChooseStudentColor);
        assertEquals(original.getAvailableStudentColors(), ((ChooseStudentColor) m).getAvailableStudentColors());
        // test null students
        original = new ChooseStudentColor(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty students
        original = new ChooseStudentColor(EnumSet.noneOf(StudentColor.class));
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.server.CommMessage} message.
     */
    @Test
    void communicationMessage() {
        CommMessage original = new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof CommMessage);
        assertEquals(original.getType(), ((CommMessage) m).getType());
        // test null type
        original = new CommMessage(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link MoveMotherNature} message.
     */
    @Test
    void moveMotherNature() {
        MoveMotherNature original = new MoveMotherNature(5);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof MoveMotherNature);
        assertEquals(original.getMaxNumMoves(), ((MoveMotherNature) m).getMaxNumMoves());
        // test null maxNumMoves
        original = new MoveMotherNature(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link PlayAssistantCard} message.
     */
    @Test
    void playAssistantCard() {
        EnumSet<AssistantCard> cards = EnumSet.of(AssistantCard.ONE, AssistantCard.THREE);
        PlayAssistantCard original = new PlayAssistantCard(cards);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof PlayAssistantCard);
        assertEquals(original.getPlayableAssistantCards(), ((PlayAssistantCard) m).getPlayableAssistantCards());
        // test null cards
        original = new PlayAssistantCard(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty cards
        original = new PlayAssistantCard(EnumSet.noneOf(AssistantCard.class));
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link MoveStudent} message.
     */
    @Test
    void moveStudent() {
        Set<Integer> fromIndexes = new HashSet<>();
        fromIndexes.add(1);
        fromIndexes.add(2);
        Set<Integer> toIndexes = new HashSet<>();
        toIndexes.add(3);
        toIndexes.add(4);
        MoveStudent original = new MoveStudent(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, toIndexes);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof MoveStudent);
        assertEquals(original.getFrom(), ((MoveStudent) m).getFrom());
        assertEquals(original.getTo(), ((MoveStudent) m).getTo());
        assertEquals(original.getFromIndexesSet(), ((MoveStudent) m).getFromIndexesSet());
        assertEquals(original.getToIndexesSet(), ((MoveStudent) m).getToIndexesSet());
        // test null from
        original = new MoveStudent(null, fromIndexes, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null to
        original = new MoveStudent(MoveLocation.CARD, fromIndexes, null, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null fromIndexes
        original = new MoveStudent(MoveLocation.CARD, null, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null toIndexes
        original = new MoveStudent(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty fromIndexes
        original = new MoveStudent(MoveLocation.CARD, new HashSet<>(), MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty toIndexes
        original = new MoveStudent(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, new HashSet<>());
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test valid null toIndexes
        original = new MoveStudent(MoveLocation.CARD, fromIndexes, MoveLocation.HALL, null);
        m = toAndFromJson(original);
        assertTrue(m.isValid());
        // test invalid students
        fromIndexes.add(5);
        original = new MoveStudent(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link SwapStudents} message.
     */
    @Test
    void swapStudents() {
        Set<Integer> fromIndexes = new HashSet<>();
        fromIndexes.add(1);
        fromIndexes.add(2);
        Set<Integer> toIndexes = new HashSet<>();
        toIndexes.add(3);
        toIndexes.add(4);
        SwapStudents original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, toIndexes);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof SwapStudents);
        assertEquals(original.getFrom(), ((SwapStudents) m).getFrom());
        assertEquals(original.getTo(), ((SwapStudents) m).getTo());
        assertEquals(original.getFromIndexesSet(), ((SwapStudents) m).getFromIndexesSet());
        assertEquals(original.getToIndexesSet(), ((SwapStudents) m).getToIndexesSet());
        // test null from
        original = new SwapStudents(null, fromIndexes, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null to
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, null, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null fromIndexes
        original = new SwapStudents(MoveLocation.CARD, null, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test null toIndexes
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty fromIndexes
        original = new SwapStudents(MoveLocation.CARD, new HashSet<>(), MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty toIndexes
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, new HashSet<>());
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test invalid from students
        fromIndexes.add(5);
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test invalid to students
        fromIndexes.remove(5);
        toIndexes.add(5);
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.ISLAND, toIndexes);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test required toIndexes for HALL
        original = new SwapStudents(MoveLocation.CARD, fromIndexes, MoveLocation.HALL, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link MultiplePossibleMoves} message.
     */
    @Test
    void multiplePossibleMoves() {
        List<MoveActionRequest> moves = new LinkedList<>();
        Set<Integer> entranceIndexes = new HashSet<>();
        entranceIndexes.add(1);
        entranceIndexes.add(5);
        Set<Integer> islandIndexes = new HashSet<>();
        islandIndexes.add(2);
        islandIndexes.add(3);
        moves.add(new MoveStudent(MoveLocation.ENTRANCE, entranceIndexes, MoveLocation.ISLAND, islandIndexes));
        moves.add(new MoveStudent(MoveLocation.ENTRANCE, entranceIndexes, MoveLocation.HALL, null));
        MultiplePossibleMoves original = new MultiplePossibleMoves(moves);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        // test equals original moves
        for (MoveActionRequest move : ((MultiplePossibleMoves) m).getPossibleMoves()) {
            MoveStudent moveStudent = (MoveStudent) move;
            boolean found = false;
            for (MoveActionRequest originalMove : moves) {
                MoveStudent originalMoveStudent = (MoveStudent) originalMove;
                if (moveStudent.getFrom().equals(originalMoveStudent.getFrom()) &&
                        moveStudent.getTo().equals(originalMoveStudent.getTo()) &&
                        moveStudent.getFromIndexesSet().equals(originalMoveStudent.getFromIndexesSet())){
                    if (moveStudent.getToIndexesSet() == null || moveStudent.getToIndexesSet().equals(originalMoveStudent.getToIndexesSet())) {
                        found = true;
                        moves.remove(originalMove);
                        break;
                    }
                }
            }
            assertTrue(found);
        }
        // test null moves
        original = new MultiplePossibleMoves(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test empty moves
        original = new MultiplePossibleMoves(new LinkedList<>());
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // test invalid moves
        moves.add(new MoveStudent(MoveLocation.ENTRANCE, null, MoveLocation.ISLAND, islandIndexes));
        original = new MultiplePossibleMoves(moves);
        m = toAndFromJson(original);
        assertFalse(m.isValid());

    }
}
