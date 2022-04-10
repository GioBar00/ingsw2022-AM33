package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumMap;
import java.util.List;

/**
 * Abstract class of a character card.
 */
public abstract class CharacterCard {
    /**
     * type of the card.
     */
    final CharacterType type;
    /**
     * cost of the card.
     */
    final int cost;
    /**
     * additional cost of the card.
     */
    int additionalCost = 0;
    /**
     * if the effect of the card was applied.
     */
    boolean appliedEffect = false;

    /**
     * Constructs the card.
     * @param type of the card.
     * @param cost of the card.
     */
    protected CharacterCard(CharacterType type, int cost) {
        this.type = type;
        this.cost = cost;
    }

    /**
     * @return cost.
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return additional cost.
     */
    public int getAdditionalCost() {
        return additionalCost;
    }

    /**
     * @return total cost.
     */
    public int getTotalCost(){
        return cost + additionalCost;
    }

    /**
     * Initializes the character card.
     * @param effectHandler handler for the effects.
     */
    public void initialize(EffectHandler effectHandler) {}

    /**
     * Applies the effect of the character card.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    public abstract boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs);

    /**
     * Ends the effect of the character card.
     * @param effectHandler handler for the effects.
     */
    public void endEffect(EffectHandler effectHandler) {
        if (appliedEffect)
            appliedEffect = false;
    }

    /**
     * @return if it can handle blocks.
     */
    public boolean canHandleBlocks(){
        return false;
    }

    /**
     * Adds number of blocks back to the card.
     * @param num of blocks to add.
     */
    public void addNumBlocks(int num) {}

    /**
     * @return if it contains students.
     */
    public boolean containsStudents() {
        return false;
    }

    /**
     * @return the students on the card
     */
    public EnumMap<StudentColor, Integer> getStudents() {
        return new EnumMap<>(StudentColor.class);
    }

    /**
     * Checks if the parameters are correct for "move effects" with multiple choices.
     * @param effectHandler handler for the effects.
     * @param pairs parameters.
     * @param maxSize of the parameters.
     * @param copy of the students to modify.
     * @return if the moves are valid.
     */
    boolean areMovesValid(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs, int maxSize, EnumMap<StudentColor, Integer> copy) {
        if (pairs == null || pairs.size() == 0 || pairs.size() > maxSize)
            return false;
        List<StudentColor> entrance = effectHandler.getStudentsInEntrance();

        for (Pair<StudentColor, Integer> pair: pairs) {
            StudentColor s = pair.getFirst();
            if (s == null || copy.get(s) <= 0)
                return false;
            Integer index = pair.getSecond();
            if (index == null || index < 0 || index >= entrance.size())
                return false;
            StudentColor onEntrance = entrance.get(index);
            if (onEntrance == null)
                return false;
            entrance.set(index, s);
            copy.put(s, copy.get(s) - 1);
            copy.put(onEntrance, copy.get(onEntrance) + 1);
        }
        return true;
    }

}
