package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.EnumMap;

/**
 * Farmer character card.
 */
public class Farmer extends CharacterCard {

    /**
     * Map of the professors to the corresponding original player index.
     */
    private EnumMap<StudentColor, Integer> original;

    /**
     * Creates the farmer.
     */
    public Farmer() {
        super(CharacterType.FARMER, 2);
    }

    /**
     * Applies the effect of the character card. Tries to give the professors to the current player if the students in the hall is equal.
     *
     * @param effectHandler handler for the effects.
     * @param parameters    for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect) {
            original = effectHandler.tryGiveProfsToCurrPlayer();
            endEffect();
            return true;
        }
        return false;
    }

    /**
     * Add to the record of original professors the player who owned it
     *
     * @param s      the professor
     * @param player the owner
     */
    @Override
    public void addToOriginal(StudentColor s, Integer player) {
        original.put(s, player);
    }

    /**
     * Reverts the effect of the character card.
     *
     * @param effectHandler handler for the effects.
     */
    @Override
    public void revertEffect(EffectHandler effectHandler) {
        if (appliedEffect) {
            effectHandler.restoreProfsToOriginalPlayer(original);
            appliedEffect = false;
        }
    }

    /**
     * @return if the card can store a history of swapped professors.
     */
    @Override
    public boolean canHandleHistory() {
        return true;
    }
}
