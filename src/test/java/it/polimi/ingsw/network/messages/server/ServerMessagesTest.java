package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests server messages.
 */
class ServerMessagesTest {

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.server.ChooseCloud} message.
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
     * Tests the {@link it.polimi.ingsw.network.messages.server.ChooseIsland} message.
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
     * Tests the {@link it.polimi.ingsw.network.messages.server.ChooseStudentColor} message.
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
     * Tests the {@link it.polimi.ingsw.network.messages.server.MoveMotherNature} message.
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
     * Tests the {@link it.polimi.ingsw.network.messages.server.PlayAssistantCard} message.
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
}
