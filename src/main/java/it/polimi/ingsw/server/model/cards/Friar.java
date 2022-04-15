package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.MoveStudent;

import java.util.*;

/**
 * Friar character card.
 */
public class Friar extends CharacterCard {

    /**
     * Students on the card.
     */
    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    /**
     * Creates the friar.
     */
    public Friar() {
        super(CharacterType.FRIAR, 1, 1);
        for (StudentColor s: StudentColor.values())
            students.put(s, 0);
    }

    /**
     * Initializes the character card. It gets 4 students from the bag.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void initialize(EffectHandler effectHandler) {
        for (int i = 0; i < 4; i++) {
            StudentColor s = effectHandler.getStudentFromBag();
            students.put(s, students.get(s) + 1);
        }
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Gets one student from the card, adds it on the chosen island and then gets one student from the bag.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            Integer islandGroupIndex = parameters.getIndex();
            if (s != null && students.get(s) > 0 && islandGroupIndex != null) {
                if (effectHandler.addStudentToIsland(s, islandGroupIndex)) {
                    students.put(s, students.get(s) - 1);
                    s = effectHandler.getStudentFromBag();
                    students.put(s, students.get(s) + 1);
                    currentChoicesNumber++;
                    endEffect();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return move student message from card to island.
     */
    @Override
    public Message getCommandMessage(EffectHandler effectHandler) {
        Set<Integer> availableStudents = CharacterCard.getAvailableStudentsOrdinal(students);
        Set<Integer> islandIndexes = effectHandler.getAvailableIslandIndexes();
        return new MoveStudent(MoveLocation.CARD, availableStudents, MoveLocation.ISLAND, islandIndexes);
    }

    /**
     * @return if it contains students.
     */
    @Override
    public boolean containsStudents() {
        return true;
    }

    /**
     * @return the students on the card
     */
    @Override
    public EnumMap<StudentColor, Integer> getStudents() {
        return new EnumMap<>(students);
    }
}
