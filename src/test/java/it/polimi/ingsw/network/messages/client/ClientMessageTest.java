package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the client messages.
 */
class ClientMessageTest {

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ActivatedCharacterCard} message.
     */
    @Test
    void activatedCharacterCard() {
        ActivatedCharacterCard original = new ActivatedCharacterCard(1);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ActivatedCharacterCard);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getCharacterCardIndex(), ((ActivatedCharacterCard)m).getCharacterCardIndex());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ChosenCloud} message.
     */
    @Test
    void chosenCloud() {
        ChosenCloud original = new ChosenCloud(5);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChosenCloud);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getCloudIndex(), ((ChosenCloud)m).getCloudIndex());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ChosenGame} message.
     */
    @Test
    void chosenGame() {
        ChosenGame original = new ChosenGame(GamePreset.THREE, GameMode.EXPERT);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChosenGame);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getPreset(), ((ChosenGame)m).getPreset());
        assertEquals(original.getMode(), ((ChosenGame)m).getMode());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ChosenIsland} message.
     */
    @Test
    void chosenIsland() {
        ChosenIsland original = new ChosenIsland(5);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChosenIsland);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getIslandIndex(), ((ChosenIsland)m).getIslandIndex());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ChosenStudentColor} message.
     */
    @Test
    void chosenStudentColor() {
        ChosenStudentColor original = new ChosenStudentColor(StudentColor.RED);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChosenStudentColor);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getStudentColor(), ((ChosenStudentColor)m).getStudentColor());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ChosenTeam} message.
     */
    @Test
    void chosenTeam() {
        ChosenTeam original = new ChosenTeam(Tower.BLACK);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ChosenTeam);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getTower(), ((ChosenTeam)m).getTower());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.ConcludeCharacterCardEffect} message.
     */
    @Test
    void concludeCharacterCardEffect() {
        ConcludeCharacterCardEffect original = new ConcludeCharacterCardEffect();
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof ConcludeCharacterCardEffect);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.Login} message.
     */
    @Test
    void login() {
        Login original = new Login("test");
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof Login);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getNickname(), ((Login)m).getNickname());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.MovedMotherNature} message.
     */
    @Test
    void movedMotherNature() {
        MovedMotherNature original = new MovedMotherNature(5);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof MovedMotherNature);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getNumMoves(), ((MovedMotherNature)m).getNumMoves());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.MovedStudent} message.
     */
    @Test
    void movedStudent() {
        MovedStudent original = new MovedStudent(MoveLocation.CARD, 2, MoveLocation.HALL, null);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof MovedStudent);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getFrom(), ((MovedStudent)m).getFrom());
        assertEquals(original.getFromIndex(), ((MovedStudent)m).getFromIndex());
        assertEquals(original.getTo(), ((MovedStudent)m).getTo());
        assertEquals(original.getToIndex(), ((MovedStudent)m).getToIndex());
        // invalid from
        original = new MovedStudent(null, 2, MoveLocation.HALL, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid to
        original = new MovedStudent(MoveLocation.CARD, 2, null, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid from index.
        original = new MovedStudent(MoveLocation.CARD, null, MoveLocation.HALL, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid to index.
        original = new MovedStudent(MoveLocation.CARD, 2, MoveLocation.ISLAND, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid StudentColor ordinal.
        original = new MovedStudent(MoveLocation.CARD, 10, MoveLocation.HALL, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.PlayedAssistantCard} message.
     */
    @Test
    void playedAssistantCard() {
        PlayedAssistantCard original = new PlayedAssistantCard(AssistantCard.FIVE);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof PlayedAssistantCard);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getAssistantCard(), ((PlayedAssistantCard)m).getAssistantCard());
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.StartGame} message.
     */
    @Test
    void startGame() {
        StartGame original = new StartGame();
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof StartGame);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
    }

    /**
     * Tests the {@link it.polimi.ingsw.network.messages.client.SwappedStudents} message.
     */
    @Test
    void swappedStudents() {
        SwappedStudents original = new SwappedStudents(MoveLocation.ENTRANCE, 2, MoveLocation.HALL, 2);
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof SwappedStudents);
        assertEquals(MessageType.retrieveByMessageClass(original), MessageType.retrieveByMessageClass(m));
        assertEquals(original.getFrom(), ((SwappedStudents)m).getFrom());
        assertEquals(original.getFromIndex(), ((SwappedStudents)m).getFromIndex());
        assertEquals(original.getTo(), ((SwappedStudents)m).getTo());
        assertEquals(original.getToIndex(), ((SwappedStudents)m).getToIndex());
        // invalid from
        original = new SwappedStudents(null, 2, MoveLocation.HALL, 2);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid to
        original = new SwappedStudents(MoveLocation.CARD, 2, null, 2);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid from index.
        original = new SwappedStudents(MoveLocation.CARD, null, MoveLocation.HALL, 2);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid to index.
        original = new SwappedStudents(MoveLocation.CARD, 2, MoveLocation.ISLAND, null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid from StudentColor ordinal.
        original = new SwappedStudents(MoveLocation.CARD, 10, MoveLocation.ENTRANCE, 2);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // invalid to StudentColor ordinal.
        original = new SwappedStudents(MoveLocation.CARD, 1, MoveLocation.HALL, 10);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
    }
}
