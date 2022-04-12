package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.MoveStudent;

import java.util.EnumMap;
import java.util.Set;

/**
 * Princess character card.
 */
public class Princess extends CharacterCard {

    /**
     * Students on the card.
     */
    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    /**
     * Creates princess.
     */
    public Princess() {
        super(CharacterType.PRINCESS, 2, 1);
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
     * Gets one student from the card, adds it to the hall and then gets one student from the bag.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            if (s != null && students.get(s) > 0) {
                if (effectHandler.addStudentToHall(s)) {
                    students.put(s, students.get(s) - 1);
                    s = effectHandler.getStudentFromBag();
                    if (s != null)
                        students.put(s, students.get(s) + 1);
                    currentChoicesNumber++;
                    endEffect();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return move student message from card to hall.
     */
    @Override
    public Message getCommandMessage(EffectHandler effectHandler) {
        Set<Integer> availableStudents = CharacterCard.getAvailableStudentsOrdinal(students);
        EnumMap<StudentColor, Integer> students = effectHandler.getHall();
        availableStudents.removeIf(i -> students.get(StudentColor.retrieveStudentColorByOrdinal(i)) >= 10);
        return new MoveStudent(MoveLocation.CARD, availableStudents, MoveLocation.HALL, null);
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
