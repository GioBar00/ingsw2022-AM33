package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * data structure that represents a Character Card
 */
public class CharacterCardView implements Serializable {
    /**
     * CharacterType of the card
     */
    private final CharacterType type;
    /**
     * boolean attribute which is true if the destination player can use the card, false otherwise
     */
    private final boolean canBeUsed;
    /**
     * original cost of the card
     */
    private final int originalCost;
    /**
     * additional cost of the card
     */
    private final int additionalCost;
    /**
     * number of blocks on the card (for type Herbalist)
     */
    private final int numBlocks;
    /**
     * students on the card (for the types that have them)
     */
    private final EnumMap<StudentColor, Integer> student;
    /**
     * boolean attribute which is true while the player is using the card (making choices, selecting effects,...)
     */
    private final boolean isActivating;

    /**
     * boolean attribute which is true if the player can end the effect of the card, false otherwise.
     */
    private final boolean canEndEffect;

    /**
     * Constructor
     *
     * @param type           CharacterType of the card.
     * @param canBeUsed      boolean attribute which is true if the destination player can use the card, false otherwise.
     * @param originalCost   original cost of the card.
     * @param additionalCost additional cost of the card.
     * @param numBlocks      number of blocks on the card (for type Herbalist).
     * @param students       students on the card (for the types that have them).
     * @param isActivating   boolean attribute which is true while the player is using the card (making choices, selecting effects,...)
     * @param canEndEffect   boolean attribute which is true if the player can end the effect of the card, false otherwise.
     */
    public CharacterCardView(CharacterType type, boolean canBeUsed, int originalCost, int additionalCost, int numBlocks, EnumMap<StudentColor, Integer> students, boolean isActivating, boolean canEndEffect) {
        this.type = type;
        this.canBeUsed = canBeUsed;
        this.originalCost = originalCost;
        this.additionalCost = additionalCost;
        this.numBlocks = numBlocks;
        this.student = students;
        this.isActivating = isActivating;
        this.canEndEffect = canEndEffect;
    }

    /**
     * @return the type of the card
     */
    public CharacterType getType() {
        return type;
    }

    /**
     * @return the attribute canBeUsed
     */
    public boolean canBeUsed() {
        return canBeUsed;
    }

    /**
     * @return the original cost of the card
     */
    public int getOriginalCost() {
        return originalCost;
    }

    /**
     * @return the additional cost of the card
     */
    public int getAdditionalCost() {
        return additionalCost;
    }

    /**
     * @return the number of blocks currently on the card
     */
    public int getNumBlocks() {
        return numBlocks;
    }

    /**
     * @return the students on the card
     */
    public EnumMap<StudentColor, Integer> getStudent() {
        return student;
    }

    /**
     * @return the attribute isActivating
     */
    public boolean isActivating() {
        return isActivating;
    }

    /**
     * @return the attribute canEndEffect
     */
    public boolean canEndEffect() {
        return canEndEffect;
    }
}
